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

    public Page<Basitem> paginate(int pageNumber, int pageSize, String itemNo, String itemName, String inclass,  String type) {
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
     * @param itemId 物料ID
     * @return 树形结构的物料列表
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
        rootNode.put("plannedPrice", currentItem.getBigDecimal("planned_price"));
        rootNode.put("avgPrice", currentItem.getBigDecimal("avg_price"));
        rootNode.put("level", 0);
        rootNode.put("isRoot", true);
        rootNode.put("children", new ArrayList<Map<String, Object>>());

        // 递归获取子物料
        buildMaterialTree(rootNode, 1);

        result.add(rootNode);
        return result;
    }

    /**
     * 递归构建物料树
     * @param parentNode 父节点
     * @param level 当前层级
     */
    private void buildMaterialTree(Map<String, Object> parentNode, int level) {
        int parentItemId = (int) parentNode.get("id");

        // 查询直接子物料
        String sql = "SELECT r.*, i.*, r.quantity as relation_quantity " +
                "FROM bas_item_relation r " +
                "LEFT JOIN basitem i ON r.childItemId = i.id " +
                "WHERE r.parentItemId = ? AND i.isdelete = 0 " +
                "ORDER BY i.no";

        List<Basitem> childItems = dao.find(sql, parentItemId);

        List<Map<String, Object>> children = new ArrayList<>();

        for (Basitem childItem : childItems) {
            Map<String, Object> childNode = new HashMap<>();
            childNode.put("id", childItem.getId());
            childNode.put("no", childItem.getStr("no"));
            childNode.put("name", childItem.getStr("name"));
            childNode.put("unit", childItem.getStr("unit"));
            childNode.put("spec", childItem.getStr("spec"));
            childNode.put("type", childItem.getInt("type"));
            childNode.put("inclass", childItem.getStr("inclass"));
            childNode.put("weight", childItem.getBigDecimal("weight"));
            childNode.put("plannedPrice", childItem.getBigDecimal("planned_price"));
            childNode.put("avgPrice", childItem.getBigDecimal("avg_price"));
            childNode.put("quantity", childItem.getBigDecimal("relation_quantity"));
            childNode.put("level", level);
            childNode.put("isRoot", false);
            childNode.put("children", new ArrayList<Map<String, Object>>());

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
        String sql = "SELECT r.*, i.*, r.quantity as relation_quantity, r.id as relation_id " +
                "FROM bas_item_relation r " +
                "LEFT JOIN basitem i ON r.childItemId = i.id " +
                "WHERE r.parentItemId = ? AND i.isdelete = 0 " +
                "ORDER BY i.no";

        return Db.find(sql, itemId);
    }


}