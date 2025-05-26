package com.xlerp.api.System.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.System.Service.TermService;
import com.xlerp.common.model.Systerm;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

@Before(HttpMethodInterceptor.class)
public class TermController extends Controller {
    private final TermService termService = new TermService();

    @ActionKey("/term/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("page");
        String pageSize = getPara("size");
        String term = getPara("term");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Systerm> page = termService.paginate(pageNum, pageSz, term);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


    @ActionKey("/term/getall")
    @HttpMethod("GET")
    public void getAll() {
        try {

            List<Systerm> list = termService.getAll();
            renderJson(Result.success("查询成功").putData("list", list));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/term/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("期间ID不能为空"));
            return;
        }

        try {
            Systerm term = termService.findById(Integer.parseInt(id));
            if (term != null) {
                renderJson(Result.success("查询期间成功").putData("term", term));
            } else {
                renderJson(Result.notFound("期间未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("期间ID格式错误"));
        }
    }

    @ActionKey("/term/save")
    @HttpMethod("POST")
    public void save(Systerm systerm) {
        try {
            if (systerm == null || systerm.getTerm() == null || systerm.getTerm().trim().isEmpty()) {
                renderJson(Result.badRequest("期间信息不能为空且期间名称必须填写"));
                return;
            }
            boolean success = termService.save(systerm);
            if (success) {
                renderJson(Result.success("期间保存成功").putData("termId", systerm.getId()));
            } else {
                renderJson(Result.serverError("保存期间失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存期间时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/term/update")
    @HttpMethod("PUT")
    public void update(Systerm systerm) {
        try {
            boolean success = termService.update(systerm);
            if (success) {
                renderJson(Result.success("期间更新成功"));
            } else {
                renderJson(Result.serverError("更新期间失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("期间ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新期间时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/term/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("期间ID不能为空"));
            return;
        }

        try {
            boolean success = termService.deleteById(Integer.parseInt(id));
            if (success) {
                renderJson(Result.success("期间删除成功"));
            } else {
                renderJson(Result.notFound("期间不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("期间ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除期间时发生错误: " + e.getMessage()));
        }
    }
}