package com.xlerp.api.PlManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.Pldingdanitem;
import com.xlerp.common.model.Plgongdanitem;
import com.xlerp.common.model.Plshengchangongdan;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlshengchangongdanService {
    private static final Plshengchangongdan dao = new Plshengchangongdan();

    public Page<Plshengchangongdan> paginate(int pageNumber, int pageSize,String woNo,String contractNo) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from plshengchangongdan where isdelete = 0");

// 动态构建查询条件
        if (StrKit.notBlank(woNo))
            from.append(" and woNo like ?");
        if (StrKit.notBlank(contractNo))
            from.append(" and contractNo like ?");
        from.append(" order by id desc");

// 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(woNo))
            params.add("%" + woNo + "%");
        if (StrKit.notBlank(contractNo))
            params.add("%" + contractNo + "%");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Page<Record> paginateByDepNo(int pageNumber, int pageSize,String woNo,String contractNo,String depNo) {
        String select = "SELECT ssgd.*, c.name AS contractName";
        StringBuilder from = new StringBuilder("FROM plshengchangongdan ssgd " +
                "LEFT JOIN bascontract c ON ssgd.contractNo = c.no " +
                "WHERE ssgd.isdelete = 0");

        // 动态构建查询条件
        List<Object> params = new ArrayList<>();

        if (StrKit.notBlank(woNo)){
            from.append(" AND ssgd.woNo LIKE ?");
            params.add("%" + woNo + "%");
        }
        if (StrKit.notBlank(contractNo)) {
            from.append(" AND ssgd.contractNo LIKE ?");
            params.add("%" + contractNo + "%");
        }
        if (StrKit.notBlank(depNo)) {
            from.append(" AND ssgd.woNo IN (SELECT gi.woNo FROM plgongdanitem gi left join pldingdanitem di on gi.dingdanitemId = di.id WHERE di.workshopName LIKE ?)");
            params.add("%" + depNo + "%");
        }

        from.append(" ORDER BY ssgd.id DESC");

        return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }


    public Plshengchangongdan findById(int id) {
        return dao.findFirst("select * from plshengchangongdan where id = ? and isdelete = 0", id);
    }

    public boolean save(Plshengchangongdan plshengchangongdan) {
        System.out.println("service保存工单信息"+plshengchangongdan);
        return plshengchangongdan.save();
    }

    public boolean update(Plshengchangongdan plshengchangongdan) {
        return plshengchangongdan.update();
    }

    public boolean logicalDeleteById(int id) {
        return Db.update("update plshengchangongdan set isdelete = 1 where id = ? and isdelete = 0", id) > 0;
    }

    public boolean batchLogicalDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "update plshengchangongdan set isdelete = 1 where id in (" + placeholders + ") and isdelete = 0";
        return Db.update(sql, ids.toArray()) > 0;
    }


    private static final Plgongdanitem itemDao = new Plgongdanitem();

    public List<Record> getGongdanItemByNo(String woNo) {
        return Db.find("select gi.*,di.workshopName from plgongdanitem gi left join pldingdanitem di on gi.dingdanitemId = di.id where gi.woNo = ?", woNo);
    }

    public boolean saveGongdanItem(Plgongdanitem item) {
        return item.save();
    }

    public boolean updateGongdanItem(Plgongdanitem item) {
        return item.update();
    }

    public boolean deleteGongdanItem(int id) {
        return itemDao.deleteById( id);
    }

    //学姐加的
    public Plshengchangongdan findByWoNo(String woNo) {
        return dao.findFirst("select * from plshengchangongdan where woNo = ? and isdelete = 0", woNo);
    }


    public List<Record> getGongdanItemByNoAndDepNo(String woNo, String depNo) {
        StringBuilder sql = new StringBuilder(
                "SELECT gi.*, di.workshopName FROM plgongdanitem gi LEFT JOIN pldingdanitem di ON gi.dingdanitemId = di.id WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (StrKit.notBlank(woNo)) {
            sql.append(" AND gi.woNo = ?");
            params.add(woNo);
        }
        if (StrKit.notBlank(depNo)) {
            sql.append(" AND di.workshopName LIKE ?");
            params.add("%" + depNo + "%");
        }

        return Db.find(sql.toString(), params.toArray());
    }
}