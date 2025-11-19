package com.xlerp.api.PlPurchaseOrder.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlPurchaseOrder;

import java.sql.SQLException;
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




    public boolean deleteByOrderNo(String purchaseOrderNo) {
        // 1. 严格参数校验（避免空值/空字符串导致无效SQL）
        if (purchaseOrderNo == null || purchaseOrderNo.trim().isEmpty()) {
            return false;
        }

        // 2. 用 JFinal 的 IAtom 实现事务（保证 update 和 delete 原子性）
        boolean result = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                // 步骤1：修正 update SQL（逗号分隔字段，不清空 purchaseOrderNo）
                String updateSql = "update bas_contract_material " +
                        "set actualQuantity = NULL, " +  // 修复：用逗号分隔字段（原 and 语法错误）
                        "standard = NULL, " +
                        "material = NULL, " +
                        "orderMemo = NULL," +
                        "purchaseOrderNo = NULL " +
                        "where purchaseOrderNo = ?";  // 条件字段不清空，否则无法匹配

                // 执行更新（JFinal Db.update 返回影响行数）
                Db.update(updateSql, purchaseOrderNo);

                // 步骤2：删除 pl_purchase_order 表对应记录
                String deleteSql = "delete from pl_purchase_order where purchaseOrderNo = ?";
                int deleteCount = Db.delete(deleteSql, purchaseOrderNo);

                // 返回 true 表示事务提交，false 表示回滚（这里只要删除成功就提交）
                return deleteCount > 0;
            }
        });

        return result;
    }

    public boolean updateStatus(String id, String status) {
        return Db.update("update pl_purchase_order set status = ? where id = ?", status, id) >0;
    }
}
