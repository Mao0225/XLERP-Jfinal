package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.ClManagement.Service.DxlsService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClDxls;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class DxlsController extends Controller {
    // 使用镀锌螺栓服务类
    private final DxlsService DxlsService = new DxlsService();

    /**
     * 分页查询镀锌螺栓数据
     */
    @ActionKey("/cl_dxls/getpage")
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

            // 查询镀锌螺栓数据分页
            Page<ClDxls> page = DxlsService.paginate(pageNum, pageSz, mafactory, matRecheckNo, contractNo, contractName, material, type, status);
            renderJson(Result.success("镀锌螺栓数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_dxls/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("镀锌螺栓记录ID不能为空"));
            return;
        }

        try {
            ClDxls Dxls = DxlsService.findById (Integer.parseInt (id));
            if (Dxls != null ) {
                renderJson (Result.success ("镀锌螺栓记录查询成功").putData ("record", Dxls));
            } else {
                renderJson (Result.notFound ("镀锌螺栓记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("镀锌螺栓记录ID格式错误"));
        }
    }

    @ActionKey ("/cl_dxls/save")
    @HttpMethod ("POST")
    public void save (ClDxls Dxls) {
        try {
            boolean success = DxlsService.save (Dxls);
            if (success) {
                renderJson (Result.success ("镀锌螺栓记录保存成功").putData ("recordId", Dxls.getId ()));
            } else {
                renderJson (Result.serverError ("镀锌螺栓记录保存失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存镀锌螺栓记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/cl_dxls/update")
    @HttpMethod ("PUT")
    public void update (ClDxls Dxls) {
        try {
            boolean success = DxlsService.update (Dxls);
            if (success) {
                renderJson (Result.success ("镀锌螺栓记录更新成功"));
            } else {
                renderJson (Result.serverError ("镀锌螺栓记录更新失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新镀锌螺栓记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_dxls/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("镀锌螺栓记录ID不能为空"));
            return;
        }

        try {
            boolean success = DxlsService.deleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("镀锌螺栓记录删除成功"));
            } else {
                renderJson (Result.notFound ("镀锌螺栓记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("镀锌螺栓记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除镀锌螺栓记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_dxls/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("镀锌螺栓记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("镀锌螺栓记录ID列表不能为空"));
                return;
            }

            boolean success = DxlsService.batchDelete(idList);
            if (success) {
                renderJson (Result.success ("批量删除镀锌螺栓记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除镀锌螺栓记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("镀锌螺栓记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除镀锌螺栓记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_dxls/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        String updatePerson = getPara("updatePerson");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = DxlsService.updateStatus(id,status,updatePerson);
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