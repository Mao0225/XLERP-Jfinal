package com.xlerp.api.Tongzhi.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.ArrayList;
import java.util.List;

public class TongzhiService {


    public List<Record> getContractItemByNoticeId(String noticeid) {
        String sql = "SELECT c.*, i.no AS itemNo, i.name AS itemName, i.spec AS itemSpec, i.tuzhiNo," +
                "psp.scheduleCode,bt.tuzhimingcheng as tuzhiName,bt.tuzhizuozhe,bt.tuzhiurl,bt.itemName as tuzhiItemName, bt.itemSpec as tuzhiItemSpec  " +
                "FROM bascontractitem c " +
                "LEFT JOIN basitem i ON c.itemid = i.id " +
                "LEFT JOIN pl_schedule_plan psp ON c.id = psp.poItemId " +
                "LEFT JOIN bastuzhi bt ON i.tuzhiNo = bt.tuzhibianhao " +
                "WHERE c.noticeid = ? AND c.isdelete = 0 " +
                "ORDER BY c.id";

        List<Object> params = new ArrayList<>();
        params.add(noticeid);

        return Db.find(sql, params.toArray());
    }

    public static Page<Record> getNoticeGroup(String noticeid, String noticeName, String pageNumber, String pageSize) {
        // 1. 处理分页参数，防止空指针，默认第一页，每页20条
        int pn = StrKit.isBlank(pageNumber) ? 1 : Integer.parseInt(pageNumber);
        int ps = StrKit.isBlank(pageSize) ? 20 : Integer.parseInt(pageSize);

        // 2. 定义参数集合
        List<Object> params = new ArrayList<>();

        // 3. 定义 Select 部分
        // 使用 MAX() 是为了兼容 MySQL 的严格模式，同时确保多行合并时取其中非空值
        String select = "SELECT c.noticeid, MAX(c.noticename) as noticename, MAX(c.no) as contractNo, " +
                "MAX(bc.name) as contractName,MAX(o.descr) AS customerName, MAX(u.name) AS salesmanName, " +
                "MAX(c.noticeStatus) as noticestatus";

        // 4. 定义 From 及 Where 部分
        StringBuilder sqlExceptSelect = new StringBuilder();
        sqlExceptSelect.append("FROM bascontractitem c ");
        sqlExceptSelect.append("LEFT JOIN bascontract bc ON bc.no = c.no ");
        sqlExceptSelect.append("LEFT JOIN basorg o ON bc.customerid = o.id ");
        sqlExceptSelect.append("LEFT JOIN hruser u ON bc.salesmanid = u.id ");

        // 基础条件：未删除 且 noticeid 不为空 (排除掉还没有制定通知的产品)
        sqlExceptSelect.append("WHERE c.isdelete = 0 AND c.noticeid IS NOT NULL AND c.noticeid != 'N/A' ");

        // 5. 动态添加查询条件
        if (StrKit.notBlank(noticeid)) {
            sqlExceptSelect.append("AND c.noticeid LIKE ? ");
            params.add("%" + noticeid + "%");
        }

        if (StrKit.notBlank(noticeName)) {
            sqlExceptSelect.append("AND c.noticename LIKE ? ");
            params.add("%" + noticeName + "%");
        }

        // 6. 核心逻辑：按 noticeid 分组，实现去重
        sqlExceptSelect.append("GROUP BY c.noticeid ");

        // 7. 排序 (这里建议按通知编号排序，或者按创建时间排序如果表里有的话)
        sqlExceptSelect.append("ORDER BY c.noticeid DESC ");

        // 8. 执行分页查询
        return Db.paginate(pn, ps, select, sqlExceptSelect.toString(), params.toArray());
    }

    public boolean updateStatus(String id, String status) {
        return Db.update("update bascontractitem set noticestatus = ? where id = ?", status, id) > 0;
    }

    public boolean updateBatchStatus(String noticeid, String status) {
        return Db.update("update bascontractitem set noticestatus = ? where noticeid = ?", status, noticeid) > 0;
    }

    // 直接处理版（适配达梦，极简）
    public boolean updateBatchNotice(String noticeid, String noticeName, String ids) {
        // 直接拼接IN条件，避开JFinal参数转义
        String sql = "update bascontractitem set noticeid = ?, noticename = ?, noticestatus = 30 where id in (" + ids + ")";
        return Db.update(sql, noticeid, noticeName) > 0;
    }

    public boolean updateStatusBynoticeId(String noticeid, String status) {
        return Db.update("update bascontractitem set noticestatus = ? where noticeid = ?", status, noticeid) > 0;
    }
}