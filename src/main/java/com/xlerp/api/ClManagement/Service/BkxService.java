package com.xlerp.api.ClManagement.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.ClBkx;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BkxService {
    // 数据库访问对象，对应cl_bkx表
    private static final ClBkx dao = new ClBkx();

    /**
     * 分页查询闭口销数据
     */
    public Page<ClBkx> paginate(int pageNumber, int pageSize, String mafactory, String inNo, String matMaterial, String matRecheckNo) {
        String select = "select *";
        StringBuilder from = new StringBuilder(" from cl_bkx");
        List<Object> params = new java.util.ArrayList<>();

        // 添加搜索条件
        boolean hasCondition = false;
        if (mafactory != null && !mafactory.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" mafactory like ?");
            params.add("%" + mafactory + "%");
            hasCondition = true;
        }
        if (inNo != null && !inNo.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" inNo like ?");
            params.add("%" + inNo + "%");
            hasCondition = true;
        }
        if (matMaterial != null && !matMaterial.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" matMaterial like ?");
            params.add("%" + matMaterial + "%");
            hasCondition = true;
        }
        if (matRecheckNo != null && !matRecheckNo.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" matRecheckNo like ?");
            params.add("%" + matRecheckNo + "%");
            hasCondition = true;
        }

        from.append(" order by id desc");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    /**
     * 根据ID查询闭口销记录
     */
    public ClBkx findById(int id) {
        return dao.findFirst("select * from cl_bkx where id = ? ", id);  // 表名改为cl_bkx
    }

    /**
     * 保存闭口销记录
     */
    public boolean save(ClBkx yg) {
        return yg.save();
    }

    /**
     * 更新闭口销记录
     */
    public boolean update(ClBkx yg) {
        return yg.update();
    }

    /**
     * 根据ID删除闭口销记录（修正方法名首字母小写）
     */
    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    /**
     * 批量删除闭口销记录
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
        Record record = Db.findById("cl_bkx", id);
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
        return Db.update("cl_bkx", record);
    }
}
