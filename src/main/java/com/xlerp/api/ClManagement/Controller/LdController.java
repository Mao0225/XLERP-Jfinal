package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.ClManagement.Service.LdService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.ClLd;  // 铝锭相关模型

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class LdController extends Controller {
    // 使用铝锭服务类
    private final LdService ldService = new LdService();

    /**
     * 分页查询铝锭数据
     */
    @ActionKey("/cl_ld/getpage")
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

            // 查询铝锭数据分页
            Page<ClLd> page = ldService.paginate(pageNum, pageSz, mafactory, inNo, matMaterial, matRecheckNo);
            renderJson(Result.success("铝锭数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_ld/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铝锭记录ID不能为空"));
            return;
        }

        try {
            ClLd ld = ldService.findById (Integer.parseInt (id));
            if (ld != null ) {
                renderJson (Result.success ("铝锭记录查询成功").putData ("record", ld));
            } else {
                renderJson (Result.notFound ("铝锭记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("铝锭记录ID格式错误"));
        }
    }

    @ActionKey ("/cl_ld/save")
    @HttpMethod ("POST")
    public void save (ClLd ld) {
        try {
            boolean success = ldService.save (ld);
            if (success) {
                renderJson (Result.success ("铝锭记录保存成功").putData ("recordId", ld.getId ()));
            } else {
                renderJson (Result.serverError ("铝锭记录保存失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存铝锭记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/cl_ld/update")
    @HttpMethod ("PUT")
    public void update (ClLd ld) {
        try {
            boolean success = ldService.update (ld);
            if (success) {
                renderJson (Result.success ("铝锭记录更新成功"));
            } else {
                renderJson (Result.serverError ("铝锭记录更新失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新铝锭记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_ld/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铝锭记录ID不能为空"));
            return;
        }

        try {
            boolean success = ldService.deleteById (Integer.parseInt (id.trim ()));
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

    @ActionKey("/cl_ld/batchdelete")
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

            boolean success = ldService.batchDelete(idList);
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
}
