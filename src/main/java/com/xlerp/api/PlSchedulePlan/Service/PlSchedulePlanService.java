package com.xlerp.api.PlSchedulePlan.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlSchedulePlan;

import java.util.List;

public class PlSchedulePlanService {
    private static final PlSchedulePlan dao = new PlSchedulePlan();

    public Page<PlSchedulePlan> paginate(int pageNumber, int pageSize) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from pl_schedule_plan");



        from.append(" order by id desc");

// 准备参数
        List<Object> params = new java.util.ArrayList<>();


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
}