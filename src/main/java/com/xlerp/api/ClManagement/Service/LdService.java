package com.xlerp.api.ClManagement.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClLd;  // 假设铝锭对应的模型类为ClLd（cl_ld表）

import java.util.List;

public class LdService {
    // 数据库访问对象，对应cl_ld表
    private static final ClLd dao = new ClLd();

    /**
     * 分页查询铝锭数据
     */
    public Page<ClLd> paginate(int pageNumber, int pageSize, String mafactory, String inNo,
                               String matMaterial, String matRecheckNo, String contractNo,
                               String contractName,String material,String type ,String status) {
        String select = "select *";
        StringBuilder from = new StringBuilder(" from cl_ld");
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
        if (contractNo != null && !contractNo.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" contractNo like ?");
            params.add("%" + contractNo + "%");
            hasCondition = true;
        }
        if (contractName != null && !contractName.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" contractName like ?");
            params.add("%" + contractName + "%");
            hasCondition = true;
        }
        if (material != null && !material.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" material like ?");
            params.add("%" + material + "%");
            hasCondition = true;
        }
        if (type != null && !type.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" type like ?");
            params.add("%" + type + "%");
            hasCondition = true;
        }
        if (status != null && !status.trim().isEmpty()) {
            //应该判断是否大于这个状态从10-50
            from.append(hasCondition ? " and" : " where").append(" status >= ?");
            params.add(status);
            hasCondition = true;
        }


        from.append(" order by id desc");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    /**
     * 根据ID查询铝锭记录
     */
    public ClLd findById(int id) {
        return dao.findFirst("select * from cl_ld where id = ? ", id);  // 表名改为cl_ld
    }

    /**
     * 保存铝锭记录
     */
    public boolean save(ClLd ld) {
        return ld.save();
    }

    /**
     * 更新铝锭记录
     */
    public boolean update(ClLd ld) {
        return ld.update();
    }

    /**
     * 根据ID删除铝锭记录（修正方法名首字母小写）
     */
    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    /**
     * 批量删除铝锭记录
     */
    public boolean batchDelete(List<Integer> ids) {
        return dao.deleteByIds(ids);
    }

    public boolean updateStatus(String id, String status) {
        return Db.update("update cl_ld set status = ? where id = ?", status, id) > 0;
    }
}
