package com.xlerp.api.ClManagement.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClGb;

import java.util.List;

public class GbService {
    // 数据库访问对象，对应cl_gb表
    private static final ClGb dao = new ClGb();

    /**
     * 分页查询钢板数据
     */
    public Page<ClGb> paginate(int pageNumber, int pageSize, String mafactory, String inNo, String matMaterial, String matRecheckNo) {
        String select = "select *";
        StringBuilder from = new StringBuilder(" from cl_gb");
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
     * 根据ID查询钢板记录
     */
    public ClGb findById(int id) {
        return dao.findFirst("select * from cl_gb where id = ? ", id);  // 表名改为cl_gb
    }

    /**
     * 保存钢板记录
     */
    public boolean save(ClGb gb) {
        return gb.save();
    }

    /**
     * 更新钢板记录
     */
    public boolean update(ClGb gb) {
        return gb.update();
    }

    /**
     * 根据ID删除钢板记录（修正方法名首字母小写）
     */
    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    /**
     * 批量删除圆钢记录
     */
    public boolean batchDelete(List<Integer> ids) {
        return dao.deleteByIds(ids);
    }
}

