package com.xlerp.api.System.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.System.Service.LoginService;
import com.xlerp.common.model.Sysuser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
// * 登录控制器，处理用户登录请求并生成 JWT Token
 *
 * @author Administrator
 */
@Before(HttpMethodInterceptor.class)

public class LoginController extends Controller {
    private final LoginService loginService = new LoginService();
    // JWT 密钥，建议从配置文件加载
    private static final String SECRET_KEY = "xlqc1234"; // 请替换为安全的密钥
    // Token 有效期，1小时（单位：毫秒）
    private static final long EXPIRATION_TIME = 3600_000;

    /**
     * 用户登录接口，验证用户名和密码，生成并返回 JWT Token
     */
    @ActionKey("/login/login")
    @HttpMethod("GET")
    public void login() {
        String username = getPara("username");
        String password = getPara("password");

        // 校验用户名和密码是否为空
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            renderJson(Result.badRequest("用户名和密码不能为空"));
            return;
        }

        try {
            // 调用服务层进行用户认证
            Sysuser user = loginService.authenticate(username, password);
            if (user == null) {
                renderJson(Result.badRequest("用户名或密码错误"));
                return;
            }

            // 构造 JWT Token 的 Claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getInt("id")); // 假设 Sysuser 的 id 是 String 类型
            claims.put("username", user.getStr("username"));
            claims.put("avatar", user.getStr("avatar"));
            claims.put("descr", user.getStr("descr"));

            // 生成 JWT Token
            String token = Jwts.builder()
                    .setClaims(claims)
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                    .compact();

            // 构造返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);

            // 返回成功响应
            renderJson(Result.success("登录成功", data));

        } catch (Exception e) {
            // 捕获异常并返回服务器错误
            renderJson(Result.serverError("登录时发生错误: " + e.getMessage()));
        }
    }
}