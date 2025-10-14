package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.ClManagement.Service.LgService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClLg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class LgController extends Controller {
    // 使用铝管服务类
    private final LgService lgService = new LgService();

    /**
     * 分页查询铝管数据
     */
    @ActionKey("/cl_lg/getpage")
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
        String basNo = getPara("basNo");



        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 查询铝管数据分页
            Page<ClLg> page = lgService.paginate(pageNum, pageSz, mafactory, matRecheckNo, contractNo, contractName, material, type, status,basNo);
            renderJson(Result.success("铝管数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_lg/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铝管记录ID不能为空"));
            return;
        }

        try {
            ClLg lg = lgService.findById (Integer.parseInt (id));
            if (lg != null ) {
                renderJson (Result.success ("铝管记录查询成功").putData ("record", lg));
            } else {
                renderJson (Result.notFound ("铝管记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("铝管记录ID格式错误"));
        }
    }

    @ActionKey ("/cl_lg/save")
    @HttpMethod ("POST")
    public void save (ClLg lg) {
        try {
            boolean success = lgService.save (lg);
            if (success) {
                renderJson (Result.success ("铝管记录保存成功").putData ("recordId", lg.getId ()));
            } else {
                renderJson (Result.serverError ("铝管记录保存失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存铝管记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/cl_lg/update")
    @HttpMethod ("PUT")
    public void update (ClLg lg) {
        try {
            boolean success = lgService.update (lg);
            if (success) {
                renderJson (Result.success ("铝管记录更新成功"));
            } else {
                renderJson (Result.serverError ("铝管记录更新失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新铝管记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_lg/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铝管记录ID不能为空"));
            return;
        }

        try {
            boolean success = lgService.deleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("铝管记录删除成功"));
            } else {
                renderJson (Result.notFound ("铝管记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("铝管记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除铝管记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_lg/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铝管记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("铝管记录ID列表不能为空"));
                return;
            }

            boolean success = lgService.batchDelete(idList);
            if (success) {
                renderJson (Result.success ("批量删除铝管记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除铝管记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("铝管记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除铝管记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_lg/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        String updatePerson = getPara("updatePerson");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = lgService.updateStatus(id,status,updatePerson);
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
