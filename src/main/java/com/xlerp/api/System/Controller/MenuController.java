package com.xlerp.api.System.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.System.Service.MenuService;
import com.xlerp.api.System.Service.UserService;
import com.xlerp.common.model.Sysmenu;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;


@Before(HttpMethodInterceptor.class)
public class MenuController extends Controller {
    private final MenuService menuService = new MenuService();
    private final UserService userService = new UserService();


    @ActionKey("/menu/getpage")
    @HttpMethod("GET")
    public void getpage() {
        try {
            int pageNumber = getParaToInt("pageNumber", 1);
            int pageSize = getParaToInt("pageSize", 10);

            if (pageNumber < 1 || pageSize < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Sysmenu> page = menuService.paginate(pageNumber, pageSize);
            renderJson(Result.success("查询菜单分页成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/menu/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("菜单ID不能为空"));
            return;
        }

        try {
            Sysmenu menu = menuService.findById(Integer.parseInt(id.trim()));
            if (menu != null) {
                renderJson(Result.success("查询菜单成功").putData("menu", menu));
            } else {
                renderJson(Result.notFound("菜单信息未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("菜单ID格式错误"));
        }
    }

    @ActionKey("/menu/save")
    @HttpMethod("POST")
    public void save(Sysmenu menu) {

        try {
            boolean success = menuService.save(menu);
            if (success) {
                renderJson(Result.success("菜单保存成功").putData("menuId", menu.getInt("id")));
            } else {
                renderJson(Result.serverError("保存菜单失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存菜单时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/menu/update")
    @HttpMethod("PUT")
    public void update(Sysmenu menu) {

        try {
            boolean success = menuService.update(menu);
            if (success) {
                renderJson(Result.success("菜单更新成功"));
            } else {
                renderJson(Result.serverError("更新菜单失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("菜单ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新菜单时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/menu/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("菜单ID不能为空"));
            return;
        }

        try {
            boolean success = menuService.deleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("菜单删除成功"));
            } else {
                renderJson(Result.notFound("菜单不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("菜单ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除菜单时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/menu/getMenuTree")
    @HttpMethod("GET")
    public void getMenuTree() {
        String userIdStr = getPara("userId");
        System.out.println("userIdStr: " + userIdStr);
        try {
            List<Record> menuTree;
            String successMsg;
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                System.out.println("获取所有菜单树");
                menuTree = menuService.getMenuTree(null);
                successMsg = "获取菜单树成功";
            } else {
                System.out.println("获取用户菜单树");
                int userId = Integer.parseInt(userIdStr.trim());
                if (userId < 1) {
                    renderJson(Result.badRequest("用户ID必须为正整数"));
                    return;
                }
                menuTree = menuService.getMenuTreeByUserId(userId);
                successMsg = "获取用户菜单树成功";
            }
            renderJson(Result.success(successMsg).putData("menuTree", menuTree));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("用户ID格式错误"));
        } catch (Exception e) {
            String errorMsg = userIdStr == null || userIdStr.trim().isEmpty() ?
                    "获取菜单树失败: " + e.getMessage() :
                    "获取用户菜单树失败: " + e.getMessage();
            renderJson(Result.serverError(errorMsg));
        }
    }
}