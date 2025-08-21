package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.ClManagement.Service.ClXcxjFcService;
import com.xlerp.api.PlManagement.Service.PlshengchangongdanService;
import com.xlerp.common.model.ClXcxjFc;
import com.xlerp.common.model.Plshengchangongdan;
import com.jfinal.plugin.activerecord.Page;

@Before(HttpMethodInterceptor.class)
public class ClXcxjFcController extends Controller {
    private final ClXcxjFcService clXcxjFcService = new ClXcxjFcService();
    private final PlshengchangongdanService plshengchangongdanService = new PlshengchangongdanService();

    @ActionKey("/clxcxjfc/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String maQuality = getPara("maQuality");
        String matRecheckNo = getPara("matRecheckNo");
        String orderno = getPara("orderno");
        String mafactory = getPara("mafactory");
        String sampleNumber = getPara("sampleNumber");
        String testResult = getPara("testResult");
        String leavefactoryDate = getPara("leavefactoryDate");
        String detectionTime = getPara("detectionTime");
        String certificate = getPara("certificate");
        String contractNo = getPara("contractNo");
        String woNo = getPara("woNo");
        String ipoNo = getPara("ipoNo");
        String writer = getPara("writer");
        String writeTime = getPara("writeTime");
        String flag = getPara("flag");
        String status = getPara("status");
        String memo = getPara("memo");
        String type = getPara("type");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = clXcxjFcService.paginate(
                    pageNum, pageSz, maQuality, matRecheckNo, orderno, mafactory, sampleNumber, testResult, leavefactoryDate,
                    detectionTime, certificate, contractNo, woNo, ipoNo, writer, writeTime, flag, status, memo, type
            );
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/clxcxjfc/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            ClXcxjFc clXcxjFc = clXcxjFcService.findById(Integer.parseInt(id));
            if (clXcxjFc != null) {
                renderJson(Result.success("查询成功").putData("clXcxjFc", clXcxjFc));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("ID格式错误"));
        }
    }

    @ActionKey("/clxcxjfc/save")
    @HttpMethod("POST")
    public void save(ClXcxjFc clXcxjFc) {
        try {
            fillIpoNoAndContractNo(clXcxjFc);
            boolean success = clXcxjFcService.save(clXcxjFc);
            if (success) {
                renderJson(Result.success("保存成功").putData("clXcxjFcId", clXcxjFc.getId()));
            } else {
                renderJson(Result.serverError("保存失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值类型格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/clxcxjfc/update")
    @HttpMethod("PUT")
    public void update(ClXcxjFc clXcxjFc) {
        try {
            fillIpoNoAndContractNo(clXcxjFc);
            boolean success = clXcxjFcService.update(clXcxjFc);
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

    @ActionKey("/clxcxjfc/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            boolean success = clXcxjFcService.deleteById(Integer.parseInt(id.trim()));
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

    /**
     * 通过woNo获取工单详情（含ipoNo和contractNo），前端自动填充用
     */
    @ActionKey("/clxcxjfc/getGongdanByWoNo")
    @HttpMethod("GET")
    public void getGongdanByWoNo() {
        String woNo = getPara("woNo");
        if (woNo == null || woNo.trim().isEmpty()) {
            renderJson(Result.badRequest("生产工单号不能为空"));
            return;
        }
        Plshengchangongdan gd = plshengchangongdanService.findByWoNo(woNo.trim());
        if (gd == null) {
            renderJson(Result.notFound("未找到对应的生产工单"));
        } else {
            renderJson(Result.success("查询成功")
                    .putData("ipoNo", gd.getIpoNo())
                    .putData("contractNo", gd.getContractNo())
                    .putData("gongDan", gd)
            );
        }
    }

    // 自动填充方法（保存/更新时调用，仅填充ipoNo和contractNo）
    private void fillIpoNoAndContractNo(ClXcxjFc clXcxjFc) {
        String woNo = clXcxjFc.getWoNo();
        if (woNo != null && !woNo.trim().isEmpty()) {
            Plshengchangongdan gd = plshengchangongdanService.findByWoNo(woNo.trim());
            if (gd != null) {
                clXcxjFc.setIpoNo(gd.getIpoNo());
                clXcxjFc.setContractNo(gd.getContractNo());
            }
        }
    }
}