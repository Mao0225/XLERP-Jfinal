package com.xlerp.api.PlInspectionController.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlInspectionController.Service.InspItemService;
import com.xlerp.common.model.PlInspItem;

import java.util.List;

@Before(HttpMethodInterceptor.class)
public class InspItemController extends Controller {
    private final InspItemService service = new InspItemService();

    @ActionKey("/insp_item/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String param = getPara("param");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = service.paginate(pageNum, pageSz,param);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码、每页大小或poItemId格式错误"));
        }
    }

    @ActionKey("/insp_item/getList")
    @HttpMethod("GET")
    public void getList() {

        String param = getPara("param");
        try {
            List<PlInspItem> list = service.getList(param);
            renderJson(Result.success("查询成功").putData("list", list));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码、每页大小或poItemId格式错误"));
        }
    }

    @ActionKey("/insp_item/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("实物ID不能为空"));
            return;
        }

        try {
            PlInspItem inspItem = service.findById(Integer.parseInt(id));
            if (inspItem != null) {
                renderJson(Result.success("查询成功").putData("inspItem", inspItem));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("实物ID格式错误"));
        }
    }

    @ActionKey("/insp_item/save")
    @HttpMethod("POST")
    public void save(PlInspItem inspItem) {
        try {
            boolean success = service.save(inspItem);
            if (success) {
                renderJson(Result.success("保存成功").putData("plentityId", inspItem.getId()));
            } else {
                renderJson(Result.serverError("保存失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值类型格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/insp_item/update")
    @HttpMethod("PUT")
    public void update(PlInspItem inspItem) {
        try {
            boolean success = service.update(inspItem);
            if (success) {
                renderJson(Result.success("更新成功"));
            } else {
                renderJson(Result.serverError("更新失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("ID或数值类型格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/insp_item/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            boolean success = service.deleteById((int) Long.parseLong(id.trim()));
            if (success) {
                renderJson(Result.success("删除成功"));
            } else {
                renderJson(Result.notFound("数据不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除时发生错误: " + e.getMessage()));
        }
    }
}