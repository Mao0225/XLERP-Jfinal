package com.xlerp.api.Contract.Service;

import com.jfinal.kit.StrKit;
import com.xlerp.common.model.BasPurchaseOrder;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;

public class BasPurchaseOrderService {
    private static final BasPurchaseOrder dao = new BasPurchaseOrder();

    public Page<BasPurchaseOrder> paginate(int pageNumber, int pageSize, String poNo) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from basPurchaseOrder where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(poNo)) {
            from.append(" and poNo like ?");
        }//采购订单编码

//        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(poNo)) {
            params.add("%" + poNo + "%");
        }
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public BasPurchaseOrder findById(int id) {
        return dao.findById(id);
    }

    public boolean save(BasPurchaseOrder basPurchaseOrder) {
        basPurchaseOrder.setIsdelete(0);
        return basPurchaseOrder.save();
    }

    public boolean update(BasPurchaseOrder basPurchaseOrder) {
        return basPurchaseOrder.update();
    }

    public boolean deleteById(int id) {
        return Db.update("update baspurchaseorder set isdelete = -1 where id = ?", id) > 0;
    }

}