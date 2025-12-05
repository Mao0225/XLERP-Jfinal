package com.xlerp.api.BasProcessRoute.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.BasProcessRoute.Service.ProcessRouteService;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.common.model.BasProcessRoute;

import java.util.List;


@Before(HttpMethodInterceptor.class)
public class ProcessRouteController extends Controller {
    private final ProcessRouteService service = new ProcessRouteService();

    //增删改查
    @ActionKey("/processRoute/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("记录 ID 不能为空"));
            return;
        }
        try {
            BasProcessRoute processRoute = service.findById(Integer.parseInt(id));
            if (processRoute != null) {
                renderJson(Result.success("查询记录成功").putData("list", processRoute));
            } else {
                renderJson(Result.notFound("记录未找到或已被删除"));
            }
        }catch (NumberFormatException e){
            renderJson(Result.badRequest("记录 ID 格式错误"));
        }
    }

    //save
    @ActionKey("/processRoute/save")
    @HttpMethod("POST")
    public void save(BasProcessRoute processRoute) {
        try {
            boolean success = processRoute.save();
            if (success) {
                renderJson(Result.success("记录保存成功").putData("processRouteId", processRoute.getId()));
            }
            else {
                renderJson(Result.badRequest("记录保存失败"));
            }
        }catch (Exception e){
            renderJson(Result.serverError("服务器错误"));
        }
    }

    //update
    @ActionKey("/processRoute/update")
    @HttpMethod("PUT")
    public void update(BasProcessRoute processRoute) {
        try {
            boolean success = processRoute.update();
            if (success) {
                renderJson(Result.success("记录更新成功"));
            }
            else {
                renderJson(Result.badRequest("记录更新失败"));
            }
        }catch (Exception e){
            renderJson(Result.serverError("服务器错误"));
        }
    }

    //delete
    @ActionKey("/processRoute/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("记录 ID 不能为空"));
            return;
        }
        try {
            boolean success = service.deleteById(Integer.parseInt(id));
            if (success) {
                renderJson(Result.success("记录删除成功"));
            }
            else {
                renderJson(Result.badRequest("记录删除失败"));
            }
        }catch (NumberFormatException e){}
    }


//    通过itemId查询该产品的工艺路线
    @ActionKey("/processRoute/getByItemId")
    @HttpMethod("GET")
    public void getByItemId() {
        String itemId = getPara("itemId");
        if (itemId == null || itemId.trim().isEmpty()) {
            renderJson(Result.badRequest("产品 ID 不能为空"));
            return;
        }
        try {
            List<BasProcessRoute> processRoutes = service.getByItemId(Integer.parseInt(itemId));
            if (processRoutes != null) {
                renderJson(Result.success("查询记录成功").putData("list", processRoutes));
            }
        }catch (NumberFormatException e){
            renderJson(Result.badRequest("产品 ID 错误"));
        }
    }

}
