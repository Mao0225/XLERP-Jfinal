package com.xlerp.api.PlReportWorkOrder.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlReportWorkOrder;

import java.util.ArrayList;
import java.util.List;

public class PlReportWorkOrderService {
    private static final PlReportWorkOrder dao = new PlReportWorkOrder();


    public Page<PlReportWorkOrder> paginate(int pageNumber, int pageSize,
                                            String contractNo, String contractName, String reportNo,
                                            String status) {
        // 构建查询字段
        String select = "select p.*,bc.no as contractNo,bc.name as contractName,bci.itemnum as contractAmount, bi.name as itemName," +
                " bi.spec as itemSpec,bci.itemunit as itemUnit,pwo.amount as woAmount,pwo.materialsCode as itemCode";

        // 构建FROM子句和基础WHERE条件
        StringBuilder from = new StringBuilder("from pl_report_work_order p ");
        from.append("left join pl_production_order ppo on ppo.ipoNo = p.ipoNo ");
        from.append("left join pl_work_order pwo on pwo.woNo = p.woNo ");
        from.append("left join bascontractitem bci on ppo.poItemId = bci.id ");
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

        if (reportNo != null && !reportNo.isEmpty()) {
            from.append("and p.reportNo like ? ");
            params.add("%" + reportNo + "%");
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

    public boolean updateStatus(String id, String status) {
        return Db.update("update pl_report_work_order set status = ? where id = ? ", status, id) > 0;
    }

    public List<PlReportWorkOrder> findBywoNo(String woNo,String processCode) {
        if (processCode != null && !processCode.isEmpty()) {
            return dao.find("select * from pl_report_work_order where woNo = ? and processCode = ? ", woNo, processCode);
        }
        return dao.find("select * from pl_report_work_order where woNo = ? ", woNo);
    }
}