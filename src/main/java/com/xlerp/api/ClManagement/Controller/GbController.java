package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.ClManagement.Service.GbService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClGb;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class GbController extends Controller {
    // 使用钢板服务类
    private final GbService gbService = new GbService();

    /**
     * 分页查询钢板数据
     */
    @ActionKey("/cl_gb/getpage")
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

            // 查询钢板数据分页
            Page<ClGb> page = gbService.paginate(pageNum, pageSz, mafactory, inNo, matMaterial, matRecheckNo);
            renderJson(Result.success("钢板数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_gb/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("钢板记录ID不能为空"));
            return;
        }

        try {
            ClGb gb = gbService.findById (Integer.parseInt (id));
            if (gb != null ) {
                renderJson (Result.success ("钢板记录查询成功").putData ("record", gb));
            } else {
                renderJson (Result.notFound ("钢板记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("钢板记录ID格式错误"));
        }
    }

    @ActionKey ("/cl_gb/save")
    @HttpMethod ("POST")
    public void save (ClGb gb) {
        try {
            boolean success = gbService.save (gb);
            if (success) {
                renderJson (Result.success ("钢板记录保存成功").putData ("recordId", gb.getId ()));
            } else {
                renderJson (Result.serverError ("钢板记录保存失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存钢板记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/cl_gb/update")
    @HttpMethod ("PUT")
    public void update (ClGb gb) {
        try {
            boolean success = gbService.update (gb);
            if (success) {
                renderJson (Result.success ("钢板记录更新成功"));
            } else {
                renderJson (Result.serverError ("钢板记录更新失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新钢板记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_gb/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("钢板记录ID不能为空"));
            return;
        }

        try {
            boolean success = gbService.deleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("钢板记录删除成功"));
            } else {
                renderJson (Result.notFound ("钢板记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("钢板记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除钢板记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_gb/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("钢板记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("钢板记录ID列表不能为空"));
                return;
            }

            boolean success = gbService.batchDelete(idList);
            if (success) {
                renderJson (Result.success ("批量删除钢板记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除钢板记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("钢板记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除钢板记录时发生错误:" + e.getMessage ()));
        }
    }
}
