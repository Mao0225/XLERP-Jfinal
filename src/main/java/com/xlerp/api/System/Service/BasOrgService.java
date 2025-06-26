package com.xlerp.api.System.Service;

import com.jfinal.kit.StrKit;
import com.xlerp.common.model.Basorg;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

public class BasOrgService {
    private static final Basorg dao = new Basorg();

    public Page<Basorg> paginate(int pageNumber, int pageSize,  String no,  String descr , String type) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from basorg where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(descr)) {
            from.append(" and descr like ?");
        }
        if (StrKit.notBlank(no)) {
            from.append(" and no like ?");
        }
        if (StrKit.notBlank(type)) {
            from.append(" and type = ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(descr)) {
            params.add("%" + descr + "%");
        }
        if (StrKit.notBlank(no)) {
            params.add("%" + no + "%");
        }
        if (StrKit.notBlank(type)) {
            params.add(type);
        }

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Basorg findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Basorg basOrg) {
        return basOrg.save();
    }

    public boolean update(Basorg basOrg) {
        return basOrg.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }
}