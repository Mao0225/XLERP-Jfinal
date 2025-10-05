package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.ClManagement.Service.XjService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClXj;  // 对应cl_xj表的模型类

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@Before(HttpMethodInterceptor.class)
public class XjController extends Controller {
    private final XjService xjService = new XjService();

    /**
     * 分页查询橡胶检验数据
     */
    @ActionKey("/cl_xj/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String mafactoryname = getPara("mafactoryname");
        String matRecheckNo = getPara("matRecheckNo");
        String contractno = getPara("contractno");
        String material = getPara("material");
        String mattype = getPara("mattype");
        String status = getPara("status");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<ClXj> page = xjService.paginate(pageNum, pageSz, mafactoryname, matRecheckNo, contractno, material, mattype, status);
            renderJson(Result.success("橡胶检验数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_xj/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("橡胶检验记录ID不能为空"));
            return;
        }

        try {
            ClXj xj = xjService.findById(Integer.parseInt(id));
            if (xj != null) {
                renderJson(Result.success("橡胶检验记录查询成功").putData("record", xj));
            } else {
                renderJson(Result.notFound("橡胶检验记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("橡胶检验记录ID格式错误"));
        }
    }

    @ActionKey("/cl_xj/save")
    @HttpMethod("POST")
    public void save(ClXj xj) {
        try {
            boolean success = xjService.save(xj);
            if (success) {
                renderJson(Result.success("橡胶检验记录保存成功").putData("recordId", xj.getId()));
            } else {
                renderJson(Result.serverError("橡胶检验记录保存失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存橡胶检验记录时发生错误:" + e.getMessage()));
        }
    }

    @ActionKey("/cl_xj/update")
    @HttpMethod("PUT")
    public void update(ClXj xj) {
        try {
            boolean success = xjService.update(xj);
            if (success) {
                renderJson(Result.success("橡胶检验记录更新成功"));
            } else {
                renderJson(Result.serverError("橡胶检验记录更新失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("更新橡胶检验记录时发生错误:" + e.getMessage()));
        }
    }

    @ActionKey("/cl_xj/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("橡胶检验记录ID不能为空"));
            return;
        }

        try {
            boolean success = xjService.deleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("橡胶检验记录删除成功"));
            } else {
                renderJson(Result.notFound("橡胶检验记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("橡胶检验记录ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除橡胶检验记录时发生错误:" + e.getMessage()));
        }
    }

    @ActionKey("/cl_xj/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim().isEmpty()) {
            renderJson(Result.badRequest("橡胶检验记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty()) {
                renderJson(Result.badRequest("橡胶检验记录ID列表不能为空"));
                return;
            }

            boolean success = xjService.batchDelete(idList);
            if (success) {
                renderJson(Result.success("批量删除橡胶检验记录成功"));
            } else {
                renderJson(Result.serverError("批量删除橡胶检验记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("橡胶检验记录ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("批量删除橡胶检验记录时发生错误:" + e.getMessage()));
        }
    }

    /**
     * 根据状态统计橡胶检验记录数量
     */
    @ActionKey("/cl_xj/countByStatus")
    @HttpMethod("GET")
    public void countByStatus() {
        String status = getPara("status");
        try {
            long count = xjService.countByStatus(status);
            renderJson(Result.success("统计成功").putData("count", count));
        } catch (Exception e) {
            renderJson(Result.serverError("统计橡胶检验记录数量时发生错误:" + e.getMessage()));
        }
    }


    /**
     * 导出橡胶检验记录
     */
    @ActionKey("/cl_xj/export")
    @HttpMethod("GET")
    public void export() {
        String mafactoryname = getPara("mafactoryname");
        String matRecheckNo = getPara("matRecheckNo");
        String contractno = getPara("contractno");
        String material = getPara("material");
        String mattype = getPara("mattype");
        String status = getPara("status");

        try {
            List<ClXj> list = xjService.findForExport(mafactoryname, matRecheckNo, contractno, material, mattype, status);
            // 导出逻辑实现
            renderJson(Result.success("导出成功"));
        } catch (Exception e) {
            renderJson(Result.serverError("导出橡胶检验记录时发生错误:" + e.getMessage()));
        }
    }

    @ActionKey("/cl_xj/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        String updatePerson = getPara("updatePerson");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("记录 ID 不能为空"));
        }
        try {
            boolean success = xjService.updateStatus(id, status, updatePerson);
            if (success) {
                renderJson(Result.success("状态更新成功"));
            } else {
                renderJson(Result.badRequest("更新状态失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("更新状态时发生错误:" + e.getMessage()));
        }
    }
}