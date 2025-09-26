package com.xlerp.api.PlProductionOrder.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlProductionOrder;

import java.util.ArrayList;
import java.util.List;

public class PlProductionOrderService {
    private static final PlProductionOrder dao = new PlProductionOrder();


    public Page<PlProductionOrder> paginate(int pageNumber, int pageSize,
                                         String contractNo, String contractName,
                                         String scheduleCode, String ipoNo,
                                         String status) {
        // 构建查询字段
        String select = "select p.*,bc.no as contractNo,bc.name as contractName,bci.itemnum as contractAmount, bi.name as itemName, bi.spec as itemSpec,bci.itemunit as itemUnit," +
                "(select COALESCE(sum(pwo.amount), 0) from pl_work_order pwo where pwo.ipoNo = p.ipoNo) as allocatedAmount";

        // 构建FROM子句和基础WHERE条件
        StringBuilder from = new StringBuilder("from pl_production_order p ");
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

        if (scheduleCode != null && !scheduleCode.isEmpty()) {
            from.append("and p.scheduleCode like ? ");
            params.add("%" + scheduleCode + "%");
        }

        if (ipoNo != null && !ipoNo.isEmpty()) {
            from.append("and p.ipoNo like ? ");
            params.add("%" + ipoNo + "%");
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

    public PlProductionOrder findById(int id) {
        return dao.findFirst("select * from pl_production_order where id = ? ", id);
    }

    public boolean save(PlProductionOrder pl_production_order) {
        return pl_production_order.save();
    }

    public boolean update(PlProductionOrder pl_production_order) {
        return pl_production_order.update();
    }

    public boolean DeleteById(int id) {
        return dao.deleteById( id);
    }

    public boolean batchDelete(List<Integer> ids) {

        return dao.deleteByIds(ids);
    }


    public boolean updateStatus(String id, String status) {
        return Db.update("update pl_production_order set status = ? where id = ? ", status, id) > 0;
    }

    public List<PlProductionOrder> findByScheduleCode(String scheduleCode) {
        return dao.find("select * from pl_production_order where scheduleCode = ? ", scheduleCode);
    }
}