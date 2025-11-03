package com.xlerp.api.PlInspectionController.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlInspectionController.Service.InspStdItemService;
import com.xlerp.common.model.PlInspStdItem;

@Before(HttpMethodInterceptor.class)
public class InspStdItemController extends Controller {
    private final InspStdItemService service = new InspStdItemService();
    /**
     * 单个保存标准明细项目
     */
    @ActionKey("/insp_std_item/save")
    @HttpMethod("POST")
    public void save(PlInspStdItem item) {
        try {
            boolean success = item.save();
            if (success) {
                renderJson(Result.success("保存成功").putData("stdItemId", item.getId()));
            } else {
                renderJson(Result.serverError("保存失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值类型格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存时发生错误: " + e.getMessage()));
        }
    }

    /**
     * 更新单条标准明细（标准值范围、备注等）
     */
    @ActionKey("/insp_std_item/update")
    @HttpMethod("PUT")
    public void update(PlInspStdItem item) {
        if (item.getId() == null) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }
        boolean success = item.update();
        renderJson(success ? Result.success("更新成功") : Result.serverError("更新失败"));
    }

    /**
     * 删除标准明细项目
     */
    @ActionKey("/insp_std_item/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");
        if (isBlank(id)) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }
        boolean success = new PlInspStdItem().deleteById(parseLong(id));
        renderJson(success ? Result.success("删除成功") : Result.serverError("删除失败"));
    }

    private long parseLong(String s) {
        return Long.parseLong(s.trim());
    }
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}