package com.xlerp.api.PlInspectionController.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlInspectionController.Service.InspWorkOrderService;
import com.xlerp.common.model.PlInspWorkOrder;

//生产工单报工检验单即产成品半成品检验单
@Before(HttpMethodInterceptor.class)
public class InspWorkOrderController extends Controller {
    private final InspWorkOrderService service = new InspWorkOrderService();

    /**
     * 创建检验主单（报检）
     * 自动生成 orderNo，初始状态为“草稿”
     */
    @ActionKey("/insp_work_order/save")
    @HttpMethod("POST")
    public void save(PlInspWorkOrder order) {
        boolean success = order.save();
        renderJson(success ? Result.success("创建成功").putData("id", order.getId())
                : Result.serverError("创建失败"));
    }

    /**
     * 更新检验单
     */
    @ActionKey("/insp_work_order/update")
    @HttpMethod("PUT")
    public void update(PlInspWorkOrder order) {
        boolean success = order.update();
        renderJson(success ? Result.success("更新成功") : Result.serverError("更新失败"));
    }

    /**
     * 分页查询检验单列表
     */
    @ActionKey("/insp_work_order/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String param = getPara("param");
        String status = getPara("status");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = service.paginate(pageNum, pageSz,param,status);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码、每页大小或poItemId格式错误"));
        }
    }

    /**
     * 删除检验单
     */
    @ActionKey("/insp_work_order/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");
        boolean success = service.deleteById(parseLong(id));
        renderJson(success ? Result.success("删除成功") : Result.serverError("删除失败"));
    }

    /**
     * 获取检验单详情
     */
    @ActionKey("/insp_work_order/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            PlInspWorkOrder inspOrder = service.findById(Integer.parseInt(id));
            if (inspOrder != null) {
                renderJson(Result.success("查询成功").putData("inspOrder", inspOrder));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("实物ID格式错误"));
        }
    }


    /**
     * 更新检验单状态
     */
    @ActionKey("/insp_work_order/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String orderId = getPara("orderId");
        String newStatus = getPara("newStatus");
        String operator = getPara("operator");
        String remark = getPara("remark");
        boolean success = service.updateStatusAndRemark(parseLong(orderId),newStatus,operator, remark);
        renderJson(success ? Result.success("更新成功") : Result.serverError("更新失败"));
    }

    private long parseLong(String s) {
        return Long.parseLong(s.trim());
    }

}
