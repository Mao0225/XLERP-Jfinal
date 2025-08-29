package com.xlerp.api.System.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.System.Service.BasNoService;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.Basno;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Before(HttpMethodInterceptor.class)
public class BasNoController extends Controller {
    private final BasNoService BasnoService = new BasNoService();

    @ActionKey("/Basno/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String basname = getPara("basname");
        String memo = getPara("memo");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson (Result.badRequest ("页码或每页大小必须为正整数"));
                return;
            }

            Page page = BasnoService.paginate (pageNum, pageSz, basname, memo);
            renderJson (Result.success ("查询成功").putData ("page", page));
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/Basno/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            Basno Basno = BasnoService.findById (Integer.parseInt (id));
            if (Basno != null) {
                renderJson (Result.success ("查询记录成功").putData ("Basno", Basno));
            } else {
                renderJson (Result.notFound ("记录未找到或已被删除"));
            }
        } catch (NumberFormatException e) {
            renderJson (Result.badRequest ("记录 ID 格式错误"));
        }
    }

    @ActionKey("/Basno/getNewNoNyName")
    @HttpMethod("GET") //
    public void getNewNoNyName() {
        String basname = getPara("basname");

        if (basname == null || basname.trim().isEmpty()) {
            renderJson(Result.badRequest("编号简称不能为空"));
            return;
        }

        try {
            String fullNoNyName = BasnoService.getNewNoNyName(basname);
            if (fullNoNyName != null) {
                renderJson(Result.success("查询记录成功").putData("fullNoNyName", fullNoNyName));
            } else {
                renderJson(Result.badRequest("未找到对应的编号记录"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("获取编号失败，请稍后重试"));
        }
    }

    @ActionKey ("/Basno/save")
    @HttpMethod ("POST")
    public void save (Basno Basno) {
        try {
            boolean success = BasnoService.save (Basno);
            if (success) {
                renderJson (Result.success ("记录保存成功").putData ("recordId", Basno.getId ()));
            } else {
                renderJson (Result.serverError ("保存记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("保存记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey ("/Basno/update")
    @HttpMethod ("PUT")
    public void update (Basno Basno) {
        try {
            boolean success = BasnoService.update (Basno);
            if (success) {
                renderJson (Result.success ("记录更新成功"));
            } else {
                renderJson (Result.serverError ("更新记录失败"));
            }
        } catch (Exception e) {
            renderJson (Result.serverError ("更新记录时发生错误:" + e.getMessage ()));
        }
    }

    @ActionKey("/Basno/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim ().isEmpty ()) {
            renderJson (Result.badRequest ("记录 ID 不能为空"));
            return;
        }

        try {
            boolean success = BasnoService.DeleteById (Integer.parseInt (id.trim ()));
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

}