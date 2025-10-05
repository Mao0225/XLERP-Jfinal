package com.xlerp.api.ClManagement.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClXj;  // 对应cl_xj表的模型类
import com.jfinal.plugin.activerecord.Record; // 关键：导入JFinal的Record类

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.ClXj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XjService {
    private static final ClXj dao = new ClXj();

    public Page<ClXj> paginate(int pageNumber, int pageSize, String mafactoryname, String matRecheckNo,
                               String contractno, String material, String mattype, String status) {
        String select = "select *";
        StringBuilder from = new StringBuilder(" from cl_xj where 1 = 1");
        List<Object> params = new ArrayList<>();

        if (mafactoryname != null && !mafactoryname.trim().isEmpty()) {
            from.append(" and mafactoryname like ?");
            params.add("%" + mafactoryname + "%");
        }
        if (matRecheckNo != null && !matRecheckNo.trim().isEmpty()) {
            from.append(" and matRecheckNo like ?");
            params.add("%" + matRecheckNo + "%");
        }
        if (contractno != null && !contractno.trim().isEmpty()) {
            from.append(" and contractno like ?");
            params.add("%" + contractno + "%");
        }
        if (material != null && !material.trim().isEmpty()) {
            from.append(" and material like ?");
            params.add("%" + material + "%");
        }
        if (mattype != null && !mattype.trim().isEmpty()) {
            from.append(" and mattype like ?");
            params.add("%" + mattype + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            from.append(" and status >= ?");
            params.add(status);
        }

        from.append(" order by id desc");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    public ClXj findById(int id) {
        return dao.findFirst("select * from cl_xj where id = ? ", id);
    }

    public boolean save(ClXj clXj) {
        return clXj.save();
    }

    public boolean update(ClXj clXj) {
        return clXj.update();
    }

    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    public boolean batchDelete(List<Integer> ids) {
        return dao.deleteByIds(ids);
    }

    public boolean updateStatus(String id, String status, String updatePerson) {
        Map<String, String> statusToField = new HashMap<>();
        statusToField.put("30", "auditor");
        statusToField.put("40", "checker");
        statusToField.put("50", "checkAuditor");

        Record record = Db.findById("cl_xj", id);
        if (record == null) {
            return false;
        }

        record.set("status", status);

        String field = statusToField.get(status);
        if (field != null) {
            String currentValue = record.getStr(field);
            if (currentValue == null || currentValue.isEmpty()) {
                record.set(field, updatePerson);
            }
        }

        return Db.update("cl_xj", record);
    }
}