package com.xlerp.api.PlManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlManagement.Service.PlshengchandingdanService;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.Plshengchandingdan;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
//生产订单管理
public class PlshengchandingdanController extends Controller {
    private final PlshengchandingdanService plshengchandingdanService = new PlshengchandingdanService();

    @ActionKey("/plshengchandingdan/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String ipoNo = getPara("ipoNo");//生产订单号

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson (Result.badRequest ("页码或每页大小必须为正整数"));
                return;
            }

            Page page = plshengchandingdanService.paginate (pageNum, pageSz,ipoNo);
            renderJson (Result.success ("查询成功").putData ("page", page));
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/plshengchandingdan/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            Plshengchandingdan plshengchandingdan = plshengchandingdanService.findById (Integer.parseInt (id));
            if (plshengchandingdan != null && plshengchandingdan.getIsdelete () == 0) {
                renderJson (Result.success ("查询记录成功").putData ("plshengchandingdan", plshengchandingdan));
            } else {
                renderJson (Result.notFound ("记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        }
    }

    @ActionKey ("/plshengchandingdan/save")
    @HttpMethod ("POST")
    public void save (Plshengchandingdan plshengchandingdan) {
        try {
            plshengchandingdan.setIsdelete (0); // 设置为正常状态
            boolean success = plshengchandingdanService.save (plshengchandingdan);
            if (success) {
                renderJson (Result.success ("记录保存成功").putData ("recordId", plshengchandingdan.getId ()));
            } else {
                renderJson (Result.serverError ("保存记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/plshengchandingdan/update")
    @HttpMethod ("PUT")
    public void update (Plshengchandingdan plshengchandingdan) {
        try {
            boolean success = plshengchandingdanService.update (plshengchandingdan);
            if (success) {
                renderJson (Result.success ("记录更新成功"));
            } else {
                renderJson (Result.serverError ("更新记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/plshengchandingdan/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            boolean success = plshengchandingdanService.logicalDeleteById (Integer.parseInt (id.trim ()));
            if (success) {
                renderJson (Result.success ("记录删除成功"));
            } else {
                renderJson (Result.notFound ("记录不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("删除记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/plshengchandingdan/batchdelete")
    @HttpMethod("DELETE")
    public void batchDelete() {
        String ids = getPara("ids");

        if (ids == null || ids.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 列表不能为空"));
            return;
        }

        try {
            List<Integer> idList = Arrays.stream(ids.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            if (idList.isEmpty ()) {
                renderJson (Result.badRequest ("记录 ID 列表不能为空"));
                return;
            }

            boolean success = plshengchandingdanService.batchLogicalDelete (idList);
            if (success) {
                renderJson (Result.success ("批量删除记录成功"));
            } else {
                renderJson (Result.serverError ("批量删除记录失败"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        } catch (Exception e) {
            renderJson (Result.serverError ("批量删除记录时发生错误:" + e.getMessage ()));
        }
    }
}