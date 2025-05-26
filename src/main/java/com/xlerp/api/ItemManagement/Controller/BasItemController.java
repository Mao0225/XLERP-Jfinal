package com.xlerp.api.ItemManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.ItemManagement.Service.BasItemService;
import com.xlerp.common.model.Basitem;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import java.math.BigDecimal;

@Before(HttpMethodInterceptor.class)
public class BasItemController extends Controller {
    private final BasItemService basItemService = new BasItemService();

    @ActionKey("/basitem/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String itemNo = getPara("itemNo");
        String itemName = getPara("itemName");
        String inclass = getPara("inclass");
        String type = getPara("type");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = basItemService.paginate(pageNum, pageSz,  itemNo, itemName, inclass, type);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
    @ActionKey("/basitem/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            Basitem basItem = basItemService.findById(Integer.parseInt(id));
            if (basItem != null) {
                renderJson(Result.success("查询物料成功").putData("basItem", basItem));
            } else {
                renderJson(Result.notFound("物料未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        }
    }
    @ActionKey("/basitem/save")
    @HttpMethod("POST")
    public void save(Basitem basItem) {
        // 校验必填字段

        try {
            boolean success = basItemService.save(basItem);
            if (success) {
                renderJson(Result.success("物料保存成功").putData("itemId", basItem.getId()));
            } else {
                renderJson(Result.serverError("保存物料失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值格式错误（如重量或价格）"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存物料时发生错误: " + e.getMessage()));
        }
    }
    @ActionKey("/basitem/update")
    @HttpMethod("PUT")
    public void update(Basitem basItem) {

        try {

            boolean success = basItemService.update(basItem);
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

    @ActionKey("/basitem/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            boolean success = basItemService.deleteById(Integer.parseInt(id.trim()));
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