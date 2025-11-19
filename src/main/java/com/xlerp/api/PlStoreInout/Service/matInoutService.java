package com.xlerp.api.PlStoreInout.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlMatInoutList;

import java.util.ArrayList;
import java.util.List;

public class matInoutService {

    private static PlMatInoutList dao = new PlMatInoutList();

    public Page paginate(int pageNum, int pageSz, String type, String materialName) {
        // 构建查询字段
        String select = "select m.*";

        // 构建FROM子句和基础WHERE条件
        StringBuilder from = new StringBuilder("from pl_mat_inout_list m where 1=1");

        // 构建查询参数
        List<Object> params = new ArrayList<>();
        if (type != null && !type.isEmpty()) {
            from.append("and m.type = ? ");
            params.add(type);
        }
        if (materialName != null && !materialName.isEmpty()) {
            from.append("and m.materialName like ? ");
            params.add("%" + materialName + "%");
        }
        // 添加排序
        from.append("order by m.id desc");

        // 执行分页查询
        return dao.paginate(pageNum, pageSz, select, from.toString(), params.toArray());
    }
}
