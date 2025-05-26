package com.xlerp.api.System.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.Common.FileUploadUtils;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.System.Service.UserService;
import com.xlerp.common.model.Sysuser;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

@Before(HttpMethodInterceptor.class)
public class UserController extends Controller {
    private final UserService userService = new UserService();

    @ActionKey("/user/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String username = getPara("username");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = userService.paginate(pageNum, pageSz, username);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/user/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("用户ID不能为空"));
            return;
        }

        try {
            Sysuser user = userService.findById(Integer.parseInt(id));
            if (user != null) {
                renderJson(Result.success("查询用户成功").putData("user", user));
            } else {
                renderJson(Result.notFound("用户未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("用户ID格式错误"));
        }
    }

    @ActionKey("/user/save")
    @HttpMethod("POST")
    public void save(Sysuser sysuser) {
        try {

            if (sysuser == null || sysuser.getUsername() == null || sysuser.getUsername().trim().isEmpty()) {
                renderJson(Result.badRequest("用户信息不能为空且用户名必须填写"));
                return;
            }
            // 保存用户信息
            boolean success = userService.save(sysuser);
            if (success) {
                renderJson(Result.success("用户保存成功").putData("userId", sysuser.getId()));
            } else {
                renderJson(Result.serverError("保存用户失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存用户时发生错误: " + e.getMessage()));
        }
    }


    @ActionKey("/user/update")
    @HttpMethod("PUT")
    public void update(Sysuser user) {

        try {
            boolean success = userService.update(user);
            if (success) {
                renderJson(Result.success("用户更新成功"));
            } else {
                renderJson(Result.serverError("更新用户失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("用户ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新用户时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/user/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("用户ID不能为空"));
            return;
        }

        try {
            boolean success = userService.deleteById(Integer.parseInt(id));
            if (success) {
                renderJson(Result.success("用户删除成功"));
            } else {
                renderJson(Result.notFound("用户不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("用户ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除用户时发生错误: " + e.getMessage()));
        }
    }
    @ActionKey("/user/changePassword")
    @HttpMethod("GET")
    public void changePassword() {
        try {
            // 获取请求体中的参数
//            Integer id = getParaToInt("id");
            String userIdStr = getPara("userId");
            String oldPassword = getPara("oldPassword");
            String newPassword = getPara("newPassword");
            System.out.println(userIdStr);
            System.out.println("oldPassword: " + oldPassword);
            System.out.println("newPassword: " + newPassword);
            // 参数校验
            if (userIdStr == null || userIdStr.trim().isEmpty()) {
                renderJson(Result.badRequest("用户ID不能为空"));
                return;
            }
            if (oldPassword == null || oldPassword.trim().isEmpty()) {
                renderJson(Result.badRequest("旧密码不能为空"));
                return;
            }
            if (newPassword == null || newPassword.trim().isEmpty()) {
                renderJson(Result.badRequest("新密码不能为空"));
                return;
            }
            if (newPassword.length() < 6) {
                renderJson(Result.badRequest("新密码长度必须至少6位"));
                return;
            }

            // 转换为整数 userId
            int userId;
            try {
                userId = Integer.parseInt(userIdStr);
            } catch (NumberFormatException e) {
                renderJson(Result.badRequest("用户ID格式错误"));
                return;
            }

            // 调用服务层处理密码更改
            boolean success = userService.changePassword(userId, oldPassword, newPassword);
            if (success) {
                renderJson(Result.success("密码修改成功"));
            } else {
                renderJson(Result.badRequest("旧密码错误或用户不存在"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("修改密码时发生错误: " + e.getMessage()));
        }
    }

}