package com.xlerp.api.PlInspectionController.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlInspectionController.Service.InspResultService;
import com.xlerp.common.model.PlInspResult;

import java.util.List;

@Before(HttpMethodInterceptor.class)
public class InspResultController extends Controller {
    private final InspResultService service = new InspResultService();

    /**
     * 批量录入检验结果
     * 支持平行试验多条数据
     */
    @ActionKey("/insp_result/batch_save")
    @HttpMethod("POST")
    public void batchSave() {
        String orderId = getPara("orderId");
        String resultsJson = getPara("results");
        if (isBlank(orderId) || isBlank(resultsJson)) {
            renderJson(Result.badRequest("参数错误"));
            return;
        }
        boolean success = service.batchSave(parseLong(orderId), resultsJson);
        renderJson(success ? Result.success("录入成功") : Result.serverError("录入失败"));
    }

    /**
     * 保存检验结果单条
     */
    @ActionKey("/insp_result/save")
    @HttpMethod("POST")
    public void save(PlInspResult result) {
        boolean success = result.save();
        renderJson(success ? Result.success("保存成功") : Result.serverError("保存失败"));
    }

    /**
     * 更新检验结果单条
     */
    @ActionKey("/insp_result/update")
    @HttpMethod("PUT")
    public void update(PlInspResult result) {
        boolean success = result.update();
        renderJson(success ? Result.success("更新成功") : Result.serverError("更新失败"));
    }

    /**
     * 删除检验结果单条
     */
    @ActionKey("/insp_result/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");
        boolean success = service.deleteById(parseLong(id));
        renderJson(success ? Result.success("删除成功") : Result.serverError("删除失败"));
    }

    /**
     * 获取检验值列表根据订单id
     */
    @ActionKey("/insp_result/getResultByOrderId")
    @HttpMethod("GET")
    public void getResultByOrderId() {
        String orderId = getPara("orderId");
        if (isBlank(orderId)) {
            renderJson(Result.badRequest("参数错误"));
            return;
        }
        try {
            List<Record> list = service.getListByOrderId(parseLong(orderId));
            renderJson(Result.success("查询成功").putData("list", list));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码、每页大小或Id格式错误"));
        }
    }


    private long parseLong(String s) {
        return Long.parseLong(s.trim());
    }
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}