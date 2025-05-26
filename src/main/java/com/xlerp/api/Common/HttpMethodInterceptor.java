package com.xlerp.api.Common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.Result;

public class HttpMethodInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        String requestMethod = controller.getRequest().getMethod().toUpperCase();

        // 记录拦截开始，包含控制器和操作信息
        System.out.println("HttpMethodInterceptor: 开始拦截，控制器 [" +
                controller.getClass().getSimpleName() + "]，操作 [" + inv.getActionKey() + "]");

        // 记录接收到的HTTP请求方法
        System.out.println("HttpMethodInterceptor: 接收到的HTTP请求方法: " + requestMethod);

        // 获取方法上的HttpMethod注解
        HttpMethod httpMethod = inv.getMethod().getAnnotation(HttpMethod.class);
        if (httpMethod != null) {
            String[] allowedMethods = httpMethod.value();

            // 记录注解中允许的HTTP方法
            System.out.println("HttpMethodInterceptor: 找到HttpMethod注解，允许的方法: " +
                    String.join(", ", allowedMethods));

            for (String method : allowedMethods) {
                if (method.toUpperCase().equals(requestMethod)) {
                    // 记录请求方法被允许
                    System.out.println("HttpMethodInterceptor: 请求方法 [" + requestMethod +
                            "] 被允许，继续执行调用。");
                    inv.invoke();
                    return;
                }
            }
            // 记录请求方法不被允许
            System.out.println("HttpMethodInterceptor: 请求方法 [" + requestMethod +
                    "] 不被允许，返回方法不支持的响应。");
            controller.renderJson(Result.methodNotAllowed("不支持的请求方法: " + requestMethod));
        } else {
            // 记录未找到HttpMethod注解的情况
            System.out.println("HttpMethodInterceptor: 未找到HttpMethod注解，允许请求继续执行。");
            inv.invoke();
        }

        // 记录拦截结束
        System.out.println("HttpMethodInterceptor: 拦截完成，操作 [" + inv.getActionKey() + "]");
    }
}