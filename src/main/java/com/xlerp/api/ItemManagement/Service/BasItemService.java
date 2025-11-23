package com.xlerp.api.ItemManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
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

    public Page<Basitem> paginate(int pageNumber, int pageSize, String itemNo, String itemName, String firstClassId, String secondClassId,String spec) {
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
        // 规格筛选
        if (StrKit.notBlank(spec)) {
            from.append("and b.spec like ? ");
            params.add("%" + spec + "%");
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

    /**
     * 保存物料（仅做：编号唯一性校验 + 保存执行）
     * 注：必填字段、格式校验已由前端完成，后端不重复校验
     */
    public boolean save(Basitem basItem) {

        // 2. 高效校验编号唯一性：用 count 替代 findFirst（仅查数量，不查整行数据，性能更优）
        Long duplicateCount = Db.queryLong("select count(*) from basitem where no = ?", basItem.getNo().trim());        if (duplicateCount > 0) {
            return false; // 编号重复，返回失败
        }

        // 3. 执行保存（前端已校验必填项，此处直接保存）
        basItem.save();
        return true;
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






}