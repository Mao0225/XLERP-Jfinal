package com.xlerp.api.PlPurchaseOrder.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlPurchaseOrder;

import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderService {
    private static final PlPurchaseOrder dao = new PlPurchaseOrder();
    public Page<PlPurchaseOrder> paginate(int pageNumber, int pageSize, String purchaseOrderNo, String status) {
        // 构建查询字段
        String select = "select p.*";

        // 构建FROM子句和基础WHERE条件
        StringBuilder from = new StringBuilder("from pl_purchase_order p where 1=1");

        // 构建查询参数
        List<Object> params = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            from.append("and p.status = ? ");
            params.add(status);
        }
        if (purchaseOrderNo != null && !purchaseOrderNo.isEmpty()) {
            from.append("and p.purchaseOrderNo like ? ");
            params.add("%" + purchaseOrderNo + "%");
        }
        // 添加排序
        from.append("order by p.id desc");

        // 执行分页查询
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public List getMaterialList(String purchaseOrderNo) {


        String select = "SELECT cm.*,i.no as itemNo,i.name as itemName,i.spec as itemSpec,i.inclass,i.unit ";
        String from = "FROM bas_contract_material cm " +
                "LEFT JOIN basitem i ON cm.itemId = i.id " +
                "WHERE cm.purchaseOrderNo = ? " +
                "ORDER BY cm.id";

        // 拼接完整的SQL语句
        String sql = select + from;
        return Db.find(sql, purchaseOrderNo);
    }

    public boolean deleteById(String id) {
        return dao.deleteById(id);
    }

    public boolean setPurchaseOrderNo(String purchaseOrderNo, String materialIds) {

        String sql = "update bas_contract_material set purchaseOrderNo = ? where id in (" + materialIds + ")";

        // 3. 执行更新（此时只有 1 个占位符，对应 purchaseOrderNo）
        int rows = Db.update(sql, purchaseOrderNo);

        return rows > 0;
    }
}
