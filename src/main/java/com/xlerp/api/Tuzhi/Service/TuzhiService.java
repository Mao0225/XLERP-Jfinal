package com.xlerp.api.Tuzhi.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.Bastuzhi;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class TuzhiService {
    private static final Bastuzhi dao = new Bastuzhi().dao();

//    public Page<Bastuzhi> paginate(int pageNumber, int pageSize, String tuzhimingcheng) {
//        // 1. 构建SELECT子句句：列出bastuzhi所有字段 + 统计数量字段
//        String select = "select " +
//                "bastuzhi.id, " +
//                "bastuzhi.tuzhibianhao, " +
//                "bastuzhi.tuzhimingcheng, " +
//                "bastuzhi.tuzhizuozhe, " +
//                "bastuzhi.chuangzuoriqi, " +
//                "bastuzhi.tuzhimiaoshu, " +
//                "bastuzhi.memo, " +
//                "bastuzhi.flag, " +
//                "bastuzhi.type, " +
//                "bastuzhi.writer, " +
//                "bastuzhi.tuzhiurl, " +
//                "bastuzhi.isdelete, " +
//                "count(bastuzhicailiao.id) as zicailiaoshuliang";
//
//        // 2. 构建FROM子句：左连接bastuzhicailiao表 + 条件 + GROUP BY + ORDER BY
//        StringBuilder from = new StringBuilder();
//        from.append("from XLQCerp.bastuzhi ");  // 关联schema
//        from.append("left join xlqcerp.bastuzhicailiao on bastuzhi.id = bastuzhicailiao.tuzhiid ");  // 左连接关联
//
//        // 3. 处理查询条件（tuzhimingcheng模糊匹配）
//        if (tuzhimingcheng != null && !tuzhimingcheng.trim().isEmpty()) {
//            from.append("where bastuzhi.tuzhimingcheng like ? ");  // 注意空格分隔
//        }
//
//        // 4. 必须包含GROUP BY（达梦要求所有非聚合字段都在GROUP BY中）
//        from.append("group by " +
//                "bastuzhi.id, " +
//                "bastuzhi.tuzhibianhao, " +
//                "bastuzhi.tuzhimingcheng, " +
//                "bastuzhi.tuzhizuozhe, " +
//                "bastuzhi.chuangzuoriqi, " +
//                "bastuzhi.tuzhimiaoshu, " +
//                "bastuzhi.memo, " +
//                "bastuzhi.flag, " +
//                "bastuzhi.type, " +
//                "bastuzhi.writer, " +
//                "bastuzhi.tuzhiurl, " +
//                "bastuzhi.isdelete ");
//
//        // 5. 保持原排序逻辑（按id降序）
//        from.append("order by bastuzhi.id desc");
//
//        // 6. 分页查询（带条件或不带条件）
//        if (tuzhimingcheng != null && !tuzhimingcheng.trim().isEmpty()) {
//            return dao.paginate(pageNumber, pageSize, select, from.toString(), "%" + tuzhimingcheng + "%");
//        } else {
//            return dao.paginate(pageNumber, pageSize, select, from.toString());
//        }
//    }


    //简化后的分页函数不用关联了
    public Page<Bastuzhi> paginate(int pageNumber, int pageSize, String tuzhimingcheng,String itemName,String itemSpec) {
        // 1. 构建SELECT子句：仅查询bastuzhi表所有字段
        String select = "select *";

        // 2. 构建FROM子句：仅查询bastuzhi表，无需连接
        StringBuilder from = new StringBuilder();
        from.append("from XLQCerp.bastuzhi ");  // 关联schema
        List<Object> params = new ArrayList<>();


        // 3. 处理查询条件（tuzhimingcheng模糊匹配）
        if (tuzhimingcheng != null && !tuzhimingcheng.trim().isEmpty()) {
            from.append("where bastuzhi.tuzhimingcheng like ? ");  // 注意空格分隔
            params.add("%" + tuzhimingcheng + "%");

        }
        if (itemName != null && !itemName.trim().isEmpty()) {
            from.append("where bastuzhi.itemName like ? ");  // 注意空格分隔
            params.add("%" + itemName + "%");
        }
        if (itemSpec != null && !itemSpec.trim().isEmpty()) {
            from.append("where bastuzhi.itemSpec like ? ");  // 注意空格分隔
            params.add("%" + itemSpec + "%");
        }

        // 4. 排序逻辑（保持按id降序）
        from.append("order by id desc");
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());


    }

    public Bastuzhi findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Bastuzhi tuzhi) {
        return tuzhi.save();
    }

    public boolean update(Bastuzhi tuzhi) {
        return tuzhi.update();
    }

    public boolean deleteById(int id) {
        Bastuzhi tuzhi = dao.findById(id);
        if (tuzhi == null) {
            return false; // 图纸不存在，删除失败
        }
        return tuzhi.delete(); // 返回删除结果
    }



    /**
     * 解析Excel文件并导入Bastuzhi数据
     * @param excelFile Excel文件
     * @return 包含导入结果的Map，包含成功数量、失败行信息、失败数量和总行数
     * @throws Exception 文件处理或数据库操作异常
     */
    public static Map<String, Object> parseBastuzhiExcel(File excelFile) throws Exception {
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

            /*Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                String header = getCellValue(headerRow.getCell(i));
                if (header != null && !header.isEmpty()) {
                    headerMap.put(header.trim(), i);
                }
            }*/
            // 2. 构建表头-列索引映射（修复1：清洗表头，去除空格/特殊字符）
            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headerRow.getLastCellNum(); i++) {
                Cell cell = headerRow.getCell(i);
                String rawHeader = getCellValue(cell);
                // 表头清洗：去半角空格、去全角空格、去除冒号/下划线等特殊字符
                String cleanHeader = rawHeader == null ? "" : rawHeader
                        .trim() // 去半角空格（如"图纸编号 "→"图纸编号"）
                        .replaceAll("　", "") // 去全角空格（如"图纸编号　"→"图纸编号"）
                        .replaceAll("[：:._-]", ""); // 去特殊字符（如"图纸_编号"→"图纸编号"）;
                if (!cleanHeader.isEmpty()) {
                    headerMap.put(cleanHeader, i);
                    // 新增日志：打印清洗后的表头与列索引，方便定位问题
                    System.out.println("[表头映射] 清洗后表头：'" + cleanHeader + "' → 列索引：" + i);
                }
            }

            // 3. 必要表头校验（修复2：确保所有代码用到的表头都存在）
            // 注意：需包含代码中所有通过headerMap.get()获取的表头
            List<String> requiredHeaders = Arrays.asList(
                    "图纸编号", "图纸名称", "图纸作者", "创作日期",
                    "图纸描述", "图纸文件", "备注","录入者"
            );
            List<String> missingHeaders = new ArrayList<>();
            for (String reqHeader : requiredHeaders) {
                if (!headerMap.containsKey(reqHeader)) {
                    missingHeaders.add(reqHeader);
                }
            }
            // 若缺失表头，直接抛明确错误，避免后续空指针
            if (!missingHeaders.isEmpty()) {
                String errorMsg = "Excel缺少必要表头：" + String.join(", ", missingHeaders);
                System.out.println("[错误] " + errorMsg);
                throw new IllegalArgumentException(errorMsg);
            }

            // 4. 解析数据行（修复3：所有字段的列索引都判空，避免null拆箱）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                totalRows++;

                Map<String, Object> failureInfo = new HashMap<>();
                Map<String, Object> rowData = new HashMap<>();
                failureInfo.put("rowNumber", i + 1);
                rowData.put("index", i + 1);

                // --------------------------
                // 关键：每个字段都先获取索引→判空→再使用
                // --------------------------
                // 图纸编号
                Integer tuzhiNoCol = headerMap.get("图纸编号");
                String tuzhiNo = tuzhiNoCol != null ? getCellValue(row.getCell(tuzhiNoCol)) : "";
                rowData.put("tuzhiNo", tuzhiNo);

                // 图纸名称
                Integer tuzhiNameCol = headerMap.get("图纸名称");
                String tuzhiName = tuzhiNameCol != null ? getCellValue(row.getCell(tuzhiNameCol)) : "";
                rowData.put("tuzhimingcheng", tuzhiName);

                // 图纸作者
                Integer authorCol = headerMap.get("图纸作者");
                String author = authorCol != null ? getCellValue(row.getCell(authorCol)) : "";
                rowData.put("tuzhizuozhe", author);

                // 创作日期
                Integer dateCol = headerMap.get("创作日期");
                String createDate = dateCol != null ? getCellValue(row.getCell(dateCol)) : "";
                rowData.put("chuangzuoriqi", createDate);

                // 图纸描述
                Integer descCol = headerMap.get("图纸描述");
                String desc = descCol != null ? getCellValue(row.getCell(descCol)) : "";
                rowData.put("tuzhimiaoshu", desc);

                // 图纸文件
                Integer fileCol = headerMap.get("图纸文件");
                String fileUrl = fileCol != null ? getCellValue(row.getCell(fileCol)) : "";
                rowData.put("tuzhiurl", fileUrl);

                // 备注
                Integer memoCol = headerMap.get("备注");
                String memo = memoCol != null ? getCellValue(row.getCell(memoCol)) : "";
                rowData.put("memo", memo);

                // 录入者
                Integer writerCol = headerMap.get("录入者");
                String writer = writerCol != null ? getCellValue(row.getCell(writerCol)) : "";
                rowData.put("writer", writer);

                failureInfo.put("rowData", rowData);

                // 5. 数据校验与保存（原逻辑不变，仅用上述已判空的字段）
                try {
                    Bastuzhi tuzhi = new Bastuzhi();
                    tuzhi.set("tuzhibianhao", tuzhiNo);
                    tuzhi.set("tuzhimingcheng", tuzhiName);
                    tuzhi.set("tuzhizuozhe", author);
                    tuzhi.set("chuangzuoriqi", createDate);
                    tuzhi.set("tuzhimiaoshu", desc);
                    tuzhi.set("tuzhiurl", fileUrl);
                    tuzhi.set("memo", memo);
                    tuzhi.set("writer",writer);
                    tuzhi.setIsdelete(0);

                    // 图纸编号校验
                    if (tuzhiNo == null || tuzhiNo.trim().isEmpty()) {
                        failureInfo.put("error", "图纸编号不能为空");
                        failedRows.add(failureInfo);
                        continue;
                    }

                    // 重复校验（注意：原代码查的是basitem表，若表名/字段不对需调整）
                    Bastuzhi existingItem = dao.findFirst("SELECT * FROM basitem WHERE no = ? and isdelete = 0", tuzhiNo);
                    if (existingItem != null) {
                        failureInfo.put("error", "图纸编号 " + tuzhiNo + " 已存在");
                        failedRows.add(failureInfo);
                        continue;
                    }

                    if (tuzhi.save()) {
                        successCount++;
                        System.out.println("[成功] 第" + (i + 1) + "行保存成功：" + tuzhiNo);
                    } else {
                        failureInfo.put("error", "保存失败，数据库操作错误");
                        failedRows.add(failureInfo);
                    }
                } catch (Exception e) {
                    String errorMsg = "解析错误：" + e.getMessage();
                    failureInfo.put("error", errorMsg);
                    failedRows.add(failureInfo);
                    System.out.println("[失败] 第" + (i + 1) + "行：" + errorMsg);
                }
            }
        } finally {
            // 关闭流（原逻辑不变，确保资源释放）
            if (workbook != null) {
                try { workbook.close(); } catch (Exception e) { e.printStackTrace(); }
            }
            if (fis != null) {
                try { fis.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }

        // 组装返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("failedRows", failedRows);
        result.put("failedCount", failedRows.size());
        result.put("totalRows", totalRows);
        return result;
    }

    // getCellValue方法保持不变（原逻辑正确）
    private static String getCellValue(Cell cell) {
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