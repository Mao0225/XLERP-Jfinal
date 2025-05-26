package com.xlerp.api.HrManagement.Service;

import com.jfinal.kit.StrKit;
import com.xlerp.api.ItemManagement.Controller.BasItemController;
import com.xlerp.common.model.Hruser;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;

public class HruserService {
    private static final Hruser dao = new Hruser();

    public Page<Hruser> paginate(int pageNumber, int pageSize, String name, String no) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from hruser where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(name)) {
            from.append(" and name like ?");
        }
        if (StrKit.notBlank(no)) {
            from.append(" and no like ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(name)) {
            params.add("%" + name + "%");
        }
        if (StrKit.notBlank(no)) {
            params.add("%" + no + "%");
        }

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Hruser findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Hruser hruser) {
        return hruser.save();
    }

    public boolean update(Hruser hruser) {
        return hruser.update();
    }

    public boolean deleteById(int id) {
        return Db.update("update hruser set isdelete = -1 where id = ?", id) > 0;
    }

}