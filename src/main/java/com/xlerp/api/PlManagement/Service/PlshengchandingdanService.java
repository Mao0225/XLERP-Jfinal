package com.xlerp.api.PlManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.xlerp.common.model.Plshengchandingdan;

import java.util.List;
import java.util.stream.Collectors;

public class PlshengchandingdanService {
    private static final Plshengchandingdan dao = new Plshengchandingdan();

    public Page<Plshengchandingdan> paginate(int pageNumber, int pageSize, String ipoNo) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from plshengchandingdan where isdelete = 0");

// 动态构建查询条件
        if (StrKit.notBlank(ipoNo))
            from.append(" and ipoNo like ?");

        from.append(" order by id desc");

// 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(ipoNo)) {
            params.add("%" + ipoNo + "%");
        }


        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Plshengchandingdan findById(int id) {
        return dao.findFirst("select * from plshengchandingdan where id = ? and isdelete = 0", id);
    }

    public boolean save(Plshengchandingdan plshengchandingdan) {
        return plshengchandingdan.save();
    }

    public boolean update(Plshengchandingdan plshengchandingdan) {
        return plshengchandingdan.update();
    }

    public boolean logicalDeleteById(int id) {
        return Db.update("update plshengchandingdan set isdelete = 1 where id = ? and isdelete = 0", id) > 0;
    }

    public boolean batchLogicalDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "update plshengchandingdan set isdelete = 1 where id in (" + placeholders + ") and isdelete = 0";
        return Db.update(sql, ids.toArray()) > 0;
    }
}