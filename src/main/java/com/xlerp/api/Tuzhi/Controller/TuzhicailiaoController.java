package com.xlerp.api.Tuzhi.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.Tuzhi.Service.TuzhicailiaoService;
import com.xlerp.common.model.Bastuzhicailiao;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

@Before(HttpMethodInterceptor.class)
public class TuzhicailiaoController extends Controller {
    private final TuzhicailiaoService tuzhicailiaoService = new TuzhicailiaoService();

    @ActionKey("/tuzhicailiao/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String tuzhiid = getPara("tuzhiid");
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = tuzhicailiaoService.paginate(pageNum, pageSz, tuzhiid);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/tuzhicailiao/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("材料ID不能为空"));
            return;
        }

        try {
            Bastuzhicailiao tuzhicailiao = tuzhicailiaoService.findById(Integer.parseInt(id));
            if (tuzhicailiao != null) {
                renderJson(Result.success("查询材料成功").putData("tuzhicailiao", tuzhicailiao));
            } else {
                renderJson(Result.notFound("材料未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("材料ID格式错误"));
        }
    }

    @ActionKey("/tuzhicailiao/save")
    @HttpMethod("POST")
    public void save(Bastuzhicailiao tuzhicailiao) {
        try {
            if (tuzhicailiao == null || tuzhicailiao.getTuzhiid() == null || tuzhicailiao.getBasitemid() == null ||
                    tuzhicailiao.getShuliang() == null || tuzhicailiao.getShuliang().trim().isEmpty()) {
                renderJson(Result.badRequest("材料信息不能为空且图纸ID、关联的basitem表ID和数量必须填写"));
                return;
            }
            boolean success = tuzhicailiaoService.save(tuzhicailiao);
            if (success) {
                renderJson(Result.success("材料保存成功").putData("tuzhicailiaoId", tuzhicailiao.getId()));
            } else {
                renderJson(Result.serverError("保存材料失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存材料时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/tuzhicailiao/update")
    @HttpMethod("PUT")
    public void update(Bastuzhicailiao tuzhicailiao) {
        try {
            if (tuzhicailiao == null || tuzhicailiao.getId() == null) {
                renderJson(Result.badRequest("材料ID不能为空"));
                return;
            }
            boolean success = tuzhicailiaoService.update(tuzhicailiao);
            if (success) {
                renderJson(Result.success("材料更新成功"));
            } else {
                renderJson(Result.serverError("更新材料失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("材料ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新材料时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/tuzhicailiao/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("材料ID不能为空"));
            return;
        }

        try {
            boolean success = tuzhicailiaoService.deleteById(Integer.parseInt(id));
            if (success) {
                renderJson(Result.success("材料删除成功"));
            } else {
                renderJson(Result.notFound("材料不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("材料ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除材料时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/tuzhicailiao/getoption")
    @HttpMethod("GET")
    public void getoption() {
        try {
            List<Record> options = tuzhicailiaoService.getOptions();
            renderJson(Result.success("查询选项成功").putData("options", options));
        } catch (Exception e) {
            renderJson(Result.serverError("查询选项时发生错误: " + e.getMessage()));
        }
    }
}