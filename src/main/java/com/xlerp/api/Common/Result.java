package com.xlerp.api.Common;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应类，用于返回 API 调用结果，并将消息记录到控制台和按日期命名的日志文件
 *
 * @author Administrator
 */
public class Result {
    // 定义允许的状态码
    public static final int CODE_SUCCESS = 200;
    public static final int CODE_BAD_REQUEST = 400;
    public static final int CODE_NOT_FOUND = 404;
    public static final int CODE_SERVER_ERROR = 500;
    public static final int CODE_METHOD_NOT_ALLOWED = 405;

    // 日志文件基础路径（可通过 JFinal 配置）
    private static final String LOG_FILE_BASE_PATH = "result_log";

    // 日志文件后缀
    private static final String LOG_FILE_SUFFIX = ".txt";

    // 时间戳格式
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // 文件名日期格式
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    private final int code;
    private final String msg;
    private final Map<String, Object> data;

    // 私有构造函数，强制使用工厂方法
    private Result(int code, String msg, Map<String, Object> data) {
        validateCode(code);
        this.code = code;
        this.msg = msg;
        this.data = data != null ? data : new HashMap<>();
        // 记录消息到控制台和日志文件
        logMessage(msg);
    }

    // 校验状态码
    private void validateCode(int code) {
        if (code != CODE_SUCCESS && code != CODE_BAD_REQUEST &&
                code != CODE_NOT_FOUND && code != CODE_SERVER_ERROR &&
                code != CODE_METHOD_NOT_ALLOWED) {
            throw new IllegalArgumentException("无效的状态码: " + code + "，仅支持 200, 400, 404, 500, 405");
        }
    }

    // 记录消息到控制台和日志文件
    private void logMessage(String msg) {
        // 格式化日志条目，包含时间戳
        String logEntry = String.format("[%s] %s%n", DATE_FORMAT.format(new Date()), msg);

        // 打印到控制台
        System.out.println("===: " + logEntry.trim());

        // 写入按日期命名的文件
        String fileName = String.format("%s_%s%s", LOG_FILE_BASE_PATH, FILE_DATE_FORMAT.format(new Date()), LOG_FILE_SUFFIX);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(logEntry);
        } catch (IOException e) {
            // 使用 System.err 或 JFinal 日志系统记录错误
            System.err.println("写入 Result 日志到文件失败: " + e.getMessage());
        }
    }

    // 静态工厂方法：成功响应
    public static Result success() {
        return new Result(CODE_SUCCESS, "成功", null);
    }

    public static Result success(String msg) {
        return new Result(CODE_SUCCESS, msg, null);
    }

    public static Result success(Map<String, Object> data) {
        return new Result(CODE_SUCCESS, "成功", data);
    }

    public static Result success(String msg, Map<String, Object> data) {
        return new Result(CODE_SUCCESS, msg, data);
    }

    // 静态工厂方法：错误响应
    public static Result badRequest(String msg) {
        return new Result(CODE_BAD_REQUEST, msg, null);
    }

    public static Result badRequest(String msg, Map<String, Object> data) {
        return new Result(CODE_BAD_REQUEST, msg, data);
    }

    public static Result notFound(String msg) {
        return new Result(CODE_NOT_FOUND, msg, null);
    }

    public static Result notFound(String msg, Map<String, Object> data) {
        return new Result(CODE_NOT_FOUND, msg, data);
    }

    public static Result serverError(String msg) {
        return new Result(CODE_SERVER_ERROR, msg, null);
    }

    public static Result serverError(String msg, Map<String, Object> data) {
        return new Result(CODE_SERVER_ERROR, msg, data);
    }

    public static Result methodNotAllowed(String msg) {
        return new Result(CODE_METHOD_NOT_ALLOWED, msg, null);
    }

    public static Result methodNotAllowed(String msg, Map<String, Object> data) {
        return new Result(CODE_METHOD_NOT_ALLOWED, msg, data);
    }

    // 流式添加数据
    public Result putData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    // 批量添加数据
    public Result putAllData(Map<String, Object> data) {
        this.data.putAll(data);
        return this;
    }

    // Getter 方法
    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Map<String, Object> getData() {
        return new HashMap<>(data); // 返回防御性副本
    }

    public boolean isSuccess() {
        return code == CODE_SUCCESS;
    }

    // 便于调试的 toString 方法
    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}