package com.xlerp.api.Common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.jfinal.kit.StrKit;

/**
 * Druid 过滤器，用于打印所有 SQL 语句到控制台，并将 INSERT、UPDATE、DELETE 语句记录到按月份组织的日志文件，包含时间戳
 *
 * @author Administrator
 */
public class SQLDruidFilter extends FilterAdapter {

    // 日志文件基础路径（可通过 JFinal 配置）
    private static final String LOG_FILE_BASE_PATH = "sqlLog";

    // 日志文件前缀
    private static final String LOG_FILE_PREFIX = "sql_log_";

    // 日志文件后缀
    private static final String LOG_FILE_SUFFIX = ".txt";

    // 正则表达式匹配 INSERT、UPDATE、DELETE 语句（忽略大小写）
    private static final Pattern DML_PATTERN = Pattern.compile("^(INSERT|UPDATE|DELETE)\\s", Pattern.CASE_INSENSITIVE);

    // 时间戳格式
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    // 文件名日期格式
    private static final SimpleDateFormat FILE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    // 月份目录格式
    private static final SimpleDateFormat MONTH_FORMAT = new SimpleDateFormat("yyyy-MM");

    // 静态初始化块，确保日志目录存在
    static {
        ensureLogDirectory();
    }

    /**
     * 确保日志目录存在
     */
    private static void ensureLogDirectory() {
        String monthDir = String.format("%s/%s", LOG_FILE_BASE_PATH, MONTH_FORMAT.format(new Date()));
        File dir = new File(monthDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        super.statement_close(chain, statement);

        String lSql = statement.getBatchSql();
        if (StrKit.notBlank(lSql)) {
            // 处理 SQL 参数
            Map<Integer, JdbcParameter> lParameters = statement.getParameters();
            String processedSql = processSqlParameters(lSql, lParameters);

            // 格式化日志条目，包含时间戳
            String logEntry = String.format("[%s] %s", DATE_FORMAT.format(new Date()), processedSql);

            // 打印所有 SQL 语句到控制台
            System.out.println("===: " + logEntry);

            // 仅将 INSERT、UPDATE、DELETE 语句写入文件
            if (isDmlStatement(lSql)) {
                writeToFile(logEntry + "\n");
            }
        }
    }

    /**
     * 检查 SQL 语句是否为 INSERT、UPDATE 或 DELETE
     */
    private boolean isDmlStatement(String sql) {
        return DML_PATTERN.matcher(sql.trim()).find();
    }

    /**
     * 处理 SQL 参数，替换占位符为实际值
     */
    private String processSqlParameters(String sql, Map<Integer, JdbcParameter> parameters) {
        String processedSql = sql;
        for (Map.Entry<Integer, JdbcParameter> lEntry : parameters.entrySet()) {
            JdbcParameter lValue = lEntry.getValue();
            if (lValue == null) {
                continue;
            }
            Object lO = lValue.getValue();
            if (lO == null) {
                continue;
            }
            String lS = lO.toString();
            // 处理特定 SQL 类型（如 VARCHAR、CHAR、DATE）
            switch (lValue.getSqlType()) {
                case 12: // VARCHAR
                case 1:  // CHAR
                case 91: // DATE
                    lS = "'" + lS + "'";
                    break;
                default:
                    // 其他类型无需额外格式化
                    break;
            }
            processedSql = processedSql.replaceFirst("\\?", lS);
        }
        return processedSql;
    }

    /**
     * 线程安全地将日志条目写入按日期命名的文件
     */
    private void writeToFile(String logEntry) {
        // 确保日志目录存在
        ensureLogDirectory();
        // 生成基于当前日期的文件路径
        String fileName = String.format("%s/%s/%s%s%s",
                LOG_FILE_BASE_PATH,
                MONTH_FORMAT.format(new Date()),
                LOG_FILE_PREFIX,
                FILE_DATE_FORMAT.format(new Date()),
                LOG_FILE_SUFFIX);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(logEntry);
        } catch (IOException e) {
            // 使用 System.err 或 JFinal 日志系统记录错误
            System.err.println("写入 SQL 日志到文件失败: " + e.getMessage());
        }
    }
}