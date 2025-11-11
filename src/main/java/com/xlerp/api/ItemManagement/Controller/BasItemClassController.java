package com.xlerp.api.ItemManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.ItemManagement.Dto.ClassTreeDTO;
import com.xlerp.api.ItemManagement.Service.BasItemClassService;
import com.xlerp.common.model.BasItemClass;

import java.util.List;

@Before(HttpMethodInterceptor.class)
public class BasItemClassController extends Controller {
    private final BasItemClassService basItemClassService = new BasItemClassService();

    @ActionKey("/bas_item_class/getList")
    @HttpMethod("GET")
    public void getList() {
        String params = getPara("params");
        try {
            List<BasItemClass> list = basItemClassService.getList(params);

            renderJson(Result.success("查询成功").putData("list", list));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/bas_item_class/getTreeList")
    @HttpMethod("GET")
    public void getTreeList() {
        String params = getPara("params");
        try {
            List<ClassTreeDTO> list = basItemClassService.getTreeList(params);
            System.out.println("===== 获取分类树成功 ====="+list);
            renderJson(Result.success("查询成功").putData("list", list));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
    @ActionKey("/bas_item_class/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            BasItemClass basItemClass = basItemClassService.findById(Integer.parseInt(id));
            if (basItemClass != null) {
                renderJson(Result.success("查询物料成功").putData("basItemClass", basItemClass));
            } else {
                renderJson(Result.notFound("物料未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        }
    }
    @ActionKey("/bas_item_class/save")
    @HttpMethod("POST")
    public void save( BasItemClass basItemClass) {
        // 校验必填字段

        try {
            boolean success = basItemClassService.save(basItemClass);
            if (success) {
                renderJson(Result.success("物料保存成功").putData("itemId", basItemClass.getId()));
            } else {
                renderJson(Result.serverError("保存物料失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值格式错误（如重量或价格）"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存物料时发生错误: " + e.getMessage()));
        }
    }
    @ActionKey("/bas_item_class/update")
    @HttpMethod("PUT")
    public void update(BasItemClass basItemClass) {

        try {

            boolean success = basItemClassService.update(basItemClass);
            if (success) {
                renderJson(Result.success("物料更新成功"));
            } else {
                renderJson(Result.serverError("更新物料失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID或数值格式错误（如重量或价格）"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新物料时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/bas_item_class/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            boolean success = basItemClassService.deleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("物料删除成功"));
            } else {
                renderJson(Result.notFound("物料不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除物料时发生错误: " + e.getMessage()));
        }
    }


}