package com.xlerp.api.ItemManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.Basitem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasItemService {
    private static final Basitem dao = new Basitem();

    public Page<Basitem> paginate(int pageNumber, int pageSize, String itemNo, String itemName, String firstClassId, String secondClassId) {
        // 核心：关联basitem和bas_item_class（物料的classId对应三级分类ID）
        // 思路：通过三级分类找二级分类，再通过二级分类找一级分类
        String select = "select b.*"; // 只查询物料表字段
        StringBuilder from = new StringBuilder(
                "from basitem b " +
                        "left join bas_item_class c3 on b.classId = c3.id " + // 物料-三级分类
                        "left join bas_item_class c2 on c3.parentId = c2.id " + // 三级-二级分类
                        "left join bas_item_class c1 on c2.parentId = c1.id " + // 二级-一级分类
                        "where b.isdelete = 0 "
        );

        List<Object> params = new ArrayList<>();

        // 物料编号筛选
        if (StrKit.notBlank(itemNo)) {
            from.append("and b.no like ? ");
            params.add("%" + itemNo + "%");
        }

        // 物料名称筛选
        if (StrKit.notBlank(itemName)) {
            from.append("and b.name like ? ");
            params.add("%" + itemName + "%");
        }

        // 一级分类筛选（匹配一级分类ID）
        if (StrKit.notBlank(firstClassId)) {
            from.append("and c1.id = ? "); // c1是一级分类表别名
            params.add(firstClassId);
        }

        // 二级分类筛选（匹配二级分类ID）
        if (StrKit.notBlank(secondClassId)) {
            from.append("and c2.id = ? "); // c2是二级分类表别名
            params.add(secondClassId);
        }

        // 排序
        from.append("order by b.id desc");

        // 执行分页查询（注意：select和from要分开传，dao.paginate会自动处理count）
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Page<Basitem> tuzhiyuancailiaopaginate(int pageNumber, int pageSize, String itemNo, String itemName, String inclass,  String type) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from basitem where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(itemNo)) {
            from.append(" and no like ?");
        }
        if (StrKit.notBlank(itemName)) {
            from.append(" and name like ?");
        }
        if (StrKit.notBlank(inclass)) {
            from.append(" and inclass like ?");
        }
        if (StrKit.notBlank(type)) {
            from.append(" and type like ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(itemNo)) {
            params.add("%" + itemNo + "%");
        }
        if (StrKit.notBlank(itemName)) {
            params.add("%" + itemName + "%");
        }
        if (StrKit.notBlank(inclass)) {
            params.add("%" + inclass + "%");
        }
        if (StrKit.notBlank(type)) {
            params.add("%" + type + "%");
        }

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Basitem findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Basitem basItem) {
        return basItem.save();
    }

    public boolean update(Basitem basItem) {
        return basItem.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }




    /**
     * 解析Excel文件并导入Basitem数据
     * @param excelFile Excel文件
     * @return 包含导入结果的Map，包含成功数量、失败行信息、失败数量和总行数
     * @throws Exception 文件处理或数据库操作异常
     */
    public Map<String, Object> parseBasitemExcel(File excelFile) throws Exception {
        List<Map<String, Object>> failedRows = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;
        Workbook workbook = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(excelFile);
            if (excelFile.getName().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (excelFile.getName().endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("不支持的文件格式，仅支持 .xls 或 .xlsx");
            }

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel文件第一行不能为空（需包含表头）");
            }

            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String header = getCellValue(headerRow.getCell(i));
                if (header != null && !header.isEmpty()) {
                    headerMap.put(header.trim(), i);
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                totalRows++;

                Map<String, Object> failureInfo = new HashMap<>();
                Map<String, Object> rowData = new HashMap<>();
                failureInfo.put("rowNumber", i + 1);

                // 收集整行数据
                rowData.put("index", i + 1);
                rowData.put("itemNo", getCellValue(row.getCell(headerMap.get("物料编号"))));
                rowData.put("itemName", getCellValue(row.getCell(headerMap.get("物料名称"))));
                rowData.put("itemUnit", getCellValue(row.getCell(headerMap.get("计量单位"))));
                rowData.put("itemType", getCellValue(row.getCell(headerMap.get("物料类型"))));
                rowData.put("inclass", getCellValue(row.getCell(headerMap.get("所属分类"))));
                rowData.put("spec", getCellValue(row.getCell(headerMap.get("规格型号"))));
                rowData.put("description", getCellValue(row.getCell(headerMap.get("物料描述"))));
                rowData.put("color", getCellValue(row.getCell(headerMap.get("颜色"))));
                rowData.put("location", getCellValue(row.getCell(headerMap.get("存放位置"))));
                rowData.put("techMemo", getCellValue(row.getCell(headerMap.get("技术参数"))));
                rowData.put("memo", getCellValue(row.getCell(headerMap.get("备注信息"))));
                rowData.put("weight", getCellValue(row.getCell(headerMap.get("重量"))));
                rowData.put("plannedPrice", getCellValue(row.getCell(headerMap.get("计划价格"))));
                rowData.put("avgPrice", getCellValue(row.getCell(headerMap.get("平均价格"))));

                failureInfo.put("rowData", rowData);

                try {
                    Basitem item = new Basitem();

                    // 设置字符串字段
                    String itemNo = getCellValue(row.getCell(headerMap.get("物料编号")));
                    item.set("no", itemNo);
                    item.set("name", getCellValue(row.getCell(headerMap.get("物料名称"))));
                    item.set("unit", getCellValue(row.getCell(headerMap.get("计量单位"))));
                    item.set("type", getCellValue(row.getCell(headerMap.get("物料类型"))));
                    item.set("inclass", getCellValue(row.getCell(headerMap.get("所属分类"))));
                    item.set("spec", getCellValue(row.getCell(headerMap.get("规格型号"))));
                    item.set("description", getCellValue(row.getCell(headerMap.get("物料描述"))));
                    item.set("color", getCellValue(row.getCell(headerMap.get("颜色"))));
                    item.set("location", getCellValue(row.getCell(headerMap.get("存放位置"))));
                    item.set("tech_memo", getCellValue(row.getCell(headerMap.get("技术参数"))));
                    item.set("memo", getCellValue(row.getCell(headerMap.get("备注信息"))));
                    item.setIsdelete(0);

                    // 设置数值字段，使用BigDecimal以匹配DECIMAL(20,2)
                    try {
                        String weightStr = getCellValue(row.getCell(headerMap.get("重量")));
                        item.set("weight", weightStr.isEmpty() ? null : new BigDecimal(weightStr));
                        String plannedPriceStr = getCellValue(row.getCell(headerMap.get("计划价格")));
                        item.set("planned_price", plannedPriceStr.isEmpty() ? null : new BigDecimal(plannedPriceStr));
                        String avgPriceStr = getCellValue(row.getCell(headerMap.get("平均价格")));
                        item.set("avg_price", avgPriceStr.isEmpty() ? null : new BigDecimal(avgPriceStr));
                    } catch (NumberFormatException e) {
                        failureInfo.put("error", "数值字段格式错误: " + e.getMessage());
                        failedRows.add(failureInfo);
                        continue;
                    }

                    // 检查物料编号是否为空
                    if (itemNo == null || itemNo.trim().isEmpty()) {
                        failureInfo.put("error", "物料编号不能为空");
                        failedRows.add(failureInfo);
                        continue;
                    }

                    // 检查物料编号是否已存在
                    Basitem existingItem = dao.findFirst("SELECT * FROM basitem WHERE no = ? and isdelete = 0", itemNo);
                    if (existingItem != null) {
                        failureInfo.put("error", "物料编号 " + itemNo + " 已存在");
                        failedRows.add(failureInfo);
                        continue;
                    }

                    if (item.save()) {
                        successCount++;
                        System.out.println("保存成功: 行 " + (i + 1));
                    } else {
                        failureInfo.put("error", "保存失败，数据库操作错误");
                        failedRows.add(failureInfo);
                    }
                } catch (Exception e) {
                    failureInfo.put("error", "解析错误: " + e.getMessage());
                    failedRows.add(failureInfo);
                }
            }
        } finally {
            if (workbook != null) workbook.close();
            if (fis != null) fis.close();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failedRows", failedRows);
        result.put("failedCount", failedRows.size());
        result.put("totalRows", totalRows);
        return result;
    }



    /**
     * 获取物料的完整子物料树形结构
     */
    public List<Map<String, Object>> getItemMaterialTree(int itemId) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 查询当前物料信息
        Basitem currentItem = findById(itemId);
        if (currentItem == null) {
            return result;
        }

        // 构建根节点
        Map<String, Object> rootNode = new HashMap<>();
        rootNode.put("id", currentItem.getId());
        rootNode.put("no", currentItem.getStr("no"));
        rootNode.put("name", currentItem.getStr("name"));
        rootNode.put("unit", currentItem.getStr("unit"));
        rootNode.put("spec", currentItem.getStr("spec"));
        rootNode.put("type", currentItem.getInt("type"));
        rootNode.put("inclass", currentItem.getStr("inclass"));
        rootNode.put("weight", currentItem.getBigDecimal("weight"));
        rootNode.put("planned_price", currentItem.getBigDecimal("planned_price"));
        rootNode.put("avg_price", currentItem.getBigDecimal("avg_price"));
        rootNode.put("level", 0);
        rootNode.put("isRoot", true);
        rootNode.put("quantity", BigDecimal.ONE); // 根节点数量为1
        rootNode.put("children", new ArrayList<Map<String, Object>>());

        // 递归获取子物料
        buildMaterialTree(rootNode, 1);

        result.add(rootNode);
        return result;
    }

    /**
     * 递归构建物料树
     */
    private void buildMaterialTree(Map<String, Object> parentNode, int level) {
        int parentItemId = (int) parentNode.get("id");
        BigDecimal parentQuantity = (BigDecimal) parentNode.get("quantity");

        // 查询直接子物料
        String sql = "SELECT r.*, i.*, r.quantity as relation_quantity, r.id as relation_id " +
                "FROM bas_item_relation r " +
                "LEFT JOIN basitem i ON r.childItemId = i.id " +
                "WHERE r.parentItemId = ? AND i.isdelete = 0 " +
                "ORDER BY i.no";

        List<Record> childRecords = Db.find(sql, parentItemId);

        List<Map<String, Object>> children = new ArrayList<>();

        for (Record childRecord : childRecords) {
            Map<String, Object> childNode = new HashMap<>();
            childNode.put("id", childRecord.getInt("id"));
            childNode.put("no", childRecord.getStr("no"));
            childNode.put("name", childRecord.getStr("name"));
            childNode.put("unit", childRecord.getStr("unit"));
            childNode.put("spec", childRecord.getStr("spec"));
            childNode.put("type", childRecord.getInt("type"));
            childNode.put("inclass", childRecord.getStr("inclass"));
            childNode.put("weight", childRecord.getBigDecimal("weight"));
            childNode.put("planned_price", childRecord.getBigDecimal("planned_price"));
            childNode.put("avg_price", childRecord.getBigDecimal("avg_price"));
            childNode.put("relation_quantity", childRecord.getBigDecimal("relation_quantity"));
            childNode.put("relation_id", childRecord.getInt("relation_id"));
            childNode.put("level", level);
            childNode.put("isRoot", false);
            childNode.put("children", new ArrayList<Map<String, Object>>());

            // 计算实际数量 = 父节点数量 × 子节点基础数量
            BigDecimal baseQuantity = childRecord.getBigDecimal("relation_quantity");
            if (baseQuantity == null) {
                baseQuantity = BigDecimal.ONE;
            }
            BigDecimal calculatedQuantity = parentQuantity.multiply(baseQuantity);
            childNode.put("quantity", calculatedQuantity); // 使用统一的quantity字段

            // 递归获取子节点的子物料
            buildMaterialTree(childNode, level + 1);

            children.add(childNode);
        }

        parentNode.put("children", children);
    }

    /**
     * 获取物料的所有层级子物料（平铺列表）
     * @param itemId 物料ID
     * @return 平铺的子物料列表
     */
    public List<Map<String, Object>> getItemAllMaterials(int itemId) {
        List<Map<String, Object>> result = new ArrayList<>();
        getFlatMaterialList(itemId, result, 0);
        return result;
    }

    /**
     * 递归获取平铺的物料列表
     */
    private void getFlatMaterialList(int parentItemId, List<Map<String, Object>> result, int level) {
        String sql = "SELECT r.*, i.*, r.quantity as relation_quantity " +
                "FROM bas_item_relation r " +
                "LEFT JOIN basitem i ON r.childItemId = i.id " +
                "WHERE r.parentItemId = ? AND i.isdelete = 0 " +
                "ORDER BY i.no";

        List<Basitem> childItems = dao.find(sql, parentItemId);

        for (Basitem childItem : childItems) {
            Map<String, Object> material = new HashMap<>();
            material.put("id", childItem.getId());
            material.put("no", childItem.getStr("no"));
            material.put("name", childItem.getStr("name"));
            material.put("unit", childItem.getStr("unit"));
            material.put("spec", childItem.getStr("spec"));
            material.put("type", childItem.getInt("type"));
            material.put("inclass", childItem.getStr("inclass"));
            material.put("weight", childItem.getBigDecimal("weight"));
            material.put("plannedPrice", childItem.getBigDecimal("planned_price"));
            material.put("avgPrice", childItem.getBigDecimal("avg_price"));
            material.put("quantity", childItem.getBigDecimal("relation_quantity"));
            material.put("level", level);
            material.put("materialAttribute", childItem.getStr("material_attribute"));
            material.put("drawingStandardNo", childItem.getStr("drawing_standard_no"));

            result.add(material);

            // 递归获取子节点的子物料
            getFlatMaterialList(childItem.getId(), result, level + 1);
        }
    }

    /**
     * 获取单元格值的辅助方法
     * @param cell 单元格对象
     * @return 单元格值的字符串表示
     */
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.format("%.2f", cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }



    /**
     * 添加物料关系
     */
    public boolean addMaterialRelation(int parentItemId, int childItemId, BigDecimal quantity, String memo) {
        try {
            // 检查关系是否已存在
            Record existingRelation = Db.findFirst(
                    "SELECT * FROM bas_item_relation WHERE parentItemId = ? AND childItemId = ?",
                    parentItemId, childItemId
            );

            if (existingRelation != null) {
                System.out.println("关系已存在，父物料ID：" + parentItemId + "，子物料ID：" + childItemId);
                return false; // 关系已存在
            }

            // 获取物料信息
            Basitem parentItem = findById(parentItemId);
            Basitem childItem = findById(childItemId);

            if (parentItem == null) {
                System.out.println("父物料不存在，ID：" + parentItemId);
                return false;
            }
            if (childItem == null) {
                System.out.println("子物料不存在，ID：" + childItemId);
                return false;
            }

            System.out.println("父物料信息: " + parentItem.getStr("no") + " - " + parentItem.getStr("name"));
            System.out.println("子物料信息: " + childItem.getStr("no") + " - " + childItem.getStr("name"));

            // 插入新关系
            Record relation = new Record();
            relation.set("parentItemId", parentItemId);
            relation.set("childItemId", childItemId);
            relation.set("parentItemType", parentItem.getInt("type"));
            relation.set("childItemType", childItem.getInt("type"));
            relation.set("quantity", quantity != null ? quantity : BigDecimal.ONE);
            relation.set("memo", memo != null ? memo : "");

            boolean saveResult = Db.save("bas_item_relation", relation);
            if (saveResult) {
                System.out.println("成功添加物料关系，父物料ID：" + parentItemId + "，子物料ID：" + childItemId);
                // 打印插入的记录ID
                Record newRelation = Db.findFirst("SELECT * FROM bas_item_relation WHERE parentItemId = ? AND childItemId = ?",
                        parentItemId, childItemId);
                if (newRelation != null) {
                    System.out.println("新关系记录ID: " + newRelation.getInt("id"));
                }
            } else {
                System.out.println("插入数据库失败，父物料ID：" + parentItemId + "，子物料ID：" + childItemId);
            }
            return saveResult;
        } catch (Exception e) {
            System.out.println("添加物料关系时发生异常，父物料ID：" + parentItemId + "，子物料ID：" + childItemId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除物料关系
     */
    public boolean removeMaterialRelation(int relationId) {
        try {
            return Db.deleteById("bas_item_relation", relationId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取物料的子物料列表
     */
    public List<Record> getChildMaterials(int itemId) {
        String sql = "SELECT r.*, i.*, r.quantity as relation_quantity, r.id as relation_id ,r.memo as memo "  +
                "FROM bas_item_relation r " +
                "LEFT JOIN basitem i ON r.childItemId = i.id " +
                "WHERE r.parentItemId = ? AND i.isdelete = 0 " +
                "ORDER BY i.no";

        return Db.find(sql, itemId);
    }

    /**
     * 更新物料关系并重新计算父物料数量
     */
    public boolean updateMaterialRelation(int relationId, BigDecimal quantity, String memo) {
        try {
            // 1. 检查关系是否存在
            Record relation = Db.findById("bas_item_relation", relationId);
            if (relation == null) {
                System.out.println("物料关系不存在，ID：" + relationId);
                return false;
            }

            // 2. 获取父物料ID和子物料ID
            int parentItemId = relation.getInt("parentItemId");
            int childItemId = relation.getInt("childItemId");

            System.out.println("更新关系 - 父物料ID: " + parentItemId + ", 子物料ID: " + childItemId + ", 新数量: " + quantity);

            // 3. 仅更新不为null的字段
            if (quantity != null) {
                relation.set("quantity", quantity);
            }
            if (memo != null) {
                relation.set("memo", memo);
            }

            // 4. 执行更新
            boolean updateResult = Db.update("bas_item_relation", relation);

            if (updateResult && quantity != null) {
                // 5. 重新计算父物料的总数量
                recalculateParentItemQuantities(parentItemId);
            }

            return updateResult;
        } catch (Exception e) {
            System.out.println("更新物料关系时发生异常，关系ID：" + relationId);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 递归重新计算父物料的数量（从当前物料开始向上计算）
     */
    private void recalculateParentItemQuantities(int itemId) {
        try {
            System.out.println("开始重新计算物料ID: " + itemId + " 的相关数量");

            // 查找所有包含此物料作为子物料的父物料
            String sql = "SELECT DISTINCT parentItemId FROM bas_item_relation WHERE childItemId = ?";
            List<Record> parentRelations = Db.find(sql, itemId);

            for (Record parentRelation : parentRelations) {
                int parentItemId = parentRelation.getInt("parentItemId");
                System.out.println("处理父物料ID: " + parentItemId);

                // 重新计算该父物料的总数量
                recalculateItemTotalQuantity(parentItemId);

                // 递归向上计算
                recalculateParentItemQuantities(parentItemId);
            }
        } catch (Exception e) {
            System.out.println("重新计算父物料数量时发生异常，物料ID：" + itemId);
            e.printStackTrace();
        }
    }

    /**
     * 计算物料的完整BOM总数量
     */
    private void recalculateItemTotalQuantity(int itemId) {
        try {
            System.out.println("计算物料ID: " + itemId + " 的BOM总数量");

            // 获取所有子物料关系
            List<Record> childRelations = Db.find(
                    "SELECT r.*, i.type as item_type FROM bas_item_relation r " +
                            "LEFT JOIN basitem i ON r.childItemId = i.id " +
                            "WHERE r.parentItemId = ? AND i.isdelete = 0", itemId);

            // 这里可以添加逻辑来计算总成本、总重量等
            // 目前主要关注数量的传递关系

            System.out.println("物料ID: " + itemId + " 有 " + childRelations.size() + " 个子物料");

            for (Record relation : childRelations) {
                int childItemId = relation.getInt("childItemId");
                BigDecimal quantity = relation.getBigDecimal("quantity");
                int childType = relation.getInt("item_type");

                System.out.println("子物料ID: " + childItemId + ", 数量: " + quantity + ", 类型: " + childType);

                // 如果是半成品或成品，需要进一步计算其子物料
                if (childType == 20 || childType == 30) { // 成品或半成品
                    calculateChildMaterialQuantities(childItemId, quantity);
                }
            }

        } catch (Exception e) {
            System.out.println("计算物料总数量时发生异常，物料ID：" + itemId);
            e.printStackTrace();
        }
    }

    /**
     * 计算子物料在父物料中的实际数量
     */
    private void calculateChildMaterialQuantities(int itemId, BigDecimal parentQuantity) {
        try {
            System.out.println("计算子物料ID: " + itemId + " 在父物料中的实际数量，父物料数量: " + parentQuantity);

            // 获取该物料的所有子物料
            List<Record> childRelations = Db.find(
                    "SELECT r.*, i.type as item_type FROM bas_item_relation r " +
                            "LEFT JOIN basitem i ON r.childItemId = i.id " +
                            "WHERE r.parentItemId = ? AND i.isdelete = 0", itemId);

            for (Record relation : childRelations) {
                int childItemId = relation.getInt("childItemId");
                BigDecimal childBaseQuantity = relation.getBigDecimal("quantity");
                int childType = relation.getInt("item_type");

                // 计算实际数量 = 父物料数量 × 子物料基础数量
                BigDecimal actualQuantity = parentQuantity.multiply(childBaseQuantity);

                System.out.println("子物料ID: " + childItemId +
                        ", 基础数量: " + childBaseQuantity +
                        ", 实际数量: " + actualQuantity +
                        ", 类型: " + childType);

                // 如果是原材料，这里可以更新库存需求或其他计算
                if (childType == 10) { // 原材料
                    updateRawMaterialRequirement(childItemId, actualQuantity);
                }

                // 递归计算下一级
                if (childType == 20 || childType == 30) {
                    calculateChildMaterialQuantities(childItemId, actualQuantity);
                }
            }

        } catch (Exception e) {
            System.out.println("计算子物料数量时发生异常，物料ID：" + itemId);
            e.printStackTrace();
        }
    }

    /**
     * 更新原材料需求（示例方法）
     */
    private void updateRawMaterialRequirement(int rawMaterialId, BigDecimal requiredQuantity) {
        try {
            System.out.println("更新原材料ID: " + rawMaterialId + " 的需求数量: " + requiredQuantity);

            // 这里可以实现更新原材料需求表的逻辑
            // 例如：更新库存需求、采购计划等
            // Record requirement = Db.findFirst("SELECT * FROM material_requirements WHERE item_id = ?", rawMaterialId);
            // if (requirement != null) {
            //     requirement.set("required_quantity", requiredQuantity);
            //     Db.update("material_requirements", requirement);
            // } else {
            //     Record newRequirement = new Record();
            //     newRequirement.set("item_id", rawMaterialId);
            //     newRequirement.set("required_quantity", requiredQuantity);
            //     Db.save("material_requirements", newRequirement);
            // }

        } catch (Exception e) {
            System.out.println("更新原材料需求时发生异常，原材料ID：" + rawMaterialId);
            e.printStackTrace();
        }
    }


}