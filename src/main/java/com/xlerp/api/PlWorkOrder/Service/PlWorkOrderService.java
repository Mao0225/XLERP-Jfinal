package com.xlerp.api.PlWorkOrder.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlWorkOrder;

import java.util.ArrayList;
import java.util.List;

public class PlWorkOrderService {
    private static final PlWorkOrder dao = new PlWorkOrder();

    public Page<PlWorkOrder> paginate(int pageNumber, int pageSize,
                                            String contractNo, String contractName, String woNo,
                                            String status) {
        // 构建查询字段
        String select = "select p.*,bc.no as contractNo,bc.name as contractName," +
                "bci.itemnum as contractAmount, bi.name as itemName," +
                "bci.itemunit as itemUnit ";

        // 构建FROM子句和基础WHERE条件
        StringBuilder from = new StringBuilder("from pl_work_order p ");
        from.append("left join pl_production_order po on po.ipoNo = p.ipoNo ");
        from.append("left join bascontractitem bci on po.poItemId = bci.id ");
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

        if (woNo != null && !woNo.isEmpty()) {
            from.append("and p.woNo like ? ");
            params.add("%" + woNo + "%");
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

    public boolean updateStatus(String id, String status) {
        return Db.update("update pl_work_order set status = ? where id = ? ", status, id) > 0;
    }

    public List<PlWorkOrder> findByIpoNo(String ipoNo) {
        return dao.find("select * from pl_work_order where ipoNo = ? ", ipoNo);
    }
}