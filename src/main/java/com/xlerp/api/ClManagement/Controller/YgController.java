package com.xlerp.api.ClManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.ClManagement.Service.YgService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.ClYg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class YgController extends Controller {
    // 使用圆钢服务类
    private final YgService ygService = new YgService();

    /**
     * 分页查询圆钢数据
     */
    @ActionKey("/cl_yg/getpage")
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

            // 查询圆钢数据分页
            Page<ClYg> page = ygService.paginate(pageNum, pageSz, mafactory, inNo, matMaterial, matRecheckNo);
            renderJson(Result.success("圆钢数据查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/cl_yg/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("圆钢记录ID不能为空"));
            return;
        }

        try {
            ClYg yg = ygService.findById (Integer.parseInt (id));
            if (yg != null ) {
                renderJson (Result.success ("圆钢记录查询成功").putData ("record", yg));
            } else {
                renderJson (Result.notFound ("圆钢记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("圆钢记录ID格式错误"));
        }
    }

    @ActionKey ("/cl_yg/save")
    @HttpMethod ("POST")
    public void save (ClYg yg) {
        try {
            boolean success = ygService.save (yg);
            if (success) {
                renderJson (Result.success ("圆钢记录保存成功").putData ("recordId", yg.getId ()));
            } else {
                renderJson (Result.serverError ("圆钢记录保存失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存圆钢记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/cl_yg/update")
    @HttpMethod ("PUT")
    public void update (ClYg yg) {
        try {
            boolean success = ygService.update (yg);
            if (success) {
                renderJson (Result.success ("圆钢记录更新成功"));
            } else {
                renderJson (Result.serverError ("圆钢记录更新失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新圆钢记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_yg/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("圆钢记录ID不能为空"));
            return;
        }

        try {
            boolean success = ygService.deleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("圆钢记录删除成功"));
            } else {
                renderJson (Result.notFound ("圆钢记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("圆钢记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除圆钢记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_yg/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("圆钢记录ID列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("圆钢记录ID列表不能为空"));
                return;
            }

            boolean success = ygService.batchDelete(idList);
            if (success) {
                renderJson (Result.success ("批量删除圆钢记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除圆钢记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("圆钢记录ID格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除圆钢记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/cl_yg/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        String updatePerson = getPara("updatePerson");
        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
        }
        try {
            boolean success = ygService.updateStatus(id,status,updatePerson);
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
