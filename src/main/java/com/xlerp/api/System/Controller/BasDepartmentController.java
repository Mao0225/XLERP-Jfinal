package com.xlerp.api.System.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.System.Service.BasDepartmentService;
import com.xlerp.common.model.Basdepartment;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

@Before(HttpMethodInterceptor.class)
public class BasDepartmentController extends Controller {
    private final BasDepartmentService basDepartmentService = new BasDepartmentService();

    @ActionKey("/basdepartment/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String name = getPara("departmentName");
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = basDepartmentService.paginate(pageNum, pageSz, name);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/basdepartment/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("部门ID不能为空"));
            return;
        }

        try {
            Basdepartment basDepartment = basDepartmentService.findById(Integer.parseInt(id));
            if (basDepartment != null) {
                renderJson(Result.success("查询部门成功").putData("basDepartment", basDepartment));
            } else {
                renderJson(Result.notFound("部门未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("部门ID格式错误"));
        }
    }

    @ActionKey("/basdepartment/save")
    @HttpMethod("POST")
    public void save(Basdepartment basDepartment) {
        try {
            System.out.println("获取到的部门列表"+basDepartment);
            if (basDepartment == null || basDepartment.getNo() == null || basDepartment.getNo().trim().isEmpty() ||
                    basDepartment.getName() == null || basDepartment.getName().trim().isEmpty()) {
                renderJson(Result.badRequest("部门信息不能为空且部门编号和名称必须填写"));
                return;
            }
            boolean success = basDepartmentService.save(basDepartment);
            if (success) {
                renderJson(Result.success("部门保存成功").putData("basDepartmentId", basDepartment.getId()));
            } else {
                renderJson(Result.serverError("保存部门失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存部门时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/basdepartment/update")
    @HttpMethod("PUT")
    public void update(Basdepartment basDepartment) {
        try {
            if (basDepartment == null || basDepartment.getId() == null) {
                renderJson(Result.badRequest("部门ID不能为空"));
                return;
            }
            boolean success = basDepartmentService.update(basDepartment);
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

    @ActionKey("/basdepartment/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("部门ID不能为空"));
            return;
        }

        try {
            boolean success = basDepartmentService.deleteById(Integer.parseInt(id));
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

    @ActionKey("/basdepartment/getoption")
    @HttpMethod("GET")
    public void getoption() {
        try {
            List<Record> options = basDepartmentService.getOptions();
            renderJson(Result.success("查询选项成功").putData("options", options));
        } catch (Exception e) {
            renderJson(Result.serverError("查询选项时发生错误: " + e.getMessage()));
        }
    }
}