package com.xlerp.api.PlManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;

import com.xlerp.api.PlManagement.Service.PlinoutstoreService;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.Plinoutstore;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class PlinoutstoreController extends Controller {
    private final PlinoutstoreService plinoutstoreService = new PlinoutstoreService();

    @ActionKey("/plinoutstore/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String storeNo = getPara("storeNo");
        String storeName = getPara("storeName");
        String type = getPara("type");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = plinoutstoreService.paginate(pageNum, pageSz, storeNo, storeName, type);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/plinoutstore/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("库存记录ID不能为空"));
            return;
        }

        try {
            Plinoutstore plinoutstore = plinoutstoreService.findById(Integer.parseInt(id));
            if (plinoutstore != null && plinoutstore.getIsdelete() == 0) {
                renderJson(Result.success("查询库存记录成功").putData("plinoutstore", plinoutstore));
            } else {
                renderJson(Result.notFound("库存记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("库存记录ID格式错误"));
        }
    }

    @ActionKey("/plinoutstore/save")
    @HttpMethod("POST")
    public void save(Plinoutstore plinoutstore) {
        try {
            plinoutstore.setIsdelete(0); // 设置为正常状态
            boolean success = plinoutstoreService.save(plinoutstore);
            if (success) {
                renderJson(Result.success("库存记录保存成功").putData("storeId", plinoutstore.getId()));
            } else {
                renderJson(Result.serverError("保存库存记录失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存库存记录时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/plinoutstore/update")
    @HttpMethod("PUT")
    public void update(Plinoutstore plinoutstore) {
        try {
            boolean success = plinoutstoreService.update(plinoutstore);
            if (success) {
                renderJson(Result.success("库存记录更新成功"));
            } else {
                renderJson(Result.serverError("更新库存记录失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("更新库存记录时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/plinoutstore/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("库存记录ID不能为空"));
            return;
        }

        try {
            boolean success = plinoutstoreService.logicalDeleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("库存记录删除成功"));
            } else {
                renderJson(Result.notFound("库存记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("库存记录ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除库存记录时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/plinoutstore/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim().isEmpty()) {
            renderJson(Result.badRequest("库存记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty()) {
                renderJson(Result.badRequest("库存记录ID列表不能为空"));
                return;
            }

            boolean success = plinoutstoreService.batchLogicalDelete(idList);
            if (success) {
                renderJson(Result.success("批量删除库存记录成功"));
            } else {
                renderJson(Result.serverError("批量删除库存记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("库存记录ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("批量删除库存记录时发生错误: " + e.getMessage()));
        }
    }
}