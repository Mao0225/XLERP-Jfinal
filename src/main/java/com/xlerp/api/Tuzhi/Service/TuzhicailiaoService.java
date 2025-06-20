package com.xlerp.api.Tuzhi.Service;

import com.xlerp.common.model.Bastuzhicailiao;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;

public class TuzhicailiaoService {
    private static final Bastuzhicailiao dao = new Bastuzhicailiao();

    public Page<Record> paginate(int pageNumber, int pageSize, String tuzhiid) {
        String select = "select b.\"id\",b.\"tuzhiid\",b.\"basitemid\",b.\"shuliang\",b.\"flag\",b.\"type\" as tuzhitype,b.\"memo\",b.\"writetime\",b.\"writer\",t.\"tuzhibianhao\",i.\"no\", i.\"name\", i.\"unit\",i.\"spec\",i.\"description\",i.\"color\",i.\"weight\",i.\"inclass\",i.\"type\" as cailiaotype";
        String from = "from XLQCERP.\"bastuzhicailiao\" b " +
                "LEFT JOIN XLQCERP.\"bastuzhi\" t on b.\"tuzhiid\" = t.\"id\" " +
                "LEFT JOIN XLQCERP.\"basitem\" i on b.\"basitemid\" = i.\"id\" " +
                "where 1 = 1";

        // 修改逻辑：如果 tuzhiid 为空，添加一个永远不成立的条件
        if (tuzhiid == null || tuzhiid.isEmpty()) {
            from += " and 1 = 0"; // 永远不成立的条件，确保不返回任何数据
        } else {
            from += " and b.\"tuzhiid\" = " + tuzhiid;
        }

        from += " order by b.\"id\" desc";

        return Db.paginate(pageNumber, pageSize, select, from);
    }
    public Bastuzhicailiao findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Bastuzhicailiao tuzhicailiao) {
        return tuzhicailiao.save();
    }

    public boolean update(Bastuzhicailiao tuzhicailiao) {
        return tuzhicailiao.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    public List<Record> getOptions() {
        return Db.find("select id, tuzhiid, basitemid from bastuzhicailiao order by id desc");
    }
}