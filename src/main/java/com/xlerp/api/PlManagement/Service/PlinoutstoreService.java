package com.xlerp.api.PlManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.Plinoutstore;

import java.util.List;
import java.util.stream.Collectors;

public class PlinoutstoreService {
    private static final Plinoutstore dao = new Plinoutstore();

    public Page<Plinoutstore> paginate(int pageNumber, int pageSize, String storeNo, String storeName, String type) {
        // 选择需要查询的字段，包括公共字段和关联表的合同名称
        String select = "select p.orderno, p.deliverunit, p.store, p.handleperson, p.receivedate, p.term, p.contractNo, c.name as contractName";

        // 构建SQL的FROM和WHERE部分，关联bascontract表，筛选未删除且isin=1的记录
        StringBuilder from = new StringBuilder("from plinoutstore p ");
        from.append("left join bascontract c on p.contractNo = c.no ")
                .append("where p.isdelete = 0 and p.isin = 1 ");

        // 动态构建查询条件
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(storeNo)) {
            from.append(" and p.storeNo like ?");
            params.add("%" + storeNo + "%"); // 添加仓库编号模糊查询参数
        }
        if (StrKit.notBlank(storeName)) {
            from.append(" and p.storeName like ?");
            params.add("%" + storeName + "%"); // 添加仓库名称模糊查询参数
        }
        if (StrKit.notBlank(type)) {
            from.append(" and p.type like ?");
            params.add("%" + type + "%"); // 添加类型模糊查询参数
        }

        // 按orderno和公共字段分组，确保相同orderno的记录合并
        from.append(" group by p.orderno, p.deliverunit, p.store, p.handleperson, p.receivedate, p.term, p.contractNo, c.name");
        // 按orderno降序排序
        from.append(" order by p.orderno desc");

        // 执行分页查询，返回结果
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Plinoutstore findById(int id) {
        return dao.findFirst("select * from plinoutstore where id = ? and isdelete = 0", id);
    }


    public List<Plinoutstore> findByOrderNo(String orderNo) {
        return dao.find("select * from plinoutstore where orderno = ? and isdelete = 0", orderNo);
    }


    public Plinoutstore findByOrderNoFirst(String orderNo) {
        return dao.findFirst("select p.orderno, p.flag,p.deliverunit, p.store, p.handleperson, p.receivedate, p.term, p.contractNo from plinoutstore p where p.orderno = ? and p.isdelete = 0", orderNo);
    }

    public boolean save(Plinoutstore plinoutstore) {
        return plinoutstore.save();
    }

    public boolean update(Plinoutstore plinoutstore) {
        return plinoutstore.update();
    }

    public boolean logicalDeleteById(int id) {
        return Db.update("update plinoutstore set isdelete = 1 where id = ? and isdelete = 0", id) > 0;
    }

    public boolean batchLogicalDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "update plinoutstore set isdelete = 1 where id in (" + placeholders + ") and isdelete = 0";
        return Db.update(sql, ids.toArray()) > 0;
    }

    public boolean deleteByOrderNo(String orderNo) {
        return Db.update("update plinoutstore set isdelete = 1 where orderno = ? and isdelete = 0", orderNo) > 0;
    }
}