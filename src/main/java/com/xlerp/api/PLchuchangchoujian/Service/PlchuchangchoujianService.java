package com.xlerp.api.PLchuchangchoujian.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.Plchuchangchoujian;

import java.util.List;
import java.util.stream.Collectors;

public class PlchuchangchoujianService {
    private static final Plchuchangchoujian dao = new Plchuchangchoujian();

    /**
     * 分页查询出厂检验数据
     */
    public Page<Plchuchangchoujian> getchuchangjianyanlist(int pageNumber, int pageSize, String prodworkorder, String spotcheckbatch, String guowanghetonghao) {
        String select = "select *";
        StringBuilder from = new StringBuilder("from plchuchangchoujian where 1=1");

        // 动态构建查询条件
        if (StrKit.notBlank(prodworkorder)) {
            from.append(" and prodworkorder like ?");
        }
        if (StrKit.notBlank(spotcheckbatch)) {
            from.append(" and spotcheckbatch like ?");
        }
        if (StrKit.notBlank(guowanghetonghao)) {
            from.append(" and guowanghetonghao like ?");
        }
        from.append(" order by id desc");

        // 准备参数
        List<Object> params = new java.util.ArrayList<>();
        if (StrKit.notBlank(prodworkorder)) {
            params.add("%" + prodworkorder + "%");
        }
        if (StrKit.notBlank(spotcheckbatch)) {
            params.add("%" + spotcheckbatch + "%");
        }
        if (StrKit.notBlank(guowanghetonghao)) {
            params.add("%" + guowanghetonghao + "%");
        }

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    /**
     * 通过ID查询出厂检验记录
     */
    public Plchuchangchoujian findById(long id) {
        return dao.findFirst("select * from plchuchangchoujian where id = ?", id);
    }

    /**
     * 保存出厂检验记录
     */
    public boolean save(Plchuchangchoujian plchuchangchoujian) {
        return plchuchangchoujian.save();
    }

    /**
     * 更新出厂检验记录
     */
    public boolean update(Plchuchangchoujian plchuchangchoujian) {
        return plchuchangchoujian.update();
    }

    /**
     * 删除出厂检验记录
     */
    public boolean deleteById(long id) {
        return dao.deleteById(id);
    }

    /**
     * 批量删除出厂检验记录
     */
    public boolean batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "delete from plchuchangchoujian where id in (" + placeholders + ")";
        return Db.update(sql, ids.toArray()) > 0;
    }

    /**
     * 通过生产工单号查询检验记录
     */
    public List<Record> getByProdWorkOrder(String prodworkorder) {
        return Db.find("select * from plchuchangchoujian where prodworkorder = ?", prodworkorder);
    }
}