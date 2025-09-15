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
    // 使用xj服务类
    private final XjService xjService = new XjService();

    /**
     * 分页查询xj数据
     */
    @ActionKey("/cl_xj/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String inNo = getPara("inNo");
        String mafactoryname = getPara("mafactoryname");
        String detectionTime = getPara("detectionTime");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 查询xj数据分页
            Page<ClXj> page = xjService.paginate(pageNum, pageSz, inNo, mafactoryname, detectionTime);
            renderJson(Result.success("xj数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_xj/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("xj记录ID不能为空"));
            return;
        }

        try {
            ClXj xj = xjService.findById(Integer.parseInt(id));
            if (xj != null) {
                renderJson(Result.success("xj记录查询成功").putData("record", xj));
            } else {
                renderJson(Result.notFound("xj记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("xj记录ID格式错误"));
        }
    }

    @ActionKey("/cl_xj/save")
    @HttpMethod("POST")
    public void save(ClXj xj) {
        try {
            boolean success = xjService.save(xj);
            if (success) {
                renderJson(Result.success("xj记录保存成功").putData("recordId", xj.getId()));
            } else {
                renderJson(Result.serverError("xj记录保存失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存xj记录时发生错误:" + e.getMessage()));
        }
    }

    @ActionKey("/cl_xj/update")
    @HttpMethod("PUT")
    public void update(ClXj xj) {
        try {
            boolean success = xjService.update(xj);
            if (success) {
                renderJson(Result.success("xj记录更新成功"));
            } else {
                renderJson(Result.serverError("xj记录更新失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("更新xj记录时发生错误:" + e.getMessage()));
        }
    }

    @ActionKey("/cl_xj/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("xj记录ID不能为空"));
            return;
        }

        try {
            boolean success = xjService.deleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("xj记录删除成功"));
            } else {
                renderJson(Result.notFound("xj记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("xj记录ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除xj记录时发生错误:" + e.getMessage()));
        }
    }

    @ActionKey("/cl_xj/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim().isEmpty()) {
            renderJson(Result.badRequest("xj记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty()) {
                renderJson(Result.badRequest("xj记录ID列表不能为空"));
                return;
            }

            boolean success = xjService.batchDelete(idList);
            if (success) {
                renderJson(Result.success("批量删除xj记录成功"));
            } else {
                renderJson(Result.serverError("批量删除xj记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("xj记录ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("批量删除xj记录时发生错误:" + e.getMessage()));
        }
    }
}