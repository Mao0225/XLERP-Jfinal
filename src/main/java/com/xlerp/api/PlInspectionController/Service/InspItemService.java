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

    public List<PlInspItem> getList(String param) {
        // 基础 SQL（where 1=1 方便后续拼接 and 条件）
        StringBuilder sql = new StringBuilder("select * from pl_insp_item where 1=1 ");
        // 存储查询参数（避免 SQL 注入，也方便动态添加）
        List<Object> params = new ArrayList<>();

        // 判断 param 不为 null 且不为空字符串（trim() 可选，根据需求是否忽略空格）
        if (param != null && !param.trim().isEmpty()) {
            // 拼接两个 like 条件（inspItemCode 和 inspItemName 都模糊匹配 param）
            sql.append("and inspItemCode like ? ");
            sql.append("and inspItemName like ? ");
            // 添加参数（前后加 % 实现模糊匹配）
            String likeParam = "%" + param.trim() + "%";
            params.add(likeParam);
            params.add(likeParam);
        }

        // 拼接排序条件
        sql.append("order by id desc");

        // 执行查询（注意：params 转成数组传入，MyBatis/IBatis 会自动对应 ? 占位符）
        return dao.find(sql.toString(), params.toArray());
    }
}
