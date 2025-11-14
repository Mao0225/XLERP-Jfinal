package com.xlerp.api.PlPurchaseOrder.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlPurchaseOrder.Service.PurchaseOrderService;
import com.xlerp.common.model.PlPurchaseOrder;

import java.util.List;


@Before(HttpMethodInterceptor.class)
public class PurchaseOrderController extends Controller {

    private PurchaseOrderService service = new PurchaseOrderService();
    @ActionKey("/pl_purchase_order/getpage")
    @HttpMethod("GET")
    public void getpage() {
        // 获取分页参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String purchaseOrderNo = getPara("purchaseOrderNo");
        String status = getPara("status");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 调用修改后的分页查询方法，传入所有查询条件,查询status为20的就是确认状态的
            Page page = service.paginate(pageNum, pageSz,purchaseOrderNo,status);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/pl_purchase_order/save")
    @HttpMethod("POST")
    public void save(PlPurchaseOrder order) {

        boolean success = order.save();
        renderJson(success ? Result.success("创建成功") : Result.badRequest("创建失败"));
    }

    @ActionKey("/pl_purchase_order/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");
        try {
            if (id == null || id.trim().isEmpty()) {
                renderJson(Result.badRequest("记录 ID 不能为空"));
                return;
            }
            boolean success = service.deleteById(id);
            renderJson(success ? Result.success("删除成功") : Result.badRequest("删除失败"));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("记录 ID 格式错误"));
        }
    }

    @ActionKey("/pl_purchase_order/update")
    @HttpMethod("PUT")
    public void update(PlPurchaseOrder order) {
        boolean success = order.update();
        renderJson(success ? Result.success("更新成功") : Result.badRequest("更新失败"));
    }

    //根据采购订单号获取采购订单的物料列表
    @ActionKey("/pl_purchase_order/getMaterialList")
    @HttpMethod("GET")
    public void getMaterialList() {
        String purchaseOrderNo = getPara("purchaseOrderNo");
        try {
            if (purchaseOrderNo == null || purchaseOrderNo.trim().isEmpty()){
                renderJson(Result.badRequest("合同编号不能为空"));
            }
            List record = service.getMaterialList(purchaseOrderNo);
            renderJson(Result.success("查询备料列表成功").putData("record", record));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("合同号格式错误"));
        }
    }

    //根据采购订单号和备料清单id列表批量设置采购订单号
    @ActionKey("/pl_purchase_order/setPurchaseOrderNo")
    @HttpMethod("GET")
    public void setPurchaseOrderNo() {
        String purchaseOrderNo = getPara("purchaseOrderNo");
        String materialIds = getPara("materialIds");
        try {
            if (purchaseOrderNo == null || purchaseOrderNo.trim().isEmpty()){
                renderJson(Result.badRequest("合同编号不能为空"));
                return;
            }
            if (materialIds == null || materialIds.trim().isEmpty()){
                renderJson(Result.badRequest("物料ID列表不能为空"));
                return;
            }
            boolean success = service.setPurchaseOrderNo(purchaseOrderNo, materialIds);
            renderJson(success ? Result.success("设置采购订单号成功") : Result.badRequest("设置采购订单号失败"));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("合同号格式错误"));
        }
    }

}
