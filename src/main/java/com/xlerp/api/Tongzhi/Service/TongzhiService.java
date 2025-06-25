package com.xlerp.api.Tongzhi.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.Bascontract;
import com.xlerp.common.model.Bascontractitem;
import com.xlerp.common.model.Bastuzhi;

import java.util.ArrayList;
import java.util.List;

public class TongzhiService {
    private static final Bascontract dao = new Bascontract();
    private static final Bastuzhi tuzhidao = new Bastuzhi().dao();

    public Page<Record> getContractList(int pageNumber, int pageSize, String term, String contractNo,
                                        String projectName, String salesmanNo, String rule, String owner) {
        // SELECT部分
        String select = "SELECT c.id,c.no,c.gridno," +
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
                from.append(" AND (c.status > 10 OR c.status = 20 OR (c.term = ? AND c.status > 10))");
                params.add(term.trim());
            } else {
                from.append(" AND (c.status > 10 OR c.status = 20)");
            }
        }


        // 排序
        from.append(" ORDER BY c.id DESC");

        return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Page<Bastuzhi> gettuzhilist(int pageNumber, int pageSize, String tuzhimingcheng) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from bastuzhi");
        if (tuzhimingcheng != null && !tuzhimingcheng.trim().isEmpty()) {
            from.append(" where tuzhimingcheng like ?");
        }
        from.append(" order by id desc");
        if (tuzhimingcheng != null && !tuzhimingcheng.trim().isEmpty()) {
            return tuzhidao.paginate(pageNumber, pageSize, select, from.toString(), "%" + tuzhimingcheng + "%");
        } else {
            return tuzhidao.paginate(pageNumber, pageSize, select, from.toString());
        }
    }
    public boolean updateitem(Bascontractitem bascontractitem) {
        return bascontractitem.update();
    }
}