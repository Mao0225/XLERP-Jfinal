package com.xlerp.api.PlInspectionController.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlInspectionController.Service.InspResultService;

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
     * 自动判定检验结果合格性
     * 根据标准值对比 realValue
     */
    @ActionKey("/insp_result/auto_judge")
    @HttpMethod("POST")
    public void autoJudge() {
        String orderId = getPara("orderId");
        if (isBlank(orderId)) {
            renderJson(Result.badRequest("orderId 不能为空"));
            return;
        }
        boolean success = service.autoJudge(parseLong(orderId));
        renderJson(success ? Result.success("判定完成") : Result.serverError("判定失败"));
    }

    /**
     * 获取平行试验平均值
     */
    @ActionKey("/insp_result/avg")
    @HttpMethod("GET")
    public void getAvg() {
        String orderId = getPara("orderId");
        String itemId = getPara("itemId");
        // 返回平均值
    }

    private long parseLong(String s) {
        return Long.parseLong(s.trim());
    }
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}