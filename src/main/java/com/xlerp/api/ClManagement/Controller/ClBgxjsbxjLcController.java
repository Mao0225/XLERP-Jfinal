package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.ClManagement.Service.ClBgxjsbxjLcService;
import com.xlerp.api.PlManagement.Service.PlshengchangongdanService;
import com.xlerp.common.model.ClBgxjsbxjLc;
import com.xlerp.common.model.Plshengchangongdan;
import com.jfinal.plugin.activerecord.Page;

@Before(HttpMethodInterceptor.class)
public class ClBgxjsbxjLcController extends Controller {
    private final ClBgxjsbxjLcService clBgxjsbxjLcService = new ClBgxjsbxjLcService();
    private final PlshengchangongdanService plshengchangongdanService = new PlshengchangongdanService();

    @ActionKey("/clbgxjsbxjlc/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String mafactory = getPara("mafactory");
        String matMaterial = getPara("matMaterial");
        String orderno = getPara("orderno");
        String matRecheckNo = getPara("matRecheckNo");
        String chemAl = getPara("chemAl");
        String chemSi = getPara("chemSi");
        String chemFe = getPara("chemFe");
        String chemCu = getPara("chemCu");
        String chemMg = getPara("chemMg");
        String chemMn = getPara("chemMn");
        String chemZn = getPara("chemZn");
        String chemTi = getPara("chemTi");
        String chemCr = getPara("chemCr");
        String tensileStrength = getPara("tensileStrength");
        String elongation = getPara("elongation");
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

            Page page = clBgxjsbxjLcService.paginate(
                    pageNum, pageSz, mafactory, matMaterial, orderno, matRecheckNo, chemAl, chemSi,
                    chemFe, chemCu, chemMg, chemMn, chemZn, chemTi, chemCr, tensileStrength, elongation,
                    leavefactoryDate, detectionTime, certificate, contractNo, woNo, ipoNo, writer, writeTime,
                    flag, status, memo, type
            );
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/clbgxjsbxjlc/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            ClBgxjsbxjLc clBgxjsbxjLc = clBgxjsbxjLcService.findById(Integer.parseInt(id));
            if (clBgxjsbxjLc != null) {
                renderJson(Result.success("查询成功").putData("clBgxjsbxjLc", clBgxjsbxjLc));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("ID格式错误"));
        }
    }

    @ActionKey("/clbgxjsbxjlc/save")
    @HttpMethod("POST")
    public void save(ClBgxjsbxjLc clBgxjsbxjLc) {
        try {
            fillIpoNoAndContractNo(clBgxjsbxjLc);
            boolean success = clBgxjsbxjLcService.save(clBgxjsbxjLc);
            if (success) {
                renderJson(Result.success("保存成功").putData("clBgxjsbxjLcId", clBgxjsbxjLc.getId()));
            } else {
                renderJson(Result.serverError("保存失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值类型格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/clbgxjsbxjlc/update")
    @HttpMethod("PUT")
    public void update(ClBgxjsbxjLc clBgxjsbxjLc) {
        try {
            fillIpoNoAndContractNo(clBgxjsbxjLc);
            boolean success = clBgxjsbxjLcService.update(clBgxjsbxjLc);
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

    @ActionKey("/clbgxjsbxjlc/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            boolean success = clBgxjsbxjLcService.deleteById(Integer.parseInt(id.trim()));
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
    @ActionKey("/clbgxjsbxjlc/getGongdanByWoNo")
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
    private void fillIpoNoAndContractNo(ClBgxjsbxjLc clBgxjsbxjLc) {
        String woNo = clBgxjsbxjLc.getWoNo();
        if (woNo != null && !woNo.trim().isEmpty()) {
            Plshengchangongdan gd = plshengchangongdanService.findByWoNo(woNo.trim());
            if (gd != null) {
                clBgxjsbxjLc.setIpoNo(gd.getIpoNo());
                clBgxjsbxjLc.setContractNo(gd.getContractNo());
            }
        }
    }
}