package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.ClManagement.Service.ClLjjjFcService;
import com.xlerp.api.PlManagement.Service.PlshengchangongdanService;
import com.xlerp.common.model.ClLjjjFc;
import com.xlerp.common.model.Plshengchangongdan;
import com.jfinal.plugin.activerecord.Page;

@Before(HttpMethodInterceptor.class)
public class ClLjjjFcController extends Controller {
    private final ClLjjjFcService clLjjjFcService = new ClLjjjFcService();
    private final PlshengchangongdanService plshengchangongdanService = new PlshengchangongdanService();

    @ActionKey("/clljjjfc/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String maQuality = getPara("maQuality");
        String matRecheckNo = getPara("matRecheckNo");
        String mafactory = getPara("mafactory");
        String orderno = getPara("orderno");
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

            Page page = clLjjjFcService.paginate(
                    pageNum, pageSz, maQuality, matRecheckNo, mafactory, orderno, sampleNumber,
                    testResult, leavefactoryDate, detectionTime, certificate, contractNo, woNo, ipoNo,
                    writer, writeTime, flag, status, memo, type
            );
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/clljjjfc/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            ClLjjjFc clLjjjFc = clLjjjFcService.findById(Integer.parseInt(id));
            if (clLjjjFc != null) {
                renderJson(Result.success("查询成功").putData("clLjjjFc", clLjjjFc));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("ID格式错误"));
        }
    }

    @ActionKey("/clljjjfc/save")
    @HttpMethod("POST")
    public void save(ClLjjjFc clLjjjFc) {
        try {
            fillIpoNoAndContractNo(clLjjjFc);
            boolean success = clLjjjFcService.save(clLjjjFc);
            if (success) {
                renderJson(Result.success("保存成功").putData("clLjjjFcId", clLjjjFc.getId()));
            } else {
                renderJson(Result.serverError("保存失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值类型格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/clljjjfc/update")
    @HttpMethod("PUT")
    public void update(ClLjjjFc clLjjjFc) {
        try {
            fillIpoNoAndContractNo(clLjjjFc);
            boolean success = clLjjjFcService.update(clLjjjFc);
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

    @ActionKey("/clljjjfc/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            boolean success = clLjjjFcService.deleteById(Integer.parseInt(id.trim()));
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
    @ActionKey("/clljjjfc/getGongdanByWoNo")
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
    private void fillIpoNoAndContractNo(ClLjjjFc clLjjjFc) {
        String woNo = clLjjjFc.getWoNo();
        if (woNo != null && !woNo.trim().isEmpty()) {
            Plshengchangongdan gd = plshengchangongdanService.findByWoNo(woNo.trim());
            if (gd != null) {
                clLjjjFc.setIpoNo(gd.getIpoNo());
                clLjjjFc.setContractNo(gd.getContractNo());
            }
        }
    }
}