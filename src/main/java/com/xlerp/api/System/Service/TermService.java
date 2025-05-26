package com.xlerp.api.System.Service;

import com.xlerp.common.model.Systerm;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

public class TermService {
    private static final Systerm dao = new Systerm().dao();

    public Page<Systerm> paginate(int pageNumber, int pageSize, String term) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from systerm");
        if (term != null && !term.trim().isEmpty()) {
            from.append(" where term like ? and status = 0");
            from.append(" order by id desc");
            return dao.paginate(pageNumber, pageSize, select, from.toString(), "%" + term + "%");
        } else {
            from.append(" where status = 0");
            from.append(" order by id desc");
            return dao.paginate(pageNumber, pageSize, select, from.toString());
        }
    }

    public Systerm findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Systerm term) {
        term.setStatus(0); // 默认为0
        return term.save();
    }

    public boolean update(Systerm term) {
        return term.update();
    }

    public boolean deleteById(int id) {
        Systerm term = dao.findById(id);
        if (term == null) {
            return false; // 期间不存在，删除失败
        }
        term.setStatus(-1); // 设置为删除状态
        return term.update(); // 返回更新结果
    }

    public List<Systerm> getAll() {
        return dao.find("select * from systerm");
    }
}