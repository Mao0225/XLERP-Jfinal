package com.xlerp.api.Tuzhi.Service;

import com.xlerp.common.model.Bastuzhi;
import com.jfinal.plugin.activerecord.Page;

public class TuzhiService {
    private static final Bastuzhi dao = new Bastuzhi().dao();

    public Page<Bastuzhi> paginate(int pageNumber, int pageSize, String tuzhimingcheng) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from bastuzhi");
        if (tuzhimingcheng != null && !tuzhimingcheng.trim().isEmpty()) {
            from.append(" where tuzhimingcheng like ?");
        }
        from.append(" order by id desc");
        if (tuzhimingcheng != null && !tuzhimingcheng.trim().isEmpty()) {
            return dao.paginate(pageNumber, pageSize, select, from.toString(), "%" + tuzhimingcheng + "%");
        } else {
            return dao.paginate(pageNumber, pageSize, select, from.toString());
        }
    }

    public Bastuzhi findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Bastuzhi tuzhi) {
        return tuzhi.save();
    }

    public boolean update(Bastuzhi tuzhi) {
        return tuzhi.update();
    }

    public boolean deleteById(int id) {
        Bastuzhi tuzhi = dao.findById(id);
        if (tuzhi == null) {
            return false; // 图纸不存在，删除失败
        }
        return tuzhi.delete(); // 返回删除结果
    }
}