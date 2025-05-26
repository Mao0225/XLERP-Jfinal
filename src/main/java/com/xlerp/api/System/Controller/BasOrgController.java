package com.xlerp.api.System.Controller;


import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.System.Service.BasOrgService;
import com.xlerp.common.model.Basorg;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@Before(HttpMethodInterceptor.class)
public class BasOrgController extends Controller {
    private final BasOrgService basOrgService = new BasOrgService();


    @ActionKey("/basorg/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String no = getPara("no");
        String descr = getPara("descr");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = basOrgService.paginate(pageNum, pageSz, no, descr);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
    @ActionKey("/basorg/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("部门ID不能为空"));
            return;
        }

        try {
            Basorg basOrg = basOrgService.findById(Integer.parseInt(id.trim()));
            if (basOrg != null) {
                renderJson(Result.success("查询部门成功").putData("basOrg", basOrg));
            } else {
                renderJson(Result.notFound("部门未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("部门ID格式错误"));
        }
    }

    @ActionKey("/basorg/save")
    @HttpMethod("POST")
    public void save(Basorg basOrg) {
        try {
            boolean success = basOrgService.save(basOrg);
            if (success) {
                renderJson(Result.success("部门保存成功").putData("basOrgId", basOrg.getId()));
            } else {
                renderJson(Result.serverError("保存部门失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存部门时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/basorg/update")
    @HttpMethod("PUT")
    public void update(Basorg basOrg) {

        try {

            boolean success = basOrgService.update(basOrg);
            if (success) {
                renderJson(Result.success("部门更新成功"));
            } else {
                renderJson(Result.serverError("更新部门失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("部门ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新部门时发生错误: " + e.getMessage()));
        }
    }
    @ActionKey("/basorg/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("部门ID不能为空"));
            return;
        }

        try {
            boolean success = basOrgService.deleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("部门删除成功"));
            } else {
                renderJson(Result.notFound("部门不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("部门ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除部门时发生错误: " + e.getMessage()));
        }
    }
}