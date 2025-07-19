package com.xlerp.api.PlManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.Pldingdanitem;
import com.xlerp.common.model.Plshengchandingdan;

import java.util.List;
import java.util.stream.Collectors;

public class PlshengchandingdanService {
    private static final Plshengchandingdan dao = new Plshengchandingdan();

    public Page<Record> paginate(int pageNumber, int pageSize, String ipoNo ,String contractNo) {
        String select = "select ssdd.*,c.name as contractName";
        StringBuilder from = new StringBuilder("from plshengchandingdan ssdd " +
                "left join bascontract c on ssdd.contractNo = c.no " +
                "where ssdd.isdelete = 0");

// 动态构建查询条件
        if (StrKit.notBlank(ipoNo))
            from.append(" and ipoNo like ?");
        if (StrKit.notBlank(contractNo))
            from.append(" and ssdd.contractNo like ?");

        from.append(" order by id desc");

// 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(ipoNo)) {
            params.add("%" + ipoNo + "%");
        }
        if (StrKit.notBlank(contractNo)) {
            params.add("%" + contractNo + "%");
        }


        return Db.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Plshengchandingdan findById(int id) {
        return dao.findFirst("select * from plshengchandingdan where id = ? and isdelete = 0", id);
    }

    public boolean save(Plshengchandingdan plshengchandingdan) {
        return plshengchandingdan.save();
    }

    public boolean update(Plshengchandingdan plshengchandingdan) {
        return plshengchandingdan.update();
    }

    public boolean logicalDeleteById(int id) {
        return Db.update("update plshengchandingdan set isdelete = 1 where id = ? and isdelete = 0", id) > 0;
    }

    public boolean batchLogicalDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "update plshengchandingdan set isdelete = 1 where id in (" + placeholders + ") and isdelete = 0";
        return Db.update(sql, ids.toArray()) > 0;
    }


    private static final Pldingdanitem itemDao = new Pldingdanitem();

    public List<Record> getDingdanItemByNo(String ipoNo) {
        return Db.find("select * from pldingdanitem where ipoNo = ?", ipoNo);
    }

    public boolean saveDingdanItem(Pldingdanitem item) {
        return item.save();
    }

    public boolean updateDingdanItem(Pldingdanitem item) {
        return item.update();
    }

    public boolean deleteDingdanItem(int id) {
        return itemDao.deleteById( id);
    }
}