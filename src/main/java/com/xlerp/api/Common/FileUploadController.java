package com.xlerp.api.Common;

import com.jfinal.core.Controller;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.Common.Result;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 * 提供多种文件上传接口，返回带前导斜杠的文件路径
 */
public class FileUploadController extends Controller {

    /**
     * 上传文件到指定文件夹
     * 使用方式: POST /api/upload/file
     * 参数:
     *   - file: 文件
     *   - folder: 文件夹名称
     */
    public void file() {
        try {
            // 获取上传的文件
            UploadFile uploadFile = getFile("file");
            if (uploadFile == null) {
                renderJson(Result.badRequest("未找到上传文件"));
                return;
            }

            // 获取文件夹参数
            String folder = getPara("folder", "common");

            // 获取上传的文件对象和原始文件名
            File file = uploadFile.getFile();
            String originalFileName = uploadFile.getOriginalFileName();

            // 上传文件并获取文件路径
            String filePath = FileUploadUtils.uploadFile(file, originalFileName, folder);

            if (filePath != null) {
                // 确保路径以斜杠开头
                if (!filePath.startsWith("/")) {
                    filePath = "/" + filePath;
                }

                // 返回成功结果和文件路径
                Map<String, Object> data = new HashMap<>();
                data.put("url", filePath);
                data.put("originalFileName", originalFileName);
                renderJson(Result.success("文件上传成功", data));
            } else {
                renderJson(Result.serverError("文件上传失败"));
            }

            // 删除临时文件
            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("文件上传异常: " + e.getMessage()));
        }
    }

    /**
     * 上传文件到自定义路径
     * 使用方式: POST /api/upload/custom
     * 参数:
     *   - file: 文件
     *   - path: 自定义路径
     */
    public void custom() {
        try {
            // 获取上传的文件
            UploadFile uploadFile = getFile("file");
            if (uploadFile == null) {
                renderJson(Result.badRequest("未找到上传文件"));
                return;
            }

            // 获取自定义路径参数
            String customPath = getPara("path", "custom");

            // 获取上传的文件对象和原始文件名
            File file = uploadFile.getFile();
            String originalFileName = uploadFile.getOriginalFileName();

            // 上传文件到自定义路径
            String filePath = FileUploadUtils.uploadToCustomPath(file, originalFileName, customPath);

            if (filePath != null) {
                // 确保路径以斜杠开头
                if (!filePath.startsWith("/")) {
                    filePath = "/" + filePath;
                }

                // 返回成功结果和文件路径
                Map<String, Object> data = new HashMap<>();
                data.put("url", filePath);
                data.put("originalFileName", originalFileName);
                renderJson(Result.success("文件上传成功", data));
            } else {
                renderJson(Result.serverError("文件上传失败"));
            }

            // 删除临时文件
            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("文件上传异常: " + e.getMessage()));
        }
    }

    /**
     * 上传头像文件（专用接口）
     * 使用方式: POST /api/upload/avatar
     * 参数:
     *   - file: 文件
     */
    public void avatar() {
        try {
            // 获取上传的文件
            UploadFile uploadFile = getFile("file");
            if (uploadFile == null) {
                renderJson(Result.badRequest("未找到上传文件"));
                return;
            }

            // 固定使用avatars文件夹
            String folder = "avatars";

            // 获取上传的文件对象和原始文件名
            File file = uploadFile.getFile();
            String originalFileName = uploadFile.getOriginalFileName();

            // 上传文件并获取文件路径
            String filePath = FileUploadUtils.uploadFile(file, originalFileName, folder);

            if (filePath != null) {
                // 确保路径以斜杠开头
                if (!filePath.startsWith("/")) {
                    filePath = "/" + filePath;
                }

                // 返回成功结果和文件路径
                Map<String, Object> data = new HashMap<>();
                data.put("url", filePath);
                data.put("originalFileName", originalFileName);
                renderJson(Result.success("头像上传成功", data));
            } else {
                renderJson(Result.serverError("头像上传失败"));
            }

            // 删除临时文件
            if (file.exists()) {
                file.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("头像上传异常: " + e.getMessage()));
        }
    }

    /**
     * 上传多个文件
     * 使用方式: POST /api/upload/multiple
     * 参数:
     *   - files: 多个文件
     *   - folder: 文件夹名称
     */
    public void multiple() {
        try {
            // 获取上传的多个文件
            java.util.List<UploadFile> uploadFiles = getFiles("files");
            if (uploadFiles == null || uploadFiles.isEmpty()) {
                renderJson(Result.badRequest("未找到上传文件"));
                return;
            }

            // 获取文件夹参数
            String folder = getPara("folder", "common");

            // 存储所有上传成功的文件路径
            java.util.List<Map<String, String>> fileList = new java.util.ArrayList<>();

            // 逐个处理文件
            for (UploadFile uploadFile : uploadFiles) {
                File file = uploadFile.getFile();
                String originalFileName = uploadFile.getOriginalFileName();

                // 上传文件
                String filePath = FileUploadUtils.uploadFile(file, originalFileName, folder);

                if (filePath != null) {
                    // 确保路径以斜杠开头
                    if (!filePath.startsWith("/")) {
                        filePath = "/" + filePath;
                    }

                    Map<String, String> fileInfo = new HashMap<>();
                    fileInfo.put("url", filePath);
                    fileInfo.put("originalFileName", originalFileName);
                    fileList.add(fileInfo);
                }

                // 删除临时文件
                if (file.exists()) {
                    file.delete();
                }
            }

            // 返回上传结果
            Map<String, Object> data = new HashMap<>();
            data.put("files", fileList);
            data.put("totalCount", fileList.size());
            renderJson(Result.success("文件上传成功", data));

        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("文件上传异常: " + e.getMessage()));
        }
    }
}