package com.xlerp.api.Contract.Service;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.xlerp.common.model.Bascontract;
import com.xlerp.common.model.Bascontractitem;
import com.xlerp.common.model.Basitem;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasContractService {
    private static final Bascontract dao = new Bascontract();


    // 获取合同列表，包含多表连接和动态条件
    public Page<Record> getContractList(int pageNumber, int pageSize, String term, String contractNo,
                                        String projectName, String salesmanNo, String rule, String owner) {
        // SELECT部分
        String select = "SELECT c.id,c.no,c.gridno, c.ecpno,c.equipno," +
                "c.name, " +
                "o.descr AS customerName, " +
                "u.name AS salesmanName, " +
                "bci.contractSum, " +
                "DATE_FORMAT(c.signdate, '%Y-%m-%d') AS signDate, " +
                "c.status, " +
                "c.term AS term, " +
                "su.descr AS writer";

        // FROM和JOIN部分
        StringBuilder from = new StringBuilder(
                "FROM bascontract c " +
                        "LEFT JOIN basorg o ON c.customerid = o.id " +
                        "LEFT JOIN hruser u ON c.salesmanid = u.id " +
                        "LEFT JOIN sysuser su ON c.userid = su.id " +
                        "LEFT JOIN (SELECT no, SUM(itemsum) AS contractSum FROM bascontractitem WHERE isdelete = 0 GROUP BY no) bci ON c.no = bci.no " +
                        "WHERE c.isdelete = 0 AND c.type = 0"
        );

        // 参数收集
        List<Object> params = new ArrayList<>();
        Boolean flag  = true;//是否添加最后的状态条件，假如是搜索条件都为空，则不添加状态条件，搜索条件不为空的情况下说明这个合同的状态不确定如果加上状态条件，可能查不出来
        // 动态条件
        if (owner != null && !owner.trim().isEmpty()) {
            from.append(" AND c.owner = ?");
            params.add(owner.trim());
        }
        if (contractNo != null && !contractNo.trim().isEmpty()) {
            flag  = false;
            from.append(" AND c.no LIKE ?");
            params.add("%" + contractNo.trim() + "%"); // 模糊查询（前后都加通配符）
        }
        if (projectName != null && !projectName.trim().isEmpty()) {
            flag  = false;
            from.append(" AND c.name LIKE ?");
            params.add("%" + projectName.trim() + "%"); // 模糊查询（前后都加通配符）
        }
        if (salesmanNo != null && !salesmanNo.trim().isEmpty()) {
            flag  = false;
            from.append(" AND u.no = ?");
            params.add(salesmanNo.trim());
        }
        if (rule != null && !rule.trim().isEmpty()) {
            from.append(" AND c.rule LIKE ?");
            params.add(rule.trim() + "%"); // 前缀搜索
        }

        if(flag){
            // 状态条件在无搜索条件的情况下就是加上的
            if (term != null && !term.trim().isEmpty()) {
                from.append(" AND (c.status = 10 OR c.status = 20 OR (c.term = ? AND c.status > 0))");
                params.add(term.trim());
            } else {
                from.append(" AND (c.status = 10 OR c.status = 20)");
            }
        }


        // 排序
        from.append(" ORDER BY c.indate DESC");

        return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }
    public Record getContractInfoByNo(String contractNo) {
        if (contractNo == null || contractNo.trim().isEmpty()) {
            return null; // 合同编号为空，返回 null
        }

        String select = "SELECT c.*, o.descr AS customerName, u.name AS salesmanName, su.descr AS writer, " +
                "(SELECT SUM(bascontractitem.itemsum) FROM bascontractitem WHERE bascontractitem.no = c.no AND bascontractitem.isdelete = 0) AS contractSum";

        StringBuilder sql = new StringBuilder(
                "FROM bascontract c " +
                        "LEFT JOIN basorg o ON c.customerid = o.id " +
                        "LEFT JOIN hruser u ON c.salesmanid = u.id " +
                        "LEFT JOIN sysuser su ON c.userid = su.id " +
                        "WHERE c.isdelete = 0 AND c.type = 0 AND c.no = ?"
        );

        try {
            return Db.findFirst(select + " " + sql.toString(), contractNo.trim());
        } catch (Exception e) {
            System.err.println("查询合同信息失败: " + e.getMessage());
            return null;
        }
    }

    public Bascontract findById(int id) {
        return dao.findFirst("select * from bascontract where id = ? and isdelete = 0", id);
    }

    public boolean save(Bascontract bascontract) {
        return bascontract.save();
    }

    public boolean update(Bascontract bascontract) {
        return bascontract.update();
    }

    public boolean deleteById(int id) {
        return Db.update("update bascontract set isdelete = -1 where id = ?", id) > 0;
    }


    public List<Record> getContractItemByNo(String contractNo) {
        String sql = "SELECT c.*, " +
                "i.no AS itemNo, i.name AS itemName, i.spec AS itemSpec, " +
                "(SELECT COALESCE(SUM(d.amount), 0) " +
                " FROM pldingdanitem d " +
                " WHERE d.conitemId = c.id) AS allocatedOrderAmount " +
                "FROM bascontractitem c " +
                "LEFT JOIN basitem i ON c.itemid = i.id " +
                "WHERE c.no = ? " +
                "AND c.isdelete = 0 "+
                "ORDER BY c.id";

        return Db.find(sql, contractNo);
    }


    public boolean saveitem(Bascontractitem bascontractitem) {
        return bascontractitem.save();
    }

    public boolean updateitem(Bascontractitem bascontractitem) {
        return bascontractitem.update();
    }

    public boolean deleteitemById(int i) {
        return Db.update("update bascontractitem set isdelete = -1 where id = ?", i) > 0;
    }

    public Record finditemById(int id) {
        String select = "SELECT c.*," +
                "i.no AS itemNo, i.name AS itemName, i.spec AS itemSpec";
        String from = "FROM bascontractitem c " +
                "LEFT JOIN basitem i ON c.itemid = i.id " +
                "WHERE c.id = ? " +
                "ORDER BY c.id";

        // 拼接完整的SQL语句
        String sql = select + " " + from;

        return Db.findFirst(sql, id);
    }

    public Bascontract findbyNo(String no) {
        return dao.findFirst("select * from bascontract where no = ?", no);
    }

    public boolean updateStatusById(String id, String status) {
        return Db.update("update bascontract set status = ? where id = ?", status, id) > 0;
    }



    private static final Basitem daoBasitem = new Basitem();

    public Map<String, Object> parseContractExcel(File excelFile, String contractNo) throws Exception {
        List<Map<String, Object>> failedRows = new ArrayList<>();
        int successCount = 0;
        int totalRows = 0;
        Workbook workbook = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(excelFile);
            // 根据文件类型创建工作簿
            if (excelFile.getName().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (excelFile.getName().endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("不支持的文件格式，仅支持 .xls 或 .xlsx");
            }

            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            // 获取表头行
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Excel文件第一行不能为空（需包含表头）");
            }

            // 从第二行开始迭代（跳过表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue; // 跳过空行
                totalRows++; // 统计非空行

                Map<String, Object> failureInfo = new HashMap<>();
                failureInfo.put("rowNumber", i + 1); // Excel row number (1-based)

                // 存储所有列的数据
                Map<String, String> rowData = new HashMap<>();
                rowData.put("index", getCellValue(row.getCell(0))); // 序号 (第一列)
                rowData.put("itemName", getCellValue(row.getCell(1))); // 产品名称
                rowData.put("itemNo", getCellValue(row.getCell(2))); // 订货型号
                rowData.put("itemNum", getCellValue(row.getCell(3))); // 产品数量
                rowData.put("itemRealPrice", getCellValue(row.getCell(4))); // 销售单价
                rowData.put("itemUnit", getCellValue(row.getCell(5))); // 产品单位
                rowData.put("itemRealSum", getCellValue(row.getCell(6))); // 销售总价
                rowData.put("itemWeight", getCellValue(row.getCell(7))); // 单重
                rowData.put("itemGrossWeight", getCellValue(row.getCell(8))); // 总重
                rowData.put("itemMemo", getCellValue(row.getCell(9))); // 备注
                rowData.put("poItemNo", getCellValue(row.getCell(10))); // 行订单号
                rowData.put("poItemId", getCellValue(row.getCell(11))); // 行订单ID
                rowData.put("poItemCode", getCellValue(row.getCell(12))); // 国网物料编码
                failureInfo.put("rowData", rowData);

                try {
                    Bascontractitem iteminfo = new Bascontractitem();
                    String itemSpec = getCellValue(row.getCell(2));//规格型号
                    Basitem item = getItemInfoBySpec(itemSpec);

                    if (item == null) {
                        failureInfo.put("error", "订货型号 " + itemSpec + " 不存在");
                        failedRows.add(failureInfo);
                        continue;
                    }

                    iteminfo.set("no", contractNo); // 合同编号
                    iteminfo.set("itemid", item.getId()); // 对应basitem里面的ID
                    iteminfo.set("itemnum", getCellValue(row.getCell(3))); // 产品数量
                    iteminfo.set("itemRealPrice", getCellValue(row.getCell(4))); // 销售单价
                    iteminfo.set("itemunit", getCellValue(row.getCell(5))); // 产品单位
                    iteminfo.set("itemRealSum", getCellValue(row.getCell(6))); // 销售总价
                    iteminfo.set("itemweight", getCellValue(row.getCell(7))); // 单重
                    iteminfo.set("itemgrossweight", getCellValue(row.getCell(8))); // 总重
                    iteminfo.set("itemmemo", getCellValue(row.getCell(9))); // 备注
                    iteminfo.set("poItemNo", getCellValue(row.getCell(10))); // 行订单号
                    iteminfo.set("poItemId", getCellValue(row.getCell(11))); // 行订单ID
                    iteminfo.set("poItemCode", getCellValue(row.getCell(12))); // 国网物料编码
                    if (iteminfo.save()) {
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
            // 关闭资源
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

    // 根据itemNo从basItem里面获取物料信息
    public Basitem getItemInfoByNo(String itemNo) {
        return daoBasitem.findFirst("SELECT * FROM basitem WHERE no = ?", itemNo);
    }

    // 根据itemSpec从basItem里面获取物料信息
    public Basitem getItemInfoBySpec(String itemSpec) {
        return daoBasitem.findFirst("SELECT * FROM basitem WHERE spec = ?", itemSpec);
    }

    // 获取单元格值的辅助方法
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }



    public Page<Record> getConfirmedList(int pageNumber, int pageSize, String contractNo, String projectName) {
        // SELECT部分
        String select = "SELECT c.id,c.no,c.gridno, c.ecpno,c.equipno," +
                "c.name, " +
                "o.descr AS customerName, " +
                "u.name AS salesmanName, " +
                "bci.contractSum, " +
                "DATE_FORMAT(c.signdate, '%Y-%m-%d') AS signDate, " +
                "c.status, " +
                "c.term AS term, " +
                "su.descr AS writer";

        // FROM和JOIN部分
        StringBuilder from = new StringBuilder(
                "FROM bascontract c " +
                        "LEFT JOIN basorg o ON c.customerid = o.id " +
                        "LEFT JOIN hruser u ON c.salesmanid = u.id " +
                        "LEFT JOIN sysuser su ON c.userid = su.id " +
                        "LEFT JOIN (SELECT no, SUM(itemsum) AS contractSum FROM bascontractitem WHERE isdelete = 0 GROUP BY no) bci ON c.no = bci.no " +
                        "WHERE c.isdelete = 0 AND c.type = 0 AND c.status = 20"
        );

        // 参数收集
        List<Object> params = new ArrayList<>();
        Boolean flag  = true;//是否添加最后的状态条件，假如是搜索条件都为空，则不添加状态条件，搜索条件不为空的情况下说明这个合同的状态不确定如果加上状态条件，可能查不出来
        // 动态条件
        if (contractNo != null && !contractNo.trim().isEmpty()) {
            flag  = false;
            from.append(" AND c.no LIKE ?");
            params.add("%" + contractNo.trim() + "%"); // 模糊查询（前后都加通配符）
        }
        if (projectName != null && !projectName.trim().isEmpty()) {
            flag  = false;
            from.append(" AND c.name LIKE ?");
            params.add("%" + projectName.trim() + "%"); // 模糊查询（前后都加通配符）
        }

        // 排序
        from.append(" ORDER BY c.indate DESC");

        return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Page<Record> getContractItemPage(String contractNo, String itemName, int pageNumber, int pageSize) {
        // 构建SQL语句和参数列表
        StringBuilder selectSql = new StringBuilder("SELECT c.id,c.itemnum,c.itemunit, " +
                "i.no AS itemNo, i.name AS itemName, i.spec AS itemSpec");

        StringBuilder fromSql = new StringBuilder("FROM bascontractitem c " +
                "LEFT JOIN basitem i ON c.itemid = i.id " +
                "WHERE c.no = ? AND c.isdelete = 0 ");

        List<Object> params = new ArrayList<>();
        params.add(contractNo);

        // 添加物品名称过滤条件
        if (itemName != null && !itemName.trim().isEmpty()) {
            fromSql.append("AND i.name LIKE ? ");
            params.add("%" + itemName.trim() + "%");
        }

        // 添加排序条件
        fromSql.append("ORDER BY c.id");

        // 执行分页查询
        return Db.paginate(pageNumber, pageSize,
                selectSql.toString(),
                fromSql.toString(),
                params.toArray());
    }
}