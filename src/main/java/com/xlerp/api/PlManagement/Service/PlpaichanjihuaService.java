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

    public Page<Plpaichanjihua> paginate(int pageNumber, int pageSize,String contractNo,String woNo,String ipoNo,String scheduleCode) {
        String select = "SELECT p.*, gi.amount AS gdamount, di.amount AS ddamount, di.workshopName, " +
                "ci.itemnum AS conamount, ci.itemprice, ci.itemRealPrice, " +
                "i.name AS itemName, i.no AS itemNo, i.spec, i.unit";
        StringBuilder from = new StringBuilder(
                "FROM plpaichanjihua p " +
                        "LEFT JOIN plgongdanitem gi ON gi.id = p.gdItemId " +
                        "LEFT JOIN pldingdanitem di ON di.id = gi.dingdanitemId " +
                        "LEFT JOIN bascontractItem ci ON ci.id = di.conitemId " +
                        "LEFT JOIN basitem i ON i.id = ci.itemid " +
                        "WHERE p.isdelete = 0 "
        );
        List<Object> params = new ArrayList<>();

        // 动态添加条件
        if (StrKit.notBlank(contractNo)) {
            from.append(" AND p.contractNo = ?");
            params.add(contractNo);
        }
        if (StrKit.notBlank(woNo)) {
            from.append(" AND p.woNo = ?");
            params.add(woNo);
        }
        if (StrKit.notBlank(ipoNo)) {
            from.append(" AND p.ipoNo = ?");
            params.add(ipoNo);
        }
        if (StrKit.notBlank(scheduleCode)) {
            from.append(" AND p.scheduleCode = ?");
            params.add(scheduleCode);
        }
        // 排序
        from.append(" ORDER BY p.id DESC");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }


    public Page<Plpaichanjihua> paginateByDepNo(int pageNumber, int pageSize, String contractNo, String woNo, String ipoNo, String scheduleCode, String depNo) {
        String select = "SELECT p.*, gi.amount AS gdamount, di.amount AS ddamount, di.workshopName, " +
                "ci.itemnum AS conamount, ci.itemprice, ci.itemRealPrice, " +
                "i.name AS itemName, i.no AS itemNo, i.spec, i.unit";
        StringBuilder from = new StringBuilder(
                "FROM plpaichanjihua p " +
                        "LEFT JOIN plgongdanitem gi ON gi.id = p.gdItemId " +
                        "LEFT JOIN pldingdanitem di ON di.id = gi.dingdanitemId " +
                        "LEFT JOIN bascontractItem ci ON ci.id = di.conitemId " +
                        "LEFT JOIN basitem i ON i.id = ci.itemid " +
                        "WHERE p.isdelete = 0 "
        );
        List<Object> params = new ArrayList<>();

        // 动态添加条件
        if (StrKit.notBlank(contractNo)) {
            from.append(" AND p.contractNo = ?");
            params.add(contractNo);
        }
        if (StrKit.notBlank(woNo)) {
            from.append(" AND p.woNo = ?");
            params.add(woNo);
        }
        if (StrKit.notBlank(ipoNo)) {
            from.append(" AND p.ipoNo = ?");
            params.add(ipoNo);
        }
        if (StrKit.notBlank(scheduleCode)) {
            from.append(" AND p.scheduleCode = ?");
            params.add(scheduleCode);
        }
        if (StrKit.notBlank(depNo)) {
            from.append(" AND di.workshopName LIKE ?");
            params.add("%" + depNo + "%");
        }

        // 排序
        from.append(" ORDER BY p.id DESC");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }
    /**
     * 根据ID查询排产计划
     * @param id 排产计划ID
     * @return 排产计划对象，包含关联信息
     */
    public Plpaichanjihua findById(int id) {
        String sql = "select pcjh.*, gi.amount, di.itemname, di.unit, " +
                "di.productModel,di.workshopName " +
                "from plpaichanjihua pcjh " +
                "left join plgongdanitem gi on gi.id = pcjh.gdItemId " +
                "left join pldingdanitem di on gi.dingdanitemId = di.id " +
                "where pcjh.id = ? and pcjh.isdelete = 0";

        try {
            return dao.findFirst(sql, id);
        } catch (Exception e) {
            // 简单异常处理，可根据实际情况调整
            e.printStackTrace();
            return null;
        }
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