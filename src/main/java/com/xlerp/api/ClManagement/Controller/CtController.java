package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.ClManagement.Service.CtService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClCt;  // 铝锭相关模型

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class CtController extends Controller {
    // 使用铝锭服务类
    private final CtService ctService = new CtService();

    /**
     * 分页查询铝锭数据
     */
    @ActionKey("/cl_ct/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String mafactory = getPara("mafactory");
        String matRecheckNo = getPara("matRecheckNo");
        String contractNo = getPara("contractNo");
        String contractName = getPara("contractName");
        String material = getPara("material");
        String type = getPara("type");
        String status = getPara("status");



        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 查询铝锭数据分页
            Page<ClCt> page = ctService.paginate(pageNum, pageSz, mafactory, matRecheckNo, contractNo, contractName, material, type, status);
            renderJson(Result.success("铝锭数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_ct/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铝锭记录ID不能为空"));
            return;
        }

        try {
            ClCt ct= ctService.findById (Integer.parseInt (id));
            if (ct != null ) {
                renderJson (Result.success ("铝锭记录查询成功").putData ("record", ct));
            } else {
                renderJson (Result.notFound ("铝锭记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("铝锭记录ID格式错误"));
        }
    }

    @ActionKey ("/cl_ct/save")
    @HttpMethod ("POST")
    public void save (ClCt ct) {
        try {
            boolean success = ctService.save (ct);
            if (success) {
                renderJson (Result.success ("铝锭记录保存成功").putData ("recordId", ct.getId ()));
            } else {
                renderJson (Result.serverError ("铝锭记录保存失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存铝锭记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/cl_ct/update")
    @HttpMethod ("PUT")
    public void update (ClCt ct) {
        try {
            boolean success = ctService.update (ct);
            if (success) {
                renderJson (Result.success ("铝锭记录更新成功"));
            } else {
                renderJson (Result.serverError ("铝锭记录更新失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新铝锭记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_ct/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铝锭记录ID不能为空"));
            return;
        }

        try {
            boolean success = ctService.deleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("铝锭记录删除成功"));
            } else {
                renderJson (Result.notFound ("铝锭记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("铝锭记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除铝锭记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_ct/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铝锭记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("铝锭记录ID列表不能为空"));
                return;
            }

            boolean success = ctService.batchDelete(idList);
            if (success) {
                renderJson (Result.success ("批量删除铝锭记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除铝锭记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("铝锭记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除铝锭记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_ct/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        String updatePerson = getPara("updatePerson");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = ctService.updateStatus(id,status,updatePerson);
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
