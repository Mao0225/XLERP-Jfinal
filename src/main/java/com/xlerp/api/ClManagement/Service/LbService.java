package com.xlerp.api.ClManagement.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.ClLb;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LbService {
    // 数据库访问对象，对应cl_lb表
    private static final ClLb dao = new ClLb();

    /**
     * 分页查询铝板数据
     */
    public Page<ClLb> paginate(int pageNumber, int pageSize, String mafactory, String matRecheckNo, String contractNo,
                               String contractName, String material, String type, String status) {
        String select = "select *";
        StringBuilder from = new StringBuilder(" from cl_lb where 1 = 1");
        List<Object> params = new java.util.ArrayList<>();

        // 添加搜索条件
        if (mafactory != null && !mafactory.trim().isEmpty()) {
            from.append(" and mafactory like ?");
            params.add("%" + mafactory + "%");
        }
        if (matRecheckNo != null && !matRecheckNo.trim().isEmpty()) {
            from.append(" and matRecheckNo like ?");
            params.add("%" + matRecheckNo + "%");
        }
        if (contractNo != null && !contractNo.trim().isEmpty()) {
            from.append(" and contractNo like ?");
            params.add("%" + contractNo + "%");
        }
        if (contractName != null && !contractName.trim().isEmpty()) {
            from.append(" and contractName like ?");
            params.add("%" + contractName + "%");
        }
        if (material != null && !material.trim().isEmpty()) {
            from.append(" and material like ?");
            params.add("%" + material + "%");
        }
        if (type != null && !type.trim().isEmpty()) {
            from.append(" and type like ?");
            params.add("%" + type + "%");
        }
        if (status != null && !status.trim().isEmpty()) {
            from.append(" and status >= ?");
            params.add(status);
        }

        from.append(" order by id desc");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    /**
     * 根据ID查询铝板记录
     */
    public ClLb findById(int id) {
        return dao.findFirst("select * from cl_lb where id = ? ", id);  // 表名改为cl_lb
    }

    /**
     * 保存铝板记录
     */
    public boolean save(ClLb lb) {
        return lb.save();
    }

    /**
     * 更新铝板记录
     */
    public boolean update(ClLb lb) {
        return lb.update();
    }

    /**
     * 根据ID删除铝板记录（修正方法名首字母小写）
     */
    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    /**
     * 批量删除铝板记录
     */
    public boolean batchDelete(List<Integer> ids) {
        return dao.deleteByIds(ids);
    }

    public boolean updateStatus(String id, String status, String updatePerson) {
        // 合法状态和对应字段映射
        Map<String, String> statusToField = new HashMap<>();
        statusToField.put("30", "requestAuditor");
        statusToField.put("40", "checkWriter");
        statusToField.put("50", "checkAuditor");

        // 获取记录
        Record record = Db.findById("cl_lb", id);
        if (record == null) {
            return false;
        }

        // 默认只更新 status
        record.set("status", status);

        // 如果 status 在映射中，检查对应字段是否为空
        String field = statusToField.get(status);
        if (field != null) {
            String currentValue = record.getStr(field);
            if (currentValue == null || currentValue.isEmpty()) {
                record.set(field, updatePerson);
            }
        }

        // 更新记录
        return Db.update("cl_lb", record);
    }
}
