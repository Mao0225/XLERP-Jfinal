package com.xlerp.api.ClManagement.Controller;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.ClManagement.Service.JgbLcService;
import com.xlerp.api.Common.FileUploadUtils;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClJgbLc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class JgbLcController extends Controller {

    private final JgbLcService jgbLcService = new JgbLcService();

    @ActionKey("/cljgbLc/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String mafactory = getPara("mafactory");
        String matMaterial = getPara("matMaterial");
        String orderno = getPara("orderno");
        String matRecheckNo = getPara("matRecheckNo");

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

            Page page = jgbLcService.paginate(pageNum, pageSz, mafactory, matMaterial, orderno, matRecheckNo, leavefactoryDate, detectionTime, certificate, contractNo, woNo, ipoNo, writer, writeTime);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/jgbLc/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("数据ID不能为空"));
            return;
        }

        try {
            ClJgbLc jgbLc = jgbLcService.findById(Integer.parseInt(id));
            if (jgbLc != null) {
                renderJson(Result.success("查询数据成功").putData("jgbLc", jgbLc));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数据ID格式错误"));
        }
    }

    @ActionKey("/jgbLc/save")
    @HttpMethod("POST")
    public void save(ClJgbLc jgbLc) {
        // 校验必填字段

        try {
            boolean success = jgbLcService.save(jgbLc);
            if (success) {
                renderJson(Result.success("数据保存成功").putData("jgbLc", jgbLc.getId()));
            } else {
                renderJson(Result.serverError("保存数据失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存数据时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/jgbLc/update")
    @HttpMethod("PUT")
    public void update(ClJgbLc jgbLc) {

        try {

            boolean success = jgbLcService.update(jgbLc);
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

    @ActionKey("/jgbLc/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("数据ID不能为空"));
            return;
        }

        try {
            boolean success = jgbLcService.deleteById(Integer.parseInt(id.trim()));
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




    @ActionKey("/jgbLc/getWoNoList")
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
            Page page = jgbLcService.getWoNoList(pageNum, pageSz, woNo, ipoNo, contractNo);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
}
