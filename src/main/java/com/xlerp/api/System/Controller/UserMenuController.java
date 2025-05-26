package com.xlerp.api.System.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.Result;
import com.xlerp.api.System.Service.UserMenuService;
import com.xlerp.common.model.Sysusermenu;

import java.util.List;

/**
 * 用户菜单控制器
 */
@Before(HttpMethodInterceptor.class)
public class UserMenuController extends Controller {
    private final UserMenuService userMenuService = new UserMenuService();


    //根据用户id查询用户拥有的菜单id
    @ActionKey("/usermenu/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String userIdStr = getPara("userId");

        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            renderJson(Result.badRequest("用户ID不能为空"));
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr.trim());
            if (userId < 1) {
                renderJson(Result.badRequest("用户ID必须为正整数"));
                return;
            }

            List<Sysusermenu> list = userMenuService.findAll(userId);
            renderJson(Result.success("查询用户菜单成功").putData("list", list));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("用户ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("查询用户菜单失败: " + e.getMessage()));
        }
    }

    @ActionKey("/usermenu/add")
    @HttpMethod("POST")
    public void add(Sysusermenu usermenu) {
//        String userIdStr = getPara("userId");
//        String menuIdStr = getPara("menuId");

//        if (userIdStr == null || userIdStr.trim().isEmpty() ||
//                menuIdStr == null || menuIdStr.trim().isEmpty()) {
//            renderJson(Result.badRequest("用户ID和菜单ID不能为空"));
//            return;
//        }

        try {
//            int userId = Integer.parseInt(userIdStr.trim());
//            int menuId = Integer.parseInt(menuIdStr.trim());
              int userId = usermenu.getUserid();
              int menuId = usermenu.getMenuid();
//            if (userId < 1 || menuId < 1) {
//                renderJson(Result.badRequest("用户ID和菜单ID必须为正整数"));
//                return;
//            }

            boolean success = userMenuService.addUsermenu(userId, menuId);
            if (success) {
                renderJson(Result.success("添加用户菜单成功"));
            } else {
                renderJson(Result.serverError("添加用户菜单失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("用户ID或菜单ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("添加用户菜单时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/usermenu/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String userIdStr = getPara("userId");
        String menuIdStr = getPara("menuId");

        if (userIdStr == null || userIdStr.trim().isEmpty() ||
                menuIdStr == null || menuIdStr.trim().isEmpty()) {
            renderJson(Result.badRequest("用户ID和菜单ID不能为空"));
            return;
        }

        try {
            int userId = Integer.parseInt(userIdStr.trim());
            int menuId = Integer.parseInt(menuIdStr.trim());

            if (userId < 1 || menuId < 1) {
                renderJson(Result.badRequest("用户ID和菜单ID必须为正整数"));
                return;
            }

            boolean success = userMenuService.deleteUsermenu(userId, menuId);
            if (success) {
                renderJson(Result.success("删除用户菜单成功"));
            } else {
                renderJson(Result.notFound("用户菜单不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("用户ID或菜单ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除用户菜单时发生错误: " + e.getMessage()));
        }
    }
}