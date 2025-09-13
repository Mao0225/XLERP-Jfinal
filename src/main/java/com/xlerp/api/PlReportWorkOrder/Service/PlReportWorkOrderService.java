package com.xlerp.api.PlReportWorkOrder.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlReportWorkOrder;

import java.util.List;

public class PlReportWorkOrderService {
    private static final PlReportWorkOrder dao = new PlReportWorkOrder();

    public Page<PlReportWorkOrder> paginate(int pageNumber, int pageSize) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from pl_report_work_order");


        from.append(" order by id desc");

// 准备参数
        List<Object> params = new java.util.ArrayList<>();

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public PlReportWorkOrder findById(int id) {
        return dao.findFirst("select * from pl_report_work_order where id = ? ", id);
    }

    public boolean save(PlReportWorkOrder pl_report_work_order) {
        return pl_report_work_order.save();
    }

    public boolean update(PlReportWorkOrder pl_report_work_order) {
        return pl_report_work_order.update();
    }

    public boolean DeleteById(int id) {
        return dao.deleteById( id);
    }

    public boolean batchDelete(List<Integer> ids) {

        return dao.deleteByIds(ids);
    }
}