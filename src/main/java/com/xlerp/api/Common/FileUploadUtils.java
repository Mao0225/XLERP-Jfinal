package com.xlerp.api.Common;

import com.jfinal.kit.PathKit;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传工具类
 * 提供多种上传文件的方法，更简洁和灵活
 */
public class FileUploadUtils {

    // 文件存储的基础路径，可以通过配置文件设置
    private static final String BASE_UPLOAD_PATH = "upload";

    /**
     * 上传文件到指定目录
     *
     * @param file 文件对象
     * @param folderName 存储文件夹名称（如 images, documents）
     * @return 文件相对路径
     */
    public static String uploadFile(File file, String folderName) {
        return uploadFile(file, file.getName(), folderName);
    }

    /**
     * 上传文件到指定目录（指定文件名）
     *
     * @param file 文件对象
     * @param originalFileName 原始文件名
     * @param folderName 存储文件夹名称
     * @return 文件相对路径
     */
    public static String uploadFile(File file, String originalFileName, String folderName) {
        if (file == null || !file.exists()) {
            return null;
        }

        try {
            // 按日期生成子目录
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());

            // 构建存储路径
            String uploadPath = BASE_UPLOAD_PATH + "/" + folderName + "/" + datePath;

            // 获取完整的物理存储路径
            String rootPath = getRootPath();
            Path dirPath = Paths.get(rootPath, uploadPath);

            // 确保目录存在
            Files.createDirectories(dirPath);

            // 生成唯一文件名
            String newFileName = generateUniqueFileName(originalFileName);

            // 目标文件路径
            Path targetPath = dirPath.resolve(newFileName);

            // 复制文件
            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 返回相对路径（可用于访问文件）
            return uploadPath + "/" + newFileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 上传文件到完全自定义的路径
     *
     * @param file 文件对象
     * @param originalFileName 原始文件名
     * @param customPath 完全自定义的路径
     * @return 文件相对路径
     */
    public static String uploadToCustomPath(File file, String originalFileName, String customPath) {
        if (file == null || !file.exists()) {
            return null;
        }

        try {
            // 获取根路径
            String rootPath = getRootPath();

            // 构建完整路径
            Path dirPath = Paths.get(rootPath, customPath);

            // 确保目录存在
            Files.createDirectories(dirPath);

            // 生成唯一文件名
            String newFileName = generateUniqueFileName(originalFileName);

            // 目标文件路径
            Path targetPath = dirPath.resolve(newFileName);

            // 复制文件
            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 返回相对路径
            return customPath + "/" + newFileName;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取Web应用根路径
     *
     * @return 根路径
     */
    private static String getRootPath() {
        // 使用JFinal的PathKit获取Web根路径
        return PathKit.getWebRootPath();
    }

    /**
     * 生成唯一文件名，避免文件名冲突
     *
     * @param originalFileName 原始文件名
     * @return 唯一文件名
     */
    private static String generateUniqueFileName(String originalFileName) {
        // 获取文件扩展名
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }

        // 使用UUID生成唯一文件名
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }
}