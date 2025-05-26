package com.xlerp.api.System.Service;

import com.xlerp.common.model.Basdepartment;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;

public class BasDepartmentService {
    private static final Basdepartment dao = new Basdepartment();

    public Page<Basdepartment> paginate(int pageNumber, int pageSize, String name) {
        String select = "select *";
        String from = "from basdepartment where status = 0";
        if (name != null && !name.equals("")) {
            from += " and name like '%" + name + "%'";
        }
        from += " order by id desc";
        return dao.paginate(pageNumber, pageSize, select, from);
    }

    public Basdepartment findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Basdepartment basDepartment) {
        return basDepartment.save();
    }

    public boolean update(Basdepartment basDepartment) {
        return basDepartment.update();
    }


    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    public List<Record> getOptions() {
        return Db.find("select id, name from basdepartment where status = 0 order by id desc");
    }
}