package com.xlerp.api.BasProcessRoute.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.BasProcessRoute;
import com.xlerp.common.model.Basitem;

import java.util.ArrayList;
import java.util.List;

public class ProcessRouteService {

    private static final BasProcessRoute dao = new BasProcessRoute().dao();
    private static final Basitem itemDao = new Basitem().dao();
    public boolean deleteById(int i) {
        return dao.deleteById(i);
    }

    public BasProcessRoute findById(int i) {
        return dao.findById(i);
    }

    public List<BasProcessRoute> getByItemId(int itemId) {
        String sql = "select * from bas_process_route where itemId = ?";
        return dao.find(sql, itemId);
    }


    public Page<Basitem> itemPaginate(int pageNumber, int pageSize, String itemNo, String itemName, String firstClassId, String secondClassId, String spec) {
        // 核心：关联basitem和bas_item_class（物料的classId对应三级分类ID）
        // 思路：通过三级分类找二级分类，再通过二级分类找一级分类
        String select = "select b.*"; // 只查询物料表字段
        StringBuilder from = new StringBuilder(
                "from basitem b " +
                        "left join bas_item_class c3 on b.classId = c3.id " + // 物料-三级分类
                        "left join bas_item_class c2 on c3.parentId = c2.id " + // 三级-二级分类
                        "left join bas_item_class c1 on c2.parentId = c1.id " + // 二级-一级分类
                        "where b.isdelete = 0 "
        );

        List<Object> params = new ArrayList<>();

        // 物料编号筛选
        if (StrKit.notBlank(itemNo)) {
            from.append("and b.no like ? ");
            params.add("%" + itemNo + "%");
        }

        // 物料名称筛选
        if (StrKit.notBlank(itemName)) {
            from.append("and b.name like ? ");
            params.add("%" + itemName + "%");
        }
        // 规格筛选
        if (StrKit.notBlank(spec)) {
            from.append("and b.spec like ? ");
            params.add("%" + spec + "%");
        }

        // 一级分类筛选（匹配一级分类ID）
        if (StrKit.notBlank(firstClassId)) {
            from.append("and c1.id = ? "); // c1是一级分类表别名
            params.add(firstClassId);
        }

        // 二级分类筛选（匹配二级分类ID）
        if (StrKit.notBlank(secondClassId)) {
            from.append("and c2.id = ? "); // c2是二级分类表别名
            params.add(secondClassId);
        }

        // 排序
        from.append("order by b.id desc");
        Page<Basitem> page = itemDao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());

        // 5. 【新增逻辑】遍历分页结果，根据 itemId 填充工序列表
        // JFinal 的 Page.getList() 返回的是 Model 列表
        for (Basitem basitem : page.getList()) {
            // 获取 Id
            Integer itemId = basitem.getInt("id");

            if (itemId != null) {
                // 调用你写好的 getByItemId 方法
                List<BasProcessRoute> routes = getByItemId(itemId);

                // 将查询到的工序列表放入 Model 的额外属性中
                // 前端 JSON 会多出一个 "processRoutes" 字段
                basitem.put("processRoutes", routes);
            }
        }

        // 执行分页查询（注意：select和from要分开传，dao.paginate会自动处理count）
        return page;
    }
}
