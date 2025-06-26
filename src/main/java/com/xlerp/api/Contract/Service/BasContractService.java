package com.xlerp.api.Contract.Service;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.xlerp.common.model.Bascontract;
import com.xlerp.common.model.Bascontractitem;

import java.util.ArrayList;
import java.util.List;

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
        String select = "SELECT c.*," +
                "i.no AS itemNo, i.name AS itemName, i.spec AS itemSpec";
        String from = "FROM bascontractitem c " +
                "LEFT JOIN basitem i ON c.itemid = i.id " +
                "WHERE c.no = ? " +
                "ORDER BY c.id";

        // 拼接完整的SQL语句
        String sql = select + " " + from;

        // 使用完整的SQL语句和参数调用Db.find()
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
}