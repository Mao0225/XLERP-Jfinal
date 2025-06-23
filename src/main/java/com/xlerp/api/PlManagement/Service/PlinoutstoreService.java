package com.xlerp.api.PlManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.xlerp.common.model.Plinoutstore;

import java.util.List;
import java.util.stream.Collectors;

public class PlinoutstoreService {
    private static final Plinoutstore dao = new Plinoutstore();

    public Page<Plinoutstore> paginate(int pageNumber, int pageSize, String storeNo, String storeName, String type) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from plinoutstore where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(storeNo)) {
            from.append(" and storeNo like ?");
        }
        if (StrKit.notBlank(storeName)) {
            from.append(" and storeName like ?");
        }
        if (StrKit.notBlank(type)) {
            from.append(" and type like ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(storeNo)) {
            params.add("%" + storeNo + "%");
        }
        if (StrKit.notBlank(storeName)) {
            params.add("%" + storeName + "%");
        }
        if (StrKit.notBlank(type)) {
            params.add("%" + type + "%");
        }

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Plinoutstore findById(int id) {
        return dao.findFirst("select * from plinoutstore where id = ? and isdelete = 0", id);
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
}