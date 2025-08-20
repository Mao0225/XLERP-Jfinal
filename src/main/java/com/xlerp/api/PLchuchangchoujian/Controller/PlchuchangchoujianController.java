package com.xlerp.api.PLchuchangchoujian.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PLchuchangchoujian.Service.PlchuchangchoujianService;
import com.jfinal.core.Controller;
import com.xlerp.common.model.Plchuchangchoujian;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class PlchuchangchoujianController extends Controller {
    private final PlchuchangchoujianService plchuchangchoujianService = new PlchuchangchoujianService();

    /**
     * 分页查询出厂检验数据列表
     */
    @ActionKey("/plchuchangchoujian/getchuchangchoujianlist")
    @HttpMethod("GET")
    public void getchuchangjianyanlist() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String prodworkorder = getPara("prodworkorder"); // 生产工单号
        String spotcheckbatch = getPara("spotcheckbatch"); // 抽检批次号
        String guowanghetonghao = getPara("guowanghetonghao"); // 国网合同号

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Plchuchangchoujian> page = plchuchangchoujianService.getchuchangjianyanlist(pageNum, pageSz, prodworkorder, spotcheckbatch, guowanghetonghao);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    /**
     * 通过ID查询出厂检验记录
     */
    @ActionKey("/plchuchangchoujian/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("记录ID不能为空"));
            return;
        }

        try {
            Plchuchangchoujian record = plchuchangchoujianService.findById(Long.parseLong(id));
            if (record != null) {
                renderJson(Result.success("查询成功").putData("record", record));
            } else {
                renderJson(Result.notFound("记录未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("记录ID格式错误"));
        }
    }

    /**
     * 保存出厂检验记录
     */
    @ActionKey("/plchuchangchoujian/save")
    @HttpMethod("POST")
    public void save(Plchuchangchoujian record) {
        try {
            boolean success = plchuchangchoujianService.save(record);
            if (success) {
                renderJson(Result.success("保存成功").putData("recordId", record.getId()));
            } else {
                renderJson(Result.serverError("保存失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存时发生错误: " + e.getMessage()));
        }
    }

    /**
     * 更新出厂检验记录
     */
    @ActionKey("/plchuchangchoujian/update")
    @HttpMethod("PUT")
    public void update(Plchuchangchoujian record) {
        try {
            boolean success = plchuchangchoujianService.update(record);
            if (success) {
                renderJson(Result.success("更新成功"));
            } else {
                renderJson(Result.serverError("更新失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("更新时发生错误: " + e.getMessage()));
        }
    }

    /**
     * 删除出厂检验记录
     */
    @ActionKey("/plchuchangchoujian/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("记录ID不能为空"));
            return;
        }

        try {
            boolean success = plchuchangchoujianService.deleteById(Long.parseLong(id.trim()));
            if (success) {
                renderJson(Result.success("删除成功"));
            } else {
                renderJson(Result.notFound("记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("记录ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除时发生错误: " + e.getMessage()));
        }
    }

    /**
     * 批量删除出厂检验记录
     */
    @ActionKey("/plchuchangchoujian/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim().isEmpty()) {
            renderJson(Result.badRequest("记录ID列表不能为空"));
            return;
        }

        try {
            List<Long> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            if (idList.isEmpty()) {
                renderJson(Result.badRequest("记录ID列表不能为空"));
                return;
            }

            boolean success = plchuchangchoujianService.batchDelete(idList);
            if (success) {
                renderJson(Result.success("批量删除成功"));
            } else {
                renderJson(Result.serverError("批量删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("记录ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("批量删除时发生错误: " + e.getMessage()));
        }
    }

    /**
     * 通过生产工单号查询检验记录
     */
    @ActionKey("/plchuchangchoujian/getByProdWorkOrder")
    @HttpMethod("GET")
    public void getByProdWorkOrder() {
        String prodworkorder = getPara("prodworkorder");
        if (prodworkorder == null || prodworkorder.trim().isEmpty()) {
            renderJson(Result.badRequest("生产工单号不能为空"));
            return;
        }

        try {
            List<Record> list = plchuchangchoujianService.getByProdWorkOrder(prodworkorder);
            renderJson(Result.success("查询成功").putData("list", list));
        } catch (Exception e) {
            renderJson(Result.serverError("查询时发生错误: " + e.getMessage()));
        }
    }
}