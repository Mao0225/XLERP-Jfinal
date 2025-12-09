package com.xlerp.api.PlStoreInout.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlFinishInoutList;

import java.util.ArrayList;
import java.util.List;

public class finishInoutService {

    private static PlFinishInoutList dao = new PlFinishInoutList();

    public Page paginate(int pageNum, int pageSz, String type, String itemName) {
        // 构建查询字段
        String select = "select m.*";

        // 构建FROM子句和基础WHERE条件
        StringBuilder from = new StringBuilder("from pl_finish_inout_list m where 1=1");

        // 构建查询参数
        List<Object> params = new ArrayList<>();
        if (type != null && !type.isEmpty()) {
            from.append("and m.type = ? ");
            params.add(type);
        }
        if (itemName != null && !itemName.isEmpty()) {
            from.append("and m.itemName like ? ");
            params.add("%" + itemName + "%");
        }
        // 添加排序
        from.append("order by m.id desc");

        // 执行分页查询
        return dao.paginate(pageNum, pageSz, select, from.toString(), params.toArray());
    }
}
