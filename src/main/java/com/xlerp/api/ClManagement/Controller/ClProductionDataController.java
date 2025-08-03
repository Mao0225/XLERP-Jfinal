package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.ClManagement.Service.ClProductionDataService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClProductionData;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import java.util.Date;

@Before(HttpMethodInterceptor.class)
public class ClProductionDataController extends Controller {
    private final ClProductionDataService clProductionDataService = new ClProductionDataService();

    @ActionKey("/clproductiondata/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String productname = getPara("productname");
        String productmodel = getPara("productmodel");
        String productionbatch = getPara("productionbatch");
        String processingmethod = getPara("processingmethod");
        String processingquantity = getPara("processingquantity");
        String productioncompletiontime = getPara("productioncompletiontime");
        String schedulingplanno = getPara("schedulingplanno");
        String contractNo = getPara("contractNo");
        String woNo = getPara("woNo");
        String ipoNo = getPara("ipoNo");
        String writer = getPara("writer");
        String writeTime = getPara("writeTime");
        String isdelete = getPara("isdelete");
        String status = getPara("status");
        String flag = getPara("flag");
        String type = getPara("type");
        String memo = getPara("memo");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = clProductionDataService.paginate(
                    pageNum, pageSz, productname, productmodel, productionbatch, processingmethod,
                    processingquantity != null && !processingquantity.trim().isEmpty() ? Integer.parseInt(processingquantity) : null,
                    productioncompletiontime, schedulingplanno, contractNo, woNo, ipoNo, writer, writeTime,
                    isdelete != null && !isdelete.trim().isEmpty() ? Integer.parseInt(isdelete) : null,
                    status, flag, type, memo
            );
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码、每页大小、processingquantity或isdelete格式错误"));
        }
    }

    @ActionKey("/clproductiondata/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            ClProductionData clProductionData = clProductionDataService.findById(Integer.parseInt(id));
            if (clProductionData != null) {
                renderJson(Result.success("查询成功").putData("clProductionData", clProductionData));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("ID格式错误"));
        }
    }

    @ActionKey("/clproductiondata/save")
    @HttpMethod("POST")
    public void save(ClProductionData clProductionData) {
        try {
            boolean success = clProductionDataService.save(clProductionData);
            if (success) {
                renderJson(Result.success("保存成功").putData("clProductionDataId", clProductionData.getId()));
            } else {
                renderJson(Result.serverError("保存失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值类型格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/clproductiondata/update")
    @HttpMethod("PUT")
    public void update(ClProductionData clProductionData) {
        try {
            boolean success = clProductionDataService.update(clProductionData);
            if (success) {
                renderJson(Result.success("更新成功"));
            } else {
                renderJson(Result.serverError("更新失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("ID或数值类型格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/clproductiondata/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            boolean success = clProductionDataService.deleteById((int) Long.parseLong(id.trim()));
            if (success) {
                renderJson(Result.success("删除成功"));
            } else {
                renderJson(Result.notFound("数据不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除时发生错误: " + e.getMessage()));
        }
    }
}