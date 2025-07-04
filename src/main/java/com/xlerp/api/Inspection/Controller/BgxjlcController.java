package com.xlerp.api.Inspection.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.Inspection.Service.BgxjlcService;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.Bgxjlc;

@Before(HttpMethodInterceptor.class)
public class BgxjlcController extends Controller {
    private final BgxjlcService bgxjlcService = new BgxjlcService();

    @ActionKey("/bgxjlc/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String contractNo = getPara("contractNo");
        String supplier = getPara("supplier");
        String paiId = getPara("paiId");
        String inspectionNo = getPara("inspectionNo");
        String Al = getPara("Al");
        String Si = getPara("Si");
        String Fe = getPara("Fe");
        String Cu = getPara("Cu");
        String Mg = getPara("Mg");
        String Mn = getPara("Mn");
        String Zn = getPara("Zn");
        String Ti = getPara("Ti");
        String Cr = getPara("Cr");
        String lstrength = getPara("lstrength");
        String elongation = getPara("elongation");
        String outDate = getPara("outDate");
        String inDate = getPara("inDate");
        String certificate = getPara("certificate");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;
            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = bgxjlcService.paginate(
                    pageNum, pageSz, contractNo, supplier, paiId, inspectionNo,
                    Al, Si, Fe, Cu, Mg, Mn, Zn, Ti, Cr, lstrength, elongation, outDate, inDate, certificate
            );
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/bgxjlc/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            Bgxjlc bgxjlc = bgxjlcService.findById(Integer.parseInt(id));
            if (bgxjlc != null) {
                renderJson(Result.success("查询成功").putData("bgxjlc", bgxjlc));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("ID格式错误"));
        }
    }

    @ActionKey("/bgxjlc/save")
    @HttpMethod("POST")
    public void save(Bgxjlc bgxjlc) {
        try {
            boolean success = bgxjlcService.save(bgxjlc);
            if (success) {
                renderJson(Result.success("保存成功").putData("id", bgxjlc.getId()));
            } else {
                renderJson(Result.serverError("保存失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值类型格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/bgxjlc/update")
    @HttpMethod("PUT")
    public void update(Bgxjlc bgxjlc) {
        try {
            boolean success = bgxjlcService.update(bgxjlc);
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

    @ActionKey("/bgxjlc/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            boolean success = bgxjlcService.deleteById((int) Long.parseLong(id.trim()));
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