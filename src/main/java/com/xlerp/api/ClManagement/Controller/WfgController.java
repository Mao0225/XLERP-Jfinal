package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.ClManagement.Service.WfgService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClWfg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class WfgController extends Controller {
    // 使用无缝管服务类
    private final WfgService wfgService = new WfgService();

    /**
     * 分页查询无缝管数据
     */
    @ActionKey("/cl_wfg/getpage")
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
            Page<ClWfg> page = wfgService.paginate(pageNum, pageSz, mafactory, inNo, matMaterial, matRecheckNo);
            renderJson(Result.success("无缝管数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_wfg/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("无缝管记录ID不能为空"));
            return;
        }

        try {
            ClWfg ld = wfgService.findById (Integer.parseInt (id));
            if (ld != null ) {
                renderJson (Result.success ("无缝管记录查询成功").putData ("record", ld));
            } else {
                renderJson (Result.notFound ("无缝管记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("无缝管记录ID格式错误"));
        }
    }

    @ActionKey ("/cl_wfg/save")
    @HttpMethod ("POST")
    public void save (ClWfg wfg) {
        try {
            boolean success = wfgService.save (wfg);
            if (success) {
                renderJson (Result.success ("无缝管记录保存成功").putData ("recordId", wfg.getId ()));
            } else {
                renderJson (Result.serverError ("无缝管记录保存失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存无缝管记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/cl_wfg/update")
    @HttpMethod ("PUT")
    public void update (ClWfg wfg) {
        try {
            boolean success = wfgService.update (wfg);
            if (success) {
                renderJson (Result.success ("无缝管记录更新成功"));
            } else {
                renderJson (Result.serverError ("无缝管记录更新失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新无缝管记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_wfg/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("无缝管记录ID不能为空"));
            return;
        }

        try {
            boolean success = wfgService.deleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("无缝管记录删除成功"));
            } else {
                renderJson (Result.notFound ("无缝管记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("无缝管记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除无缝管记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_wfg/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("无缝管记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("无缝管记录ID列表不能为空"));
                return;
            }

            boolean success = wfgService.batchDelete(idList);
            if (success) {
                renderJson (Result.success ("批量删除无缝管记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除无缝管记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("无缝管记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除无缝管记录时发生错误:" + e.getMessage ()));
        }
    }
}
