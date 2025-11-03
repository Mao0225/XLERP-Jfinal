package com.xlerp.api.PlInspectionController.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlInspItem;

import java.util.ArrayList;
import java.util.List;

public class InspItemService {
    private static final PlInspItem dao = new PlInspItem();

    public Page<PlInspItem> paginate(int pageNumber, int pageSize, String param) {
        String select = "select *";
        // 核心：先加 where 1=1，后续条件统一用 and 拼接
        StringBuilder from = new StringBuilder("from pl_insp_item where 1=1 ");
        List<Object> params = new ArrayList<>();

        // 只判断一次param，有值就拼接两个 and 条件
        if (StrKit.notBlank(param)) {
            from.append("and inspItemCode like ? "); // 统一用 and
            from.append("and inspItemName like ? "); // 统一用 and
            params.add("%" + param + "%");
            params.add("%" + param + "%");
        }

        from.append("order by id desc");
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public PlInspItem findById(int id) {
        return dao.findById(id);
    }

    public boolean save(PlInspItem PlInspItem) {
        return PlInspItem.save();
    }

    public boolean update(PlInspItem PlInspItem) {
        return PlInspItem.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }
}
