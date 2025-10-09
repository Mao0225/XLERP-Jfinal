package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.ClManagement.Service.DxgjxService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClDxgjx;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class DxgjxController extends Controller {
    // 使用镀锌钢绞线服务类
    private final DxgjxService dxgjxService = new DxgjxService();

    /**
     * 分页查询镀锌钢绞线数据
     */
    @ActionKey("/cl_dxgjx/getpage")
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

            // 查询铝dxgjx据分页
            Page<ClDxgjx> page = dxgjxService.paginate(pageNum, pageSz, mafactory, matRecheckNo, contractNo, contractName, material, type, status);
            renderJson(Result.success("镀锌钢绞线数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_dxgjx/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("镀锌钢绞线记录ID不能为空"));
            return;
        }

        try {
            ClDxgjx dxgjx = dxgjxService.findById(Long.parseLong(id));
            if (dxgjx != null ) {
                renderJson (Result.success ("镀锌钢绞线记录查询成功").putData ("record", dxgjx));
            } else {
                renderJson (Result.notFound ("镀锌钢绞线记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("镀锌钢绞线记录ID格式错误"));
        }
    }

    @ActionKey ("/cl_dxgjx/save")
    @HttpMethod ("POST")
    public void save (ClDxgjx dxgjx) {
        try {
            boolean success = dxgjxService.save (dxgjx);
            if (success) {
                renderJson (Result.success ("镀锌钢绞线记录保存成功").putData ("recordId", dxgjx.getId ()));
            } else {
                renderJson (Result.serverError ("镀锌钢绞线记录保存失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存镀锌钢绞线记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/cl_dxgjx/update")
    @HttpMethod ("PUT")
    public void update (ClDxgjx dxgjx) {
        try {
            boolean success = dxgjxService.update (dxgjx);
            if (success) {
                renderJson (Result.success ("镀锌钢绞线记录更新成功"));
            } else {
                renderJson (Result.serverError ("镀锌钢绞线记录更新失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新镀锌钢绞线记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_dxgjx/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("镀锌钢绞线记录ID不能为空"));
            return;
        }

        try {
            boolean success = dxgjxService.deleteById(Long.parseLong(id));
            if (success) {
                renderJson (Result.success ("镀锌钢绞线记录删除成功"));
            } else {
                renderJson (Result.notFound ("镀锌钢绞线记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("镀锌钢绞线记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除镀锌钢绞线记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_dxgjx/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("镀锌钢绞线记录ID列表不能为空"));
            return;
        }

        try {
            List<Long> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)  // 这里改为 Long
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("镀锌钢绞线记录ID列表不能为空"));
                return;
            }

            boolean success = dxgjxService.batchDelete(idList);
            if (success) {
                renderJson (Result.success ("批量删除镀锌钢绞线记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除镀锌钢绞线记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("镀锌钢绞线记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除镀锌钢绞线记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_dxgjx/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        String updatePerson = getPara("updatePerson");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = dxgjxService.updateStatus(id,status,updatePerson);
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