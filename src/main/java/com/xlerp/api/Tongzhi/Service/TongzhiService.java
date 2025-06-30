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

    //下面是获取通知的列表的功能。刘国奇
    public Page<Record> gettongzhipage(int pageNumber, int pageSize, String noticeid, String noticename) {
        // SELECT部分，修正列名双引号闭合问题，并去除重复列
        String select = "SELECT DISTINCT c.\"no\" AS contractno, c.\"name\" AS contractname, i.\"noticeid\" AS noticeid, i.\"noticename\" AS noticename, i.\"noticestatus\" AS noticestatus, i.\"noticeshenhe\" AS noticeshenhe, i.\"noticebuilddate\" AS noticebuilddate, i.\"noticedeliver\" AS noticedeliver, i.\"noticeauther\" AS noticeauther";

        // FROM和JOIN部分
        StringBuilder from = new StringBuilder(
                "FROM XLQCERP.\"bascontractitem\" i " +
                        "LEFT JOIN XLQCERP.\"bascontract\" c ON c.\"no\" = i.\"no\" " +
                        "WHERE i.\"isdelete\" = 0 and i.\"noticestatus\" >5 "
        );

        // 参数收集
        List<Object> params = new ArrayList<>();

        // 动态条件
        if (noticeid != null && !noticeid.trim().isEmpty()) {
            from.append(" AND i.\"noticeid\" LIKE ?");
            params.add("%" + noticeid.trim() + "%"); // 模糊查询（前后都加通配符）
        }
        if (noticename != null && !noticename.trim().isEmpty()) {
            from.append(" AND i.\"noticename\" LIKE ?");
            params.add("%" + noticename.trim() + "%"); // 模糊查询（前后都加通配符）
        }

        // 排序
        from.append(" ORDER BY i.\"noticebuilddate\" DESC");

        return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    //下面是获取审核后通知的列表的功能。刘国奇
    public Page<Record> getshenhehoutongzhipage(int pageNumber, int pageSize, String noticeid, String noticename) {
        // SELECT部分，修正列名双引号闭合问题，并去除重复列
        String select = "SELECT DISTINCT c.\"no\" AS contractno, c.\"name\" AS contractname, i.\"noticeid\" AS noticeid, i.\"noticename\" AS noticename, i.\"noticestatus\" AS noticestatus, i.\"noticeshenhe\" AS noticeshenhe, i.\"noticebuilddate\" AS noticebuilddate, i.\"noticedeliver\" AS noticedeliver, i.\"noticeauther\" AS noticeauther";

        // FROM和JOIN部分
        StringBuilder from = new StringBuilder(
                "FROM XLQCERP.\"bascontractitem\" i " +
                        "LEFT JOIN XLQCERP.\"bascontract\" c ON c.\"no\" = i.\"no\" " +
                        "WHERE i.\"isdelete\" = 0 and i.\"noticestatus\" >30 "
        );

        // 参数收集
        List<Object> params = new ArrayList<>();

        // 动态条件
        if (noticeid != null && !noticeid.trim().isEmpty()) {
            from.append(" AND i.\"noticeid\" LIKE ?");
            params.add("%" + noticeid.trim() + "%"); // 模糊查询（前后都加通配符）
        }
        if (noticename != null && !noticename.trim().isEmpty()) {
            from.append(" AND i.\"noticename\" LIKE ?");
            params.add("%" + noticename.trim() + "%"); // 模糊查询（前后都加通配符）
        }

        // 排序
        from.append(" ORDER BY i.\"noticebuilddate\" DESC");

        return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }
    //下面根据通知编号，获取通知信息，刘国奇
    public Page<Record> gettongzhibyid(int pageNumber, int pageSize, String noticeid) {
        String select = "select b.\"no\" as itemno,b.\"name\",b.\"spec\",c.\"itemnum\",b.\"unit\"," +
                "c.\"id\",c.\"noticeid\",c.\"noticedrawno\",c.\"noticeinstead\",c.\"noticename\",c.\"noticeauther\",c.\"noticebuilddate\"，c.\"noticedeliver\",c.\"noticeshenhe\",c.\"noticecomment\" ";
        String from = "from XLQCERP.\"bascontractitem\" c " +
                "left join XLQCERP.\"basitem\" b on " +
                "c.\"itemid\" = b.\"id\" " +
                "where c.\"noticeid\" = ?";

        return Db.paginate(pageNumber, pageSize, select, from, noticeid);

    }
    //根据 noticeid 更新 noticestatus 的值，下面的方法是确认、校验，审核通知的方法。
    /**
     * 确认通知方法
     * @param noticeid 通知编号
     * @return 更新成功返回 true，否则返回 false
     */
    public boolean querentongzhi(String noticeid) {

        String sql = "update bascontractitem set noticestatus = 20  where noticeid = ? ";
        return Db.update(sql, noticeid) > 0;
    }

    /**
     * 反确认通知方法
     * @param noticeid 通知编号
     * @return 更新成功返回 true，否则返回 false
     */
    public boolean fanquerentongzih(String noticeid) {
        String sql = "update bascontractitem set noticestatus = 10 where noticeid = ?";
        return Db.update(sql, noticeid) > 0;
    }

    /**
     * 校验通知方法
     * @param noticedeliver 替代通知信息
     * @param noticeid 通知编号
     * @return 更新成功返回 true，否则返回 false
     */
    public boolean jiaoyantongzhi(String noticedeliver, String noticeid) {
        String sql = "update bascontractitem set noticestatus = '30', noticedeliver = ? where noticeid = ?";
        return Db.update(sql, noticedeliver, noticeid) > 0;
    }

    /**
     * 反校验通知方法
     * @param noticeid 通知编号
     * @return 更新成功返回 true，否则返回 false
     */
    public boolean fanjiaoyantongzhi(String noticeid) {
        String sql = "update bascontractitem set noticestatus = 20 where noticeid = ?";
        return Db.update(sql, noticeid) > 0;
    }

    /**
     * 审核通知方法
     * @param noticeshenhe 审核信息
     * @param noticeid 通知编号
     * @return 更新成功返回 true，否则返回 false
     */
    public boolean shenhetongzhi(String noticeshenhe, String noticeid) {
        String sql = "update bascontractitem set noticestatus = 40 , noticeshenhe = ? where noticeid = ?";
        return Db.update(sql, noticeshenhe, noticeid) > 0;
    }

    /**
     * 反审核通知方法
     * @param noticeid 通知编号
     * @return 更新成功返回 true，否则返回 false
     */
    public boolean fanshenhtongzhi(String noticeid) {
        String sql = "update bascontractitem set noticestatus = 30 where noticeid = ?";
        return Db.update(sql, noticeid) > 0;
    }
}