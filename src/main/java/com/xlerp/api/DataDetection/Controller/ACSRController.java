package com.xlerp.api.DataDetection.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.Common.FileUploadUtils;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.DataDetection.Service.ACSRService;
import com.xlerp.common.model.ACSR;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


@Before(HttpMethodInterceptor.class)
public class ACSRController extends Controller {

    private final ACSRService acsrService = new ACSRService();

    @ActionKey("/acsr/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String RawmaterialManufacturer = getPara("RawmaterialManufacturer");
        String Size = getPara("Size");
        String IncomingNo = getPara("IncomingNo");

        String SinglefilamentStrength = getPara("SinglefilamentStrength");
        String Factorydata = getPara("Factorydata");
        String Incomingdata = getPara("Incomingdata");
        String QualityCertificate = getPara("QualityCertificate");
        String gridno = getPara("gridno");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = acsrService.paginate(pageNum, pageSz,  RawmaterialManufacturer, Size, IncomingNo, SinglefilamentStrength, Factorydata, Incomingdata, QualityCertificate, gridno);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/acsr/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("数据ID不能为空"));
            return;
        }

        try {
            ACSR acsr = acsrService.findById(Integer.parseInt(id));
            if (acsr != null) {
                renderJson(Result.success("查询数据成功").putData("acsr", acsr));
            } else {
                renderJson(Result.notFound("数据未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数据ID格式错误"));
        }
    }

    @ActionKey("/acsr/save")
    @HttpMethod("POST")
    public void save(ACSR acsr) {
        // 校验必填字段

        try {
            boolean success = acsrService.save(acsr);
            if (success) {
                renderJson(Result.success("数据保存成功").putData("acsrId", acsr.getId()));
            } else {
                renderJson(Result.serverError("保存数据失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存数据时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/acsr/update")
    @HttpMethod("PUT")
    public void update(ACSR acsr) {

        try {

            boolean success = acsrService.update(acsr);
            if (success) {
                renderJson(Result.success("数据更新成功"));
            } else {
                renderJson(Result.serverError("更新数据失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数据ID或数值格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新数据时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/acsr/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("数据ID不能为空"));
            return;
        }

        try {
            boolean success = acsrService.deleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("数据删除成功"));
            } else {
                renderJson(Result.notFound("数据不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数据ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除数据时发生错误: " + e.getMessage()));
        }
    }

    /**
     * 上传文件到自定义路径
     * 使用方式: POST /fastener/upload/custom
     * 参数:
     *   - file: 文件
     *   - path: 自定义路径
     */
    @ActionKey("/acsr/upload/custom")
    @HttpMethod("POST")
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

    @ActionKey("/acsr/getGridNoList")
    @HttpMethod("GET")
    public void getGridNoList() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
//        String gridNo = getPara("gridNo");
        String gridno = getPara("gridno");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 调用服务层方法获取合同号列表
            Page page = acsrService.getGridNoList(pageNum, pageSz, gridno);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }




}
