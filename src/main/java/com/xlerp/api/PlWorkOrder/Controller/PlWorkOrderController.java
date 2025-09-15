package com.xlerp.api.PlWorkOrder.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlWorkOrder.Service.PlWorkOrderService;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.PlWorkOrder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class PlWorkOrderController extends Controller {
    private final PlWorkOrderService workOrderService = new PlWorkOrderService();

    @ActionKey("/pl_work_order/getpage")
    @HttpMethod("GET")
    public void getpage() {
// 获取分页参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");

        // 获取查询条件参数
        String contractNo = getPara("contractNo");
        String contractName = getPara("contractName");
        String woNo = getPara("woNo");
        String status = getPara("status");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 调用修改后的分页查询方法，传入所有查询条件,查询status为20的就是确认状态的
            Page page = workOrderService.paginate(pageNum, pageSz, contractNo, contractName, woNo,status);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


    @ActionKey("/pl_work_order/getConfirmedList")
    @HttpMethod("GET")
    public void getConfirmedList() {
        // 获取分页参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");

        // 获取查询条件参数
        String contractNo = getPara("contractNo");
        String contractName = getPara("contractName");
        String woNo = getPara("woNo");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 调用修改后的分页查询方法，传入所有查询条件,查询status为20的就是确认状态的
            Page page = workOrderService.paginate(pageNum, pageSz, contractNo, contractName, woNo,"20");
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/pl_work_order/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            PlWorkOrder pl_work_order = workOrderService.findById (Integer.parseInt (id));
            if (pl_work_order != null) {
                renderJson (Result.success ("查询记录成功").putData ("order", pl_work_order));
            } else {
                renderJson (Result.notFound ("记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        }
    }

    @ActionKey ("/pl_work_order/save")
    @HttpMethod ("POST")
    public void save (PlWorkOrder pl_work_order) {
        try {
            boolean success = workOrderService.save (pl_work_order);
            if (success) {
                renderJson (Result.success ("记录保存成功").putData ("recordId", pl_work_order.getId ()));
            } else {
                renderJson (Result.serverError ("保存记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/pl_work_order/update")
    @HttpMethod ("PUT")
    public void update (PlWorkOrder pl_work_order) {
        try {
            boolean success = workOrderService.update (pl_work_order);
            if (success) {
                renderJson (Result.success ("记录更新成功"));
            } else {
                renderJson (Result.serverError ("更新记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/pl_work_order/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            boolean success = workOrderService.DeleteById (Integer.parseInt (id.trim ()));
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

    @ActionKey("/pl_work_order/batchdelete")
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

            boolean success = workOrderService.batchDelete (idList);
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


    @ActionKey("/pl_work_order/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = workOrderService.updateStatus(id,status);
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