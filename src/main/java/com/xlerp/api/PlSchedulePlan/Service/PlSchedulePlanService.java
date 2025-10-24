package com.xlerp.api.PlSchedulePlan.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.api.System.Service.BasNoService;
import com.xlerp.common.model.PlSchedulePlan;

import java.util.ArrayList;
import java.util.List;

public class PlSchedulePlanService {
    private static final PlSchedulePlan dao = new PlSchedulePlan();
    private static final BasNoService basNoService = new BasNoService();
    public Page<PlSchedulePlan> paginate(int pageNumber, int pageSize,
                                         String contractNo, String contractName,
                                         String purchaserHqCode, String scheduleCode,
                                         String status) {
        // 构建查询字段
        String select = "select p.*,bc.no as contractNo,bc.name as contractName,bci.itemnum as amount,bi.no as itemCode, bi.name as itemName, bi.spec as itemSpec,bci.itemunit as itemUnit, " +
                "(select COALESCE(sum(po.amount), 0) from pl_production_order po where po.scheduleCode = p.scheduleCode) as allocatedAmount";

        // 构建FROM子句和基础WHERE条件
        StringBuilder from = new StringBuilder("from pl_schedule_plan p ");
        from.append("left join bascontractitem bci on p.poItemId = bci.id ");
        from.append("left join bascontract bc on bc.no = bci.no ");
        from.append("left join basitem bi on bci.itemid = bi.id ");
        from.append("where 1 = 1 "); // 基础条件，简化后续拼接

        // 构建查询参数
        List<Object> params = new ArrayList<>();

        // 动态添加查询条件
        if (contractNo != null && !contractNo.isEmpty()) {
            from.append("and bci.no like ? ");
            params.add("%" + contractNo + "%");
        }

        if (contractName != null && !contractName.isEmpty()) {
            from.append("and bc.name like ? ");
            params.add("%" + contractName + "%");
        }

        if (purchaserHqCode != null && !purchaserHqCode.isEmpty()) {
            from.append("and p.purchaserHqCode like ? ");
            params.add("%" + purchaserHqCode + "%");
        }

        if (scheduleCode != null && !scheduleCode.isEmpty()) {
            from.append("and p.scheduleCode like ? ");
            params.add("%" + scheduleCode + "%");
        }

        if (status != null && !status.isEmpty()) {
            from.append("and p.status = ? ");
            params.add(status);
        }

        // 添加排序
        from.append("order by p.id desc");

        // 执行分页查询
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public PlSchedulePlan findById(int id) {
        return dao.findFirst("select * from pl_schedule_plan where id = ? ", id);
    }

    public boolean save(PlSchedulePlan pl_schedule_plan) {
        if (pl_schedule_plan.getScheduleCode() == null || pl_schedule_plan.getScheduleCode().isEmpty()) {
            pl_schedule_plan.setScheduleCode(basNoService.getNewNoNyName("pcjh"));
        }
        return pl_schedule_plan.save();
    }

    public boolean update(PlSchedulePlan pl_schedule_plan) {
        if (pl_schedule_plan.getScheduleCode() == null || pl_schedule_plan.getScheduleCode().isEmpty()) {
            pl_schedule_plan.setScheduleCode(basNoService.getNewNoNyName("pcjh"));
        }
        return pl_schedule_plan.update();
    }

    public boolean DeleteById(int id) {
        return dao.deleteById(id);
    }

    public boolean batchDelete(List<Integer> ids) {
        return dao.deleteByIds(ids);
    }

    public boolean updateStatus(String id, String status) {
        return Db.update("update pl_schedule_plan set status = ? where id = ? ", status, id) > 0;
    }

    public List<Record> getContractItemList(String contractNo) {
        StringBuilder selectSql = new StringBuilder(
                "SELECT psp.id," +
                        "c.id AS contractItemId," +
                        "c.itemnum, c.itemunit, c.itemRealPrice, c.itemRealSum, c.itemweight, c.itemgrossweight, " +
                        "c.poItemCode, c.poItemId, c.poItemNo, c.itemmemo, " +
                        "i.no AS itemNo, i.name AS itemName, i.spec AS itemSpec, " +
                        "psp.scheduleCode, psp.planPeriod, psp.planStartDate, psp.planFinishDate, " +
                        "psp.actualStartDate, psp.actualFinishDate, psp.dueDate, psp.remark, " +
                        "psp.actualPeriod, psp.status," +
                        "(select COALESCE(sum(po.amount), 0) from pl_production_order po where po.scheduleCode = psp.scheduleCode) as allocatedAmount "
        );

        StringBuilder fromSql = new StringBuilder(
                "FROM bascontractitem c " +
                        "LEFT JOIN pl_schedule_plan psp ON psp.poItemId = c.id " + // ✅ 注意JOIN方向反转
                        "LEFT JOIN basitem i ON c.itemid = i.id " +
                        "WHERE c.no = ? AND c.isdelete = 0 " +
                        "ORDER BY psp.status"
        );

        List<Object> params = new ArrayList<>();
        params.add(contractNo);

        // ✅ 拼接完整 SQL
        String sql = selectSql.append(fromSql).toString();

        return Db.find(sql, params.toArray());
    }


}