package com.xlerp.api.PlManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlManagement.Service.PlentityIdService;
import com.xlerp.common.model.Plentityid;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import java.util.Date;

@Before(HttpMethodInterceptor.class)
public class PlentityIdController extends Controller {
    private final PlentityIdService plentityIdService = new PlentityIdService();

    @ActionKey("/plentityid/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String purchaserHqCode = getPara("purchaserHqCode");
        String supplierCode = getPara("supplierCode");
        String supplierName = getPara("supplierName");
        String entityCode = getPara("entityCode");
        String poItemId = getPara("poItemId");
        String entityStatus = getPara("entityStatus");
        String dataSource = getPara("dataSource");
        String dataSourceCreateTime = getPara("dataSourceCreateTime");
        String remark = getPara("remark");
        String ownerId = getPara("ownerId");
        String openId = getPara("openId");
        String status = getPara("status");
        String flag = getPara("flag");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = plentityIdService.paginate(
                    pageNum, pageSz, purchaserHqCode, supplierCode, supplierName, entityCode,
                    poItemId != null && !poItemId.trim().isEmpty() ? Long.parseLong(poItemId) : null,
                    entityStatus, dataSource, dataSourceCreateTime, remark, ownerId, openId, status, flag
            );
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码、每页大小或poItemId格式错误"));
        }
    }

    @ActionKey("/plentityid/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("实物ID不能为空"));
            return;
        }

        try {
            Plentityid plentityid = plentityIdService.findById(Integer.parseInt(id));
            if (plentityid != null) {
                renderJson(Result.success("查询成功").putData("plentityid", plentityid));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("实物ID格式错误"));
        }
    }

    @ActionKey("/plentityid/save")
    @HttpMethod("POST")
    public void save(Plentityid plentityid) {
        try {
            boolean success = plentityIdService.save(plentityid);
            if (success) {
                renderJson(Result.success("保存成功").putData("plentityId", plentityid.getId()));
            } else {
                renderJson(Result.serverError("保存失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值类型格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/plentityid/update")
    @HttpMethod("PUT")
    public void update(Plentityid plentityid) {
        try {
            boolean success = plentityIdService.update(plentityid);
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

    @ActionKey("/plentityid/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            boolean success = plentityIdService.deleteById((int) Long.parseLong(id.trim()));
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