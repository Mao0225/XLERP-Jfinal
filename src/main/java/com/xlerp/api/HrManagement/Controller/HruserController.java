package com.xlerp.api.HrManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;

import com.xlerp.api.HrManagement.Service.HruserService;
import com.xlerp.common.model.Hruser;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

@Before(HttpMethodInterceptor.class)
public class HruserController extends Controller {
    private final HruserService hruserService = new HruserService();

    @ActionKey("/hruser/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String name = getPara("name"); // 新增 name 参数
        String no = getPara("no");     // 新增 no 参数

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = hruserService.paginate(pageNum, pageSz, name, no); // 传递 name 和 no
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/hruser/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("用户ID不能为空"));
            return;
        }

        try {
            Hruser hruser = hruserService.findById(Integer.parseInt(id));
            if (hruser != null) {
                renderJson(Result.success("查询用户成功").putData("hruser", hruser));
            } else {
                renderJson(Result.notFound("用户未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("用户ID格式错误"));
        }
    }

    @ActionKey("/hruser/save")
    @HttpMethod("POST")
    public void save(Hruser hruser) {
        try {
            if (hruser == null || hruser.getNo() == null || hruser.getNo().trim().isEmpty() ||
                    hruser.getName() == null || hruser.getName().trim().isEmpty()) {
                renderJson(Result.badRequest("用户信息不能为空且用户编号和名称必须填写"));
                return;
            }
            boolean success = hruserService.save(hruser);
            if (success) {
                renderJson(Result.success("用户保存成功").putData("hruserId", hruser.getId()));
            } else {
                renderJson(Result.serverError("保存用户失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存用户时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/hruser/update")
    @HttpMethod("PUT")
    public void update(Hruser hruser) {
        try {
            if (hruser == null || hruser.getId() == null) {
                renderJson(Result.badRequest("用户ID不能为空"));
                return;
            }
            boolean success = hruserService.update(hruser);
            if (success) {
                renderJson(Result.success("用户更新成功"));
            } else {
                renderJson(Result.serverError("更新用户失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("用户ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新用户时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/hruser/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("用户ID不能为空"));
            return;
        }

        try {
            boolean success = hruserService.deleteById(Integer.parseInt(id));
            if (success) {
                renderJson(Result.success("用户删除成功"));
            } else {
                renderJson(Result.notFound("用户不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("用户ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除用户时发生错误: " + e.getMessage()));
        }
    }

}