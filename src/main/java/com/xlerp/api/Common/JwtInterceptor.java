package com.xlerp.api.Common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;

/**
 * JWT 认证拦截器，用于验证请求中的 Token 并提取用户信息
 *
 * @author Administrator
 */
public class JwtInterceptor implements Interceptor {

    // JWT 密钥，建议从配置文件加载
    private static final String SECRET_KEY = "xlqc1234"; // 请替换为安全的密钥

    /**
     * 拦截方法，验证请求头中的 Authorization Token
     *
     * @param inv 调用对象
     */
    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        String actionKey = inv.getActionKey();
        System.out.println("actionKey: " + actionKey);
        // 跳过 /login 路由的 Token 验证
        if ("/login/login".equals(actionKey)) {
            inv.invoke();
            return;
        }

        String authHeader = controller.getHeader("Authorization");
        System.out.println("authHeader: " + authHeader);
        // 检查 Authorization 头是否存在且格式正确
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            controller.renderJson(Result.badRequest("缺少或无效的 Authorization 头"));
            return;
        }

        // 提取 Token
        String token = authHeader.substring(7);

        try {
            // 解析 JWT Token
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody();

            // 提取用户信息并存储到控制器
            controller.setAttr("userId", claims.get("id", Integer.class));
            controller.setAttr("username", claims.get("username", String.class));
            controller.setAttr("avatar", claims.get("avatar", String.class));
            controller.setAttr("descr", claims.get("descr", String.class));

            // 继续执行后续逻辑
            inv.invoke();

        } catch (ExpiredJwtException e) {
            // Token 过期
            controller.renderJson(Result.badRequest("Token 已过期"));
        } catch (SignatureException e) {
            // Token 签名无效
            controller.renderJson(Result.badRequest("无效的 Token"));
        } catch (Exception e) {
            // 其他 Token 解析错误
            controller.renderJson(Result.badRequest("Token 解析失败: " + e.getMessage()));
        }
    }
}