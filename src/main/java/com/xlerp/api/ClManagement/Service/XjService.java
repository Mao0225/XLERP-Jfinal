package com.xlerp.api.ClManagement.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.xlerp.common.model.ClXj;

import java.util.*;
import java.util.stream.Collectors;

public class XjService {
    // 核心：在Service内部手动定义ClXj的dao实例（替代模型类的静态dao）
    private static final ClXj dao = new ClXj();

    /**
     * 分页查询橡胶检验数据
     */
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

        // 使用内部定义的dao实例
        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    /**
     * 根据ID查询橡胶检验记录
     */
    public ClXj findById(int id) {
        // 使用内部定义的dao实例
        return dao.findFirst("select * from cl_xj where id = ? ", id);
    }

    /**
     * 保存橡胶检验记录
     */
    public boolean save(ClXj clXj) {
        return clXj.save();
    }

    /**
     * 更新橡胶检验记录
     */
    public boolean update(ClXj clXj) {
        return clXj.update();
    }

    /**
     * 根据ID删除橡胶检验记录
     */
    public boolean deleteById(int id) {
        // 使用内部定义的dao实例
        return dao.deleteById(id);
    }

    /**
     * 批量删除橡胶检验记录（去重并统一实现）
     */
    /**
     * 批量删除橡胶检验记录（接收字符串格式的ID列表，如"1,2,3"）
     */
    /**
     * 批量删除橡胶检验记录（接收整数列表类型的ID列表）
     */
    public boolean batchDelete(List<Integer> idList) {
        if (idList == null || idList.isEmpty()) {
            return false;
        }
        // 执行批量删除SQL，返回删除的记录数
        String sql = "delete from cl_xj where id in (?)"; // 注意：JFinal会自动处理List参数为逗号分隔的ID列表
        int count = Db.update(sql, idList);
        // 验证删除数量是否与ID列表长度一致
        return count == idList.size();
    }

    /**
     * 更新状态（去重并统一实现，使用内部dao实例）
     */
    public boolean updateStatus(String id, String status, String updatePerson) {
        // 1. 通过内部dao实例查询记录（替代 ClXj.dao.findById）
        ClXj xj = dao.findById(id); // 注意：若id为字符串类型，需确保模型类支持字符串ID查询
        if (xj == null) {
            return false;
        }

        // 2. 更新状态
        xj.set("status", status);

        // 3. 状态为"50"时记录审核人（可根据实际业务调整）
        if ("50".equals(status)) {
            xj.set("auditor", updatePerson);
        }

        // 4. 执行更新
        return xj.update();
    }

    /**
     * 根据状态统计记录数量（使用内部dao实例）
     */
    /**
     * 根据状态统计记录数量
     */
    public long countByStatus(String status) {
        if (status != null && !status.trim().isEmpty()) {
            // 使用 Db.queryLong 直接查询计数，返回 long 类型
            return Db.queryLong("select count(*) from cl_xj where status = ?", status);
        } else {
            // 无状态条件时查询总记录数
            return Db.queryLong("select count(*) from cl_xj");
        }
    }

    /**
     * 查询导出数据
     */
    public List<ClXj> findForExport(String mafactoryname, String matRecheckNo, String contractno,
                                    String material, String mattype, String status) {
        // 使用Service中定义的ClXj dao实例执行查询，自动映射为ClXj列表
        SqlPara sqlPara = Db.getSqlPara("cl_xj.findForExport",
                mafactoryname, matRecheckNo, contractno, material, mattype, status);
        return dao.find(sqlPara); // 关键：用dao.find替代Db.find
    }
}