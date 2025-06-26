package com.xlerp.api.PlManagement.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.Plentityid;

import java.util.List;

public class PlentityIdService {
    private static final Plentityid dao = new Plentityid();

    public Page<Plentityid> paginate(int pageNumber, int pageSize, String purchaserHqCode, String supplierCode, String supplierName, String entityCode, Long poItemId, String entityStatus, String dataSource, String dataSourceCreateTime, String remark, String ownerId, String openId, String status, String flag) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from plentityid where isdelete = 0");

        // 动态构建查询条件
        if (StrKit.notBlank(purchaserHqCode)) {
            from.append(" and purchaserHqCode like ?");
        }
        if (StrKit.notBlank(supplierCode)) {
            from.append(" and supplierCode like ?");
        }
        if (StrKit.notBlank(supplierName)) {
            from.append(" and supplierName like ?");
        }
        if (StrKit.notBlank(entityCode)) {
            from.append(" and entityCode like ?");
        }
        if (poItemId != null) {
            from.append(" and poItemId = ?");
        }
        if (StrKit.notBlank(entityStatus)) {
            from.append(" and entityStatus like ?");
        }
        if (StrKit.notBlank(dataSource)) {
            from.append(" and dataSource like ?");
        }
        if (StrKit.notBlank(dataSourceCreateTime)) {
            from.append(" and dataSourceCreateTime like ?");
        }
        if (StrKit.notBlank(remark)) {
            from.append(" and remark like ?");
        }
        if (StrKit.notBlank(ownerId)) {
            from.append(" and ownerId like ?");
        }
        if (StrKit.notBlank(openId)) {
            from.append(" and openId like ?");
        }
        if (StrKit.notBlank(status)) {
            from.append(" and status like ?");
        }
        if (StrKit.notBlank(flag)) {
            from.append(" and flag like ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(purchaserHqCode)) {
            params.add("%" + purchaserHqCode + "%");
        }
        if (StrKit.notBlank(supplierCode)) {
            params.add("%" + supplierCode + "%");
        }
        if (StrKit.notBlank(supplierName)) {
            params.add("%" + supplierName + "%");
        }
        if (StrKit.notBlank(entityCode)) {
            params.add("%" + entityCode + "%");
        }
        if (poItemId != null) {
            params.add(poItemId);
        }
        if (StrKit.notBlank(entityStatus)) {
            params.add("%" + entityStatus + "%");
        }
        if (StrKit.notBlank(dataSource)) {
            params.add("%" + dataSource + "%");
        }
        if (StrKit.notBlank(dataSourceCreateTime)) {
            params.add("%" + dataSourceCreateTime + "%");
        }
        if (StrKit.notBlank(remark)) {
            params.add("%" + remark + "%");
        }
        if (StrKit.notBlank(ownerId)) {
            params.add("%" + ownerId + "%");
        }
        if (StrKit.notBlank(openId)) {
            params.add("%" + openId + "%");
        }
        if (StrKit.notBlank(status)) {
            params.add("%" + status + "%");
        }
        if (StrKit.notBlank(flag)) {
            params.add("%" + flag + "%");
        }
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public Plentityid findById(int id) {
        return dao.findById(id);
    }

    public boolean save(Plentityid plentityid) {
        return plentityid.save();
    }

    public boolean update(Plentityid plentityid) {
        return plentityid.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }
}
