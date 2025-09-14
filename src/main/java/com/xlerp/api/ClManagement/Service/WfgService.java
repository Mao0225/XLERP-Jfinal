package com.xlerp.api.ClManagement.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClWfg;

import java.util.List;

public class WfgService {
    // 数据库访问对象，对应cl_wfg表
    private static final ClWfg dao = new ClWfg();

    /**
     * 分页查询无缝管数据
     */
    public Page<ClWfg> paginate(int pageNumber, int pageSize, String mafactory, String inNo, String matMaterial, String matRecheckNo) {
        String select = "select *";
        StringBuilder from = new StringBuilder(" from cl_wfg");
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
     * 根据ID查询无缝管记录
     */
    public ClWfg findById(int id) {
        return dao.findFirst("select * from cl_wfg where id = ? ", id);  // 表名改为cl_wfg
    }

    /**
     * 保存无缝管记录
     */
    public boolean save(ClWfg wfg) {
        return wfg.save();
    }

    /**
     * 更新无缝管记录
     */
    public boolean update(ClWfg wfg) {
        return wfg.update();
    }

    /**
     * 根据ID删除无缝管记录（修正方法名首字母小写）
     */
    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    /**
     * 批量删除无缝管记录
     */
    public boolean batchDelete(List<Integer> ids) {
        return dao.deleteByIds(ids);
    }
}
