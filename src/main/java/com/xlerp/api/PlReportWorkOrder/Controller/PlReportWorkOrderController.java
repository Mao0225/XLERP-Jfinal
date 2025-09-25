package com.xlerp.api.PlReportWorkOrder.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlReportWorkOrder.Service.PlReportWorkOrderService;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlReportWorkOrder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class PlReportWorkOrderController extends Controller {
    private final PlReportWorkOrderService orderService = new PlReportWorkOrderService();

    @ActionKey("/pl_report_work_order/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String status = getPara("status");
        String contractNo = getPara("contractNo");
        String contractName = getPara("contractName");
        String reportNo = getPara("reportNo");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson (Result.badRequest ("页码或每页大小必须为正整数"));
                return;
            }

            Page page = orderService.paginate (pageNum, pageSz, contractNo, contractName, reportNo,status);
            renderJson (Result.success ("查询成功").putData ("page", page));
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/pl_report_work_order/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            PlReportWorkOrder pl_report_work_order = orderService.findById (Integer.parseInt (id));
            if (pl_report_work_order != null ) {
                renderJson (Result.success ("查询记录成功").putData ("order", pl_report_work_order));
            } else {
                renderJson (Result.notFound ("记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        }
    }


    //根据工单编号获取报工单列表
    @ActionKey("/pl_report_work_order/getListByWoNo")
    @HttpMethod("GET")
    public void getByWoNo() {
        String woNo = getPara("woNo");

        if (woNo == null || woNo.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("工单编号不能为空"));
            return;
        }

        try {
            List<PlReportWorkOrder> orderList = orderService.findBywoNo (woNo);
            if (orderList != null ) {
                renderJson (Result.success ("查询记录成功").putData ("orderList", orderList));
            } else {
                renderJson (Result.notFound ("记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        }
    }

    @ActionKey ("/pl_report_work_order/save")
    @HttpMethod ("POST")
    public void save (PlReportWorkOrder pl_report_work_order) {
        try {
            boolean success = orderService.save (pl_report_work_order);
            if (success) {
                renderJson (Result.success ("记录保存成功").putData ("recordId", pl_report_work_order.getId ()));
            } else {
                renderJson (Result.serverError ("保存记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/pl_report_work_order/update")
    @HttpMethod ("PUT")
    public void update (PlReportWorkOrder pl_report_work_order) {
        try {
            boolean success = orderService.update (pl_report_work_order);
            if (success) {
                renderJson (Result.success ("记录更新成功"));
            } else {
                renderJson (Result.serverError ("更新记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/pl_report_work_order/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            boolean success = orderService.DeleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("记录删除成功"));
            } else {
                renderJson (Result.notFound ("记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/pl_report_work_order/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("记录 ID 列表不能为空"));
                return;
            }

            boolean success = orderService.batchDelete(idList);
            if (success) {
                renderJson (Result.success ("批量删除记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/pl_report_work_order/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = orderService.updateStatus(id,status);
            if (success) {
                renderJson(Result.success("状态更新成功"));
            } else {
                renderJson(Result.serverError("更新状态失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("确认排产计划时发生错误:" + e.getMessage ()));
        }
    }
}