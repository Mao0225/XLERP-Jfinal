package com.xlerp.api.PlInspectionController.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlInspectionController.Service.InspOrderService;
import com.xlerp.common.model.PlInspOrder;

@Before(HttpMethodInterceptor.class)
public class InspOrderController extends Controller {
    private final InspOrderService service = new InspOrderService();

    /**
     * 创建检验主单（报检）
     * 自动生成 orderNo，初始状态为“草稿”
     */
    @ActionKey("/insp_order/save")
    @HttpMethod("POST")
    public void save(PlInspOrder order) {
        boolean success = order.save();
        renderJson(success ? Result.success("创建成功").putData("id", order.getId())
                : Result.serverError("创建失败"));
    }

    /**
     * 分页查询检验单列表
     */
    @ActionKey("/insp_order/getpage")
    @HttpMethod("GET")
    public void getpage() {
        // 同 InspItemController.getpage() 逻辑
    }

    /**
     * 获取检验单详情（含结果、标准对比）
     */
    @ActionKey("/insp_order/get")
    @HttpMethod("GET")
    public void get() {
        // 联表查询
    }

    /**
     * 提交报检审核
     */
    @ActionKey("/insp_order/submit_report")
    @HttpMethod("PATCH")
    public void submitReport() {
        String id = getPara("id");
        boolean success = service.updateStatus(parseLong(id), "报检中");
        renderJson(success ? Result.success("提交成功") : Result.serverError("提交失败"));
    }

    /**
     * 开始检验
     */
    @ActionKey("/insp_order/start_inspect")
    @HttpMethod("PATCH")
    public void startInspect() {
        String id = getPara("id");
        boolean success = service.updateStatus(parseLong(id), "检验中");
        renderJson(success ? Result.success("开始检验") : Result.serverError("操作失败"));
    }

    /**
     * 提交入库申请
     */
    @ActionKey("/insp_order/submit_stock")
    @HttpMethod("PATCH")
    public void submitStock() {
        String id = getPara("id");
        boolean success = service.updateStatus(parseLong(id), "待入库");
        renderJson(success ? Result.success("提交入库") : Result.serverError("操作失败"));
    }

    /**
     * 入库确认（通过/拒绝）
     */
    @ActionKey("/insp_order/confirm_stock")
    @HttpMethod("PATCH")
    public void confirmStock() {
        String id = getPara("id");
        String pass = getPara("pass");
        String remark = getPara("stockRemark");
        String status = "true".equals(pass) ? "已入库" : "拒绝入库";
        boolean success = service.updateStatusAndRemark(parseLong(id), status, remark);
        renderJson(success ? Result.success("入库确认完成") : Result.serverError("操作失败"));
    }

    private long parseLong(String s) {
        return Long.parseLong(s.trim());
    }
}