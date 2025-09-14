package com.xlerp.api.ClManagement.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClYg;

import java.util.List;

public class YgService {
    // 数据库访问对象，对应cl_yg表
    private static final ClYg dao = new ClYg();

    /**
     * 分页查询圆钢数据
     */
    public Page<ClYg> paginate(int pageNumber, int pageSize, String mafactory, String inNo, String matMaterial, String matRecheckNo) {
        String select = "select *";
        StringBuilder from = new StringBuilder(" from cl_yg");
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
     * 根据ID查询圆钢记录
     */
    public ClYg findById(int id) {
        return dao.findFirst("select * from cl_yg where id = ? ", id);  // 表名改为cl_yg
    }

    /**
     * 保存圆钢记录
     */
    public boolean save(ClYg yg) {
        return yg.save();
    }

    /**
     * 更新圆钢记录
     */
    public boolean update(ClYg yg) {
        return yg.update();
    }

    /**
     * 根据ID删除圆钢记录（修正方法名首字母小写）
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
