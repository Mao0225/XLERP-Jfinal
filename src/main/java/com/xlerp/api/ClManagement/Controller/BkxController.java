package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.ClManagement.Service.BkxService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClBkx;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class BkxController extends Controller {
    // 使用闭口销服务类
    private final BkxService bkxService = new BkxService();

    /**
     * 分页查询闭口销数据
     */
    @ActionKey("/cl_bkx/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String mafactory = getPara("mafactory");
        String inNo = getPara("inNo");
        String matMaterial = getPara("matMaterial");
        String matRecheckNo = getPara("matRecheckNo");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 查询圆闭口销数据分页
            Page<ClBkx> page = bkxService.paginate(pageNum, pageSz, mafactory, inNo, matMaterial, matRecheckNo);
            renderJson(Result.success("闭口销数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_bkx/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("闭口销记录ID不能为空"));
            return;
        }

        try {
            ClBkx bkx = bkxService.findById (Integer.parseInt (id));
            if (bkx != null ) {
                renderJson (Result.success ("闭口销记录查询成功").putData ("record", bkx));
            } else {
                renderJson (Result.notFound ("闭口销记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("闭口销记录ID格式错误"));
        }
    }

    @ActionKey ("/cl_bkx/save")
    @HttpMethod ("POST")
    public void save (ClBkx bkx) {
        try {
            boolean success = bkxService.save (bkx);
            if (success) {
                renderJson (Result.success ("闭口销记录保存成功").putData ("recordId", bkx.getId ()));
            } else {
                renderJson (Result.serverError ("闭口销记录保存失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存闭口销记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/cl_bkx/update")
    @HttpMethod ("PUT")
    public void update (ClBkx bkx) {
        try {
            boolean success = bkxService.update (bkx);
            if (success) {
                renderJson (Result.success ("闭口销记录更新成功"));
            } else {
                renderJson (Result.serverError ("闭口销记录更新失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新闭口销记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_bkx/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("闭口销记录ID不能为空"));
            return;
        }

        try {
            boolean success = bkxService.deleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("闭口销记录删除成功"));
            } else {
                renderJson (Result.notFound ("闭口销记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("闭口销记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除闭口销记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_bkx/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("闭口销记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("闭口销记录ID列表不能为空"));
                return;
            }

            boolean success = bkxService.batchDelete(idList);
            if (success) {
                renderJson (Result.success ("批量删除闭口销记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除闭口销记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("闭口销记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除闭口销记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_bkx/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        String updatePerson = getPara("updatePerson");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = bkxService.updateStatus(id,status,updatePerson);
            if (success) {
                renderJson(Result.success("状态更新成功"));
            }
            else {
                renderJson(Result.badRequest("更新状态失败"));
            }
        }
        catch (Exception e) {
            renderJson (Result.serverError ("更新状态时发生错误:" + e.getMessage ()));
        }
    }
}
