package com.xlerp.api.Tongzhi.Service;

import com.xlerp.common.model.Plbeiliaojihua;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.Db;

import java.util.ArrayList;
import java.util.List;

public class BeiliaojihuaService {
    private static final Plbeiliaojihua dao = new Plbeiliaojihua();

    public Page<Plbeiliaojihua> paginate(int pageNumber, int pageSize, String noticeid) {
        String select = "select *";
        String from = "from plbeiliaojihua";
        if (noticeid != null && !noticeid.equals("")) {
            from += " where noticeid = " + noticeid + "'";
        }
        from += " order by id desc";
        return dao.paginate(pageNumber, pageSize, select, from);
    }
    public Page<Plbeiliaojihua> beiliaojihuapaginate(int pageNumber, int pageSize, String noticeid, String noticedrawno) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT p.*, b.unit, b.spec,b.name, i.name as sxclname, i.unit as sxclunit"); // 修改SELECT子句

        StringBuilder from = new StringBuilder();
        from.append("FROM plbeiliaojihua p ")
                .append("LEFT JOIN basitem b ON p.itemno = b.no ")
                .append("LEFT JOIN basitem i ON p.sxclitemno = i.no ") // 添加JOIN子句
                .append("WHERE 1=1");

        // 使用预编译语句的参数化查询，避免SQL注入
        List<Object> params = new ArrayList<>();
        if (noticeid != null && !noticeid.isEmpty()) {
            from.append(" AND p.noticeid = ?");
            params.add(noticeid);
        }
        if (noticedrawno != null && !noticedrawno.isEmpty()) {
            from.append(" AND p.noticedrawno = ?");
            params.add(noticedrawno);
        }

        from.append(" ORDER BY p.id DESC"); // 使用plbeiliaojihua表的id字段排序

        // 使用带参数的paginate方法
        return dao.paginate(pageNumber, pageSize, select.toString(), from.toString(), params.toArray());
    }
    public Page<Plbeiliaojihua> beiliaojihuabynoticepaginate(int pageNumber, int pageSize, String noticeid) {
        StringBuilder select = new StringBuilder();
        select.append("SELECT p.*, b.unit, b.spec,b.name, i.name as sxclname, i.unit as sxclunit"); // 修改SELECT子句

        StringBuilder from = new StringBuilder();
        from.append("FROM plbeiliaojihua p ")
                .append("LEFT JOIN basitem b ON p.itemno = b.no ")
                .append("LEFT JOIN basitem i ON p.itemno = i.no ") // 添加JOIN子句
                .append("WHERE 1=1");

        // 使用预编译语句的参数化查询，避免SQL注入
        List<Object> params = new ArrayList<>();
        if (noticeid != null && !noticeid.isEmpty()) {
            from.append(" AND p.noticeid = ?");
            params.add(noticeid);
        }

        from.append(" ORDER BY p.id DESC"); // 使用plbeiliaojihua表的id字段排序

        // 使用带参数的paginate方法
        return dao.paginate(pageNumber, pageSize, select.toString(), from.toString(), params.toArray());
    }
    public Plbeiliaojihua findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Plbeiliaojihua beiliaojihua) {
        return beiliaojihua.save();
    }

    public boolean update(Plbeiliaojihua beiliaojihua) {
        return beiliaojihua.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    public List<Record> getOptions() {
        return Db.find("select id, contractname from plbeiliaojihua order by id desc");
    }
}