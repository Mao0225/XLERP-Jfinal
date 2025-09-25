package com.xlerp.api.PlSchedulePlan.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlSchedulePlan.Service.PlSchedulePlanService;
import com.xlerp.common.model.PlSchedulePlan;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class PlSchedulePlanController extends Controller {
    private final PlSchedulePlanService planService = new PlSchedulePlanService();

    @ActionKey("/pl_schedule_plan/getpage")
    @HttpMethod("GET")
    public void getpage() {
        // 获取分页参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");

        // 获取查询条件参数
        String contractNo = getPara("contractNo");
        String contractName = getPara("contractName");
        String purchaserHqCode = getPara("purchaserHqCode");
        String scheduleCode = getPara("scheduleCode");
        String status = getPara("status");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 调用修改后的分页查询方法，传入所有查询条件
            Page page = planService.paginate(pageNum, pageSz, contractNo, contractName, purchaserHqCode, scheduleCode,status);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    //获取确认后的排产计划列表
    @ActionKey("/pl_schedule_plan/getConfirmedList")
    @HttpMethod("GET")
    public void getConfirmedList() {
        // 获取分页参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");

        // 获取查询条件参数
        String contractNo = getPara("contractNo");
        String contractName = getPara("contractName");
        String purchaserHqCode = getPara("purchaserHqCode");
        String scheduleCode = getPara("scheduleCode");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 调用修改后的分页查询方法，传入所有查询条件,查询status为20的就是确认状态的
            Page page = planService.paginate(pageNum, pageSz, contractNo, contractName, purchaserHqCode, scheduleCode,"30");
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }




    @ActionKey("/pl_schedule_plan/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            PlSchedulePlan pl_schedule_plan = planService.findById (Integer.parseInt (id));
            if (pl_schedule_plan != null ) {
                renderJson (Result.success ("查询记录成功").putData ("schedulePlan", pl_schedule_plan));
            } else {
                renderJson (Result.notFound ("记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        }
    }

    @ActionKey ("/pl_schedule_plan/save")
    @HttpMethod ("POST")
    public void save (PlSchedulePlan pl_schedule_plan) {
        try {
            boolean success = planService.save (pl_schedule_plan);
            if (success) {
                renderJson (Result.success ("记录保存成功").putData ("recordId", pl_schedule_plan.getId ()));
            } else {
                renderJson (Result.serverError ("保存记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/pl_schedule_plan/update")
    @HttpMethod ("PUT")
    public void update (PlSchedulePlan pl_schedule_plan) {
        try {
            boolean success = planService.update (pl_schedule_plan);
            if (success) {
                renderJson (Result.success ("记录更新成功"));
            } else {
                renderJson (Result.serverError ("更新记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/pl_schedule_plan/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            boolean success = planService.DeleteById (Integer.parseInt (id.trim ()));
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

    //确认排产计划
    @ActionKey("/pl_schedule_plan/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = planService.updateStatus(id,status);
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

    @ActionKey("/pl_schedule_plan/batchdelete")
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

            boolean success = planService.batchDelete (idList);
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
}