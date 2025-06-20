package com.xlerp.api.ItemManagement.Service;

import com.jfinal.kit.StrKit;
import com.xlerp.common.model.Basitem;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

public class BasItemService {
    private static final Basitem dao = new Basitem();

    public Page<Basitem> paginate(int pageNumber, int pageSize, String itemNo, String itemName, String inclass,  String type) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from basitem where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(itemNo)) {
            from.append(" and no like ?");
        }
        if (StrKit.notBlank(itemName)) {
            from.append(" and name like ?");
        }
        if (StrKit.notBlank(inclass)) {
            from.append(" and inclass like ?");
        }
        if (StrKit.notBlank(type)) {
            from.append(" and type like ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(itemNo)) {
            params.add("%" + itemNo + "%");
        }
        if (StrKit.notBlank(itemName)) {
            params.add("%" + itemName + "%");
        }
        if (StrKit.notBlank(inclass)) {
            params.add("%" + inclass + "%");
        }
        if (StrKit.notBlank(type)) {
            params.add("%" + type + "%");
        }

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Page<Basitem> tuzhiyuancailiaopaginate(int pageNumber, int pageSize, String itemNo, String itemName, String inclass,  String type) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from basitem where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(itemNo)) {
            from.append(" and no like ?");
        }
        if (StrKit.notBlank(itemName)) {
            from.append(" and name like ?");
        }
        if (StrKit.notBlank(inclass)) {
            from.append(" and inclass like ?");
        }
        if (StrKit.notBlank(type)) {
            from.append(" and type like ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(itemNo)) {
            params.add("%" + itemNo + "%");
        }
        if (StrKit.notBlank(itemName)) {
            params.add("%" + itemName + "%");
        }
        if (StrKit.notBlank(inclass)) {
            params.add("%" + inclass + "%");
        }
        if (StrKit.notBlank(type)) {
            params.add("%" + type + "%");
        }

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Basitem findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Basitem basItem) {
        return basItem.save();
    }

    public boolean update(Basitem basItem) {
        return basItem.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }
}