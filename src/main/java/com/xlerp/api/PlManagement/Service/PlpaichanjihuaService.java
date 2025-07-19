package com.xlerp.api.PlManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.Plbeiliaojihua;
import com.xlerp.common.model.Plpaichanjihua;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlpaichanjihuaService {
    private static final Plpaichanjihua dao = new Plpaichanjihua();

    public Page<Plpaichanjihua> paginate(int pageNumber, int pageSize) {
        String select = "select p.*,gi.amount as gdamount,di.amount as ddamount," +
                "ci.itemnum as conamount,ci.itemprice," +
                "i.name as itemName ,i.no as itemNo ,i.spec,i.unit";
        StringBuilder from = new StringBuilder("from plpaichanjihua p ");
        from.append("left join plgongdanitem gi on gi.id = p.gdItemId ")
                .append("left join pldingdanitem di on di.id = gi.dingdanitemId ")
                .append("left join bascontractItem ci on ci.id = di.conitemId ")
                .append("left join basitem i on i.id = ci.itemid ")
                .append("where p.isdelete = 0 ");

        from.append("order by p.id desc");

// 准备参数
        List<Object> params = new java.util.ArrayList<>();

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Plpaichanjihua findById(int id) {
        return dao.findFirst("select * from plpaichanjihua where id = ? and isdelete = 0", id);
    }

    public boolean save(Plpaichanjihua plpaichanjihua) {
        return plpaichanjihua.save();
    }

    public boolean update(Plpaichanjihua plpaichanjihua) {
        return plpaichanjihua.update();
    }

    public boolean logicalDeleteById(int id) {
        return Db.update("update plpaichanjihua set isdelete = 1 where id = ? and isdelete = 0", id) > 0;
    }

    public boolean batchLogicalDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "update plpaichanjihua set isdelete = 1 where id in (" + placeholders + ") and isdelete = 0";
        return Db.update(sql, ids.toArray()) > 0;
    }


    private static final Plbeiliaojihua beiliaodao = new Plbeiliaojihua();



    public List<Plbeiliaojihua> beiliaojihuaList(String gdItemId) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.*, b.unit, b.spec, b.name, i.name as sxclname, i.unit as sxclunit,g.amount as dinghuotaoshupaichan ")
                .append("FROM plbeiliaojihua p ")
                .append("LEFT JOIN basitem b ON p.itemno = b.no ")
                .append("LEFT JOIN basitem i ON p.itemno = i.no ")
                .append("LEFT JOIN plgongdanitem g ON g.id = ? ")
                .append("LEFT JOIN pldingdanitem d ON g.dingdanitemId = d.id ")
                .append("LEFT JOIN bascontractitem ci ON d.conitemId = ci.id ")
                .append("WHERE 1=1 ")
                .append("AND p.noticeid = ci.noticeid ")
                .append("AND p.noticedrawno = ci.noticedrawno ")
                .append("ORDER BY p.id DESC");

        List<Object> params = new ArrayList<>();
        params.add(gdItemId);

        return beiliaodao.find(sql.toString(), params.toArray());
    }

}