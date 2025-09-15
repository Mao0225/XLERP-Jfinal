package com.xlerp.api.ClManagement.Service;

import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClXj;  // 对应cl_xj表的模型类

import java.util.List;

public class XjService {
    // 数据库访问对象，对应cl_xj表
    private static final ClXj dao = new ClXj();

    /**
     * 分页查询xj数据
     */
    public Page<ClXj> paginate(int pageNumber, int pageSize, String inNo, String mafactoryname, String detectionTime) {
        String select = "select *";
        StringBuilder from = new StringBuilder(" from cl_xj");
        List<Object> params = new java.util.ArrayList<>();

        // 添加搜索条件
        boolean hasCondition = false;
        if (inNo != null && !inNo.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" inNo like ?");
            params.add("%" + inNo + "%");
            hasCondition = true;
        }
        if (mafactoryname != null && !mafactoryname.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" mafactoryname like ?");
            params.add("%" + mafactoryname + "%");
            hasCondition = true;
        }
        // 处理日期查询，只按日期匹配，忽略时间部分
        if (detectionTime != null && !detectionTime.trim().isEmpty()) {
            from.append(hasCondition ? " and" : " where").append(" trunc(detectionTime) = trunc(?)");
            params.add(detectionTime);
            hasCondition = true;
        }

        from.append(" order by id desc");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    /**
     * 根据ID查询xj记录
     */
    public ClXj findById(int id) {
        return dao.findFirst("select * from cl_xj where id = ? ", id);
    }

    /**
     * 保存xj记录
     */
    public boolean save(ClXj xj) {
        return xj.save();
    }

    /**
     * 更新xj记录
     */
    public boolean update(ClXj xj) {
        return xj.update();
    }

    /**
     * 根据ID删除xj记录
     */
    public boolean deleteById(int id) {
        return dao.deleteById(id);
    }

    /**
     * 批量删除xj记录
     */
    public boolean batchDelete(List<Integer> ids) {
        return dao.deleteByIds(ids);
    }
}