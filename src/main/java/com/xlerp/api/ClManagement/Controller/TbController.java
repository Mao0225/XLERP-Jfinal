package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.ClManagement.Service.TbService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClTb;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class TbController extends Controller {
    // 使用铝锭服务类
    private final TbService TbService = new TbService();

    /**
     * 分页查询铝锭数据
     */
    @ActionKey("/cl_tb/getpage")
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
            Page<ClTb> page = TbService.paginate(pageNum, pageSz, mafactory, matRecheckNo, contractNo, contractName, material, type, status);
            renderJson(Result.success("铜板数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_tb/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铜板记录ID不能为空"));
            return;
        }

        try {
            ClTb Tb = TbService.findById (Integer.parseInt (id));
            if (Tb != null ) {
                renderJson (Result.success ("铜板记录查询成功").putData ("record", Tb));
            } else {
                renderJson (Result.notFound ("铜板记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("铜板记录ID格式错误"));
        }
    }

    @ActionKey ("/cl_tb/save")
    @HttpMethod ("POST")
    public void save (ClTb Tb) {
        try {
            boolean success = TbService.save (Tb);
            if (success) {
                renderJson (Result.success ("铜板记录保存成功").putData ("recordId", Tb.getId ()));
            } else {
                renderJson (Result.serverError ("铜板记录保存失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存铜板记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/cl_tb/update")
    @HttpMethod ("PUT")
    public void update (ClTb Tb) {
        try {
            boolean success = TbService.update (Tb);
            if (success) {
                renderJson (Result.success ("铜板记录更新成功"));
            } else {
                renderJson (Result.serverError ("铜板记录更新失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新铜板记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_tb/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铜板记录ID不能为空"));
            return;
        }

        try {
            boolean success = TbService.deleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("铜板记录删除成功"));
            } else {
                renderJson (Result.notFound ("铜板记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("铜板记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除铜板记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_tb/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("铜板记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("铜板记录ID列表不能为空"));
                return;
            }

            boolean success = TbService.batchDelete(idList);
            if (success) {
                renderJson (Result.success ("批量删除铜板记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除铜板记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("铜板记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除铜板记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_tb/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        String updatePerson = getPara("updatePerson");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = TbService.updateStatus(id,status,updatePerson);
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

