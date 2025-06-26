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
        String type = getPara("type");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = basOrgService.paginate(pageNum, pageSz, no, descr, type);
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
            renderJson(Result.badRequest("客户ID不能为空"));
            return;
        }

        try {
            Basorg basOrg = basOrgService.findById(Integer.parseInt(id.trim()));
            if (basOrg != null) {
                renderJson(Result.success("查询客户成功").putData("basOrg", basOrg));
            } else {
                renderJson(Result.notFound("客户未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("客户ID格式错误"));
        }
    }

    @ActionKey("/basorg/save")
    @HttpMethod("POST")
    public void save(Basorg basOrg) {
        try {
            boolean success = basOrgService.save(basOrg);
            if (success) {
                renderJson(Result.success("客户保存成功").putData("basOrgId", basOrg.getId()));
            } else {
                renderJson(Result.serverError("保存客户失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存客户时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/basorg/update")
    @HttpMethod("PUT")
    public void update(Basorg basOrg) {

        try {

            boolean success = basOrgService.update(basOrg);
            if (success) {
                renderJson(Result.success("客户更新成功"));
            } else {
                renderJson(Result.serverError("更新客户失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("客户ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新客户时发生错误: " + e.getMessage()));
        }
    }
    @ActionKey("/basorg/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("客户ID不能为空"));
            return;
        }

        try {
            boolean success = basOrgService.deleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("客户删除成功"));
            } else {
                renderJson(Result.notFound("客户不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("客户ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除客户时发生错误: " + e.getMessage()));
        }
    }
}