package com.xlerp.api.PlManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.xlerp.common.model.Plshengchangongdan;

import java.util.List;
import java.util.stream.Collectors;

public class PlshengchangongdanService {
    private static final Plshengchangongdan dao = new Plshengchangongdan();

    public Page<Plshengchangongdan> paginate(int pageNumber, int pageSize,String woNo) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from plshengchangongdan where isdelete = 0");

// 动态构建查询条件
        if (StrKit.notBlank(woNo))
            from.append(" and wo_no like ?");

        from.append(" order by id desc");

// 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(woNo))
            params.add("%" + woNo + "%");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Plshengchangongdan findById(int id) {
        return dao.findFirst("select * from plshengchangongdan where id = ? and isdelete = 0", id);
    }

    public boolean save(Plshengchangongdan plshengchangongdan) {
        return plshengchangongdan.save();
    }

    public boolean update(Plshengchangongdan plshengchangongdan) {
        return plshengchangongdan.update();
    }

    public boolean logicalDeleteById(int id) {
        return Db.update("update plshengchangongdan set isdelete = 1 where id = ? and isdelete = 0", id) > 0;
    }

    public boolean batchLogicalDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "update plshengchangongdan set isdelete = 1 where id in (" + placeholders + ") and isdelete = 0";
        return Db.update(sql, ids.toArray()) > 0;
    }
}