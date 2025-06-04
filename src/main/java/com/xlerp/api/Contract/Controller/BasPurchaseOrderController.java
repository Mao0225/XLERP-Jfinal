package com.xlerp.api.Contract.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;


import com.xlerp.api.Contract.Service.BasPurchaseOrderService;
import com.xlerp.common.model.BasPurchaseOrder;
import com.jfinal.plugin.activerecord.Page;

@Before(HttpMethodInterceptor.class)
public class BasPurchaseOrderController extends Controller {
    private final BasPurchaseOrderService basPurchaseOrderService = new BasPurchaseOrderService();

    @ActionKey("/baspurchaseorder/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String poNo = getPara("poNo"); // 新增 poNo 参数 采购订单编码

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = basPurchaseOrderService.paginate(pageNum, pageSz, poNo); // 传递 name 和 no
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/baspurchaseorder/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("采购单ID不能为空"));
            return;
        }

        try {
            BasPurchaseOrder basPurchaseOrder = basPurchaseOrderService.findById(Integer.parseInt(id));
            if (basPurchaseOrder != null) {
                renderJson(Result.success("查询采购单成功").putData("basPurchaseOrder", basPurchaseOrder));
            } else {
                renderJson(Result.notFound("采购单未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("采购单ID格式错误"));
        }
    }

    @ActionKey("/baspurchaseorder/save")
    @HttpMethod("POST")
    public void save(BasPurchaseOrder basPurchaseOrder) {
        try {
            if (basPurchaseOrder == null || basPurchaseOrder.getPoNo() == null || basPurchaseOrder.getPoNo().trim().isEmpty() ) {
                renderJson(Result.badRequest("采购单信息不能为空且采购单编号必须填写"));
                return;
            }
            boolean success = basPurchaseOrderService.save(basPurchaseOrder);
            if (success) {
                renderJson(Result.success("采购单保存成功").putData("basPurchaseOrderId", basPurchaseOrder.getId()));
            } else {
                renderJson(Result.serverError("保存采购单失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存采购单时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/baspurchaseorder/update")
    @HttpMethod("PUT")
    public void update(BasPurchaseOrder basPurchaseOrder) {
        try {
            if (basPurchaseOrder == null || basPurchaseOrder.getId() == null) {
                renderJson(Result.badRequest("采购单ID不能为空"));
                return;
            }
            boolean success = basPurchaseOrderService.update(basPurchaseOrder);
            if (success) {
                renderJson(Result.success("采购单更新成功"));
            } else {
                renderJson(Result.serverError("更新采购单失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("采购单ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新采购单时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/baspurchaseorder/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("采购单ID不能为空"));
            return;
        }

        try {
            boolean success = basPurchaseOrderService.deleteById(Integer.parseInt(id));
            if (success) {
                renderJson(Result.success("采购单删除成功"));
            } else {
                renderJson(Result.notFound("采购单不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("采购单ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除采购单时发生错误: " + e.getMessage()));
        }
    }

}