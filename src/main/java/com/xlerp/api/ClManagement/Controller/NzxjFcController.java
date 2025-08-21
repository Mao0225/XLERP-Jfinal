package com.xlerp.api.ClManagement.Controller;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.ClManagement.Service.NzxjFcService;
import com.xlerp.api.Common.FileUploadUtils;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClNzxjFc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NzxjFcController extends Controller {

    private final NzxjFcService nzxjFcService = new NzxjFcService();

    @ActionKey("/clnzxjFc/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String mafactory = getPara("mafactory");
        String maQuality = getPara("maQuality");
        String orderno = getPara("orderno");
        String matRecheckNo = getPara("matRecheckNo");
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

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = nzxjFcService.paginate(pageNum, pageSz, mafactory, maQuality, orderno, matRecheckNo, sampleNumber, testResult, leavefactoryDate, detectionTime, certificate, contractNo, woNo, ipoNo, writer, writeTime);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/nzxjFc/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("数据ID不能为空"));
            return;
        }

        try {
            ClNzxjFc nzxjFc = nzxjFcService.findById(Integer.parseInt(id));
            if (nzxjFc != null) {
                renderJson(Result.success("查询数据成功").putData("nzxjFc", nzxjFc));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数据ID格式错误"));
        }
    }

    @ActionKey("/nzxjFc/save")
    @HttpMethod("POST")
    public void save(ClNzxjFc nzxjFc) {
        // 校验必填字段

        try {
            boolean success = nzxjFcService.save(nzxjFc);
            if (success) {
                renderJson(Result.success("数据保存成功").putData("nzxjFc", nzxjFc.getId()));
            } else {
                renderJson(Result.serverError("保存数据失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存数据时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/nzxjFc/update")
    @HttpMethod("PUT")
    public void update(ClNzxjFc nzxjFc) {

        try {

            boolean success = nzxjFcService.update(nzxjFc);
            if (success) {
                renderJson(Result.success("数据更新成功"));
            } else {
                renderJson(Result.serverError("更新数据失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数据ID或数值格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新数据时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/nzxjFc/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("数据ID不能为空"));
            return;
        }

        try {
            boolean success = nzxjFcService.deleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("数据删除成功"));
            } else {
                renderJson(Result.notFound("数据不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数据ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除数据时发生错误: " + e.getMessage()));
        }
    }




    @ActionKey("/nzxjFc/getWoNoList")
    @HttpMethod("GET")
    public void getWoNoList() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String woNo = getPara("woNo");//获取合同的场内编号
        String ipoNo = getPara("ipoNo");
        String contractNo = getPara("cntractNo");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 调用服务层方法获取合同号列表，将 gridno 替换为 no
            Page page = nzxjFcService.getWoNoList(pageNum, pageSz, woNo, ipoNo, contractNo);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
}
