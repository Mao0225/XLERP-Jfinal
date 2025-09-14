package com.xlerp.api.PlSchedulePlan.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlSchedulePlan;

import java.util.ArrayList;
import java.util.List;

public class PlSchedulePlanService {
    private static final PlSchedulePlan dao = new PlSchedulePlan();

    public Page<PlSchedulePlan> paginate(int pageNumber, int pageSize,
                                         String contractNo, String contractName,
                                         String purchaserHqCode, String scheduleCode,
                                         String status) {
        // 构建查询字段
        String select = "select p.*,bc.no as contractNo,bc.name as contractName,bci.itemnum as amount, bi.name as itemName, bi.spec as itemSpec,bci.itemunit as itemUnit";

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
        return pl_schedule_plan.save();
    }

    public boolean update(PlSchedulePlan pl_schedule_plan) {
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
}