package com.xlerp.api.PlWorkOrder.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlWorkOrder;

import java.util.List;

public class PlWorkOrderService {
    private static final PlWorkOrder dao = new PlWorkOrder();

    public Page<PlWorkOrder> paginate(int pageNumber, int pageSize) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from pl_work_order");


        from.append(" order by id desc");

// 准备参数
        List<Object> params = new java.util.ArrayList<>();

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public PlWorkOrder findById(int id) {
        return dao.findFirst("select * from pl_work_order where id = ? ", id);
    }

    public boolean save(PlWorkOrder pl_work_order) {
        return pl_work_order.save();
    }

    public boolean update(PlWorkOrder pl_work_order) {
        return pl_work_order.update();
    }

    public boolean DeleteById(int id) {
        return dao.deleteById( id);
    }

    public boolean batchDelete(List<Integer> ids) {

        return dao.deleteByIds(ids);
    }
}