package com.xlerp.api.Tuzhi.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.Tuzhi.Service.TuzhiService;
import com.xlerp.common.model.Bastuzhi;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Before(HttpMethodInterceptor.class)
public class TuzhiController extends Controller {
    private final TuzhiService tuzhiService = new TuzhiService();

    @ActionKey("/bastuzhi/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String tuzhimingcheng = getPara("tuzhimingcheng");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Bastuzhi> page = tuzhiService.paginate(pageNum, pageSz, tuzhimingcheng);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/bastuzhi/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("图纸ID不能为空"));
            return;
        }

        try {
            Bastuzhi tuzhi = tuzhiService.findById(Integer.parseInt(id));
            if (tuzhi != null) {
                renderJson(Result.success("查询图纸成功").putData("tuzhi", tuzhi));
            } else {
                renderJson(Result.notFound("图纸未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("图纸ID格式错误"));
        }
    }


    @ActionKey("/bastuzhi/save")
    @HttpMethod("POST")
    public void save(Bastuzhi bastuzhi) {
        System.out.println("===== 开始处理图纸保存请求 =====");

        // 打印接收到的参数对象
        System.out.println("接收到的Bastuzhi对象：" + bastuzhi);

        try {
            // 打印方法开始执行日志
            System.out.println("【步骤1】开始校验图纸参数");

            // 校验对象是否为空
            if (bastuzhi == null) {
                System.out.println("❌ 错误：接收到的图纸对象为null");
                renderJson(Result.badRequest("图纸信息不能为空且图纸名称必须填写"));
                System.out.println("===== 图纸保存请求处理结束（对象为空） =====");
                return;
            }

            // 打印对象属性详情
            System.out.println("图纸ID：" + (bastuzhi.getId() != null ? bastuzhi.getId() : "未设置"));
            System.out.println("图纸名称：" + (bastuzhi.getTuzhimingcheng() != null ? bastuzhi.getTuzhimingcheng() : "未设置"));
            System.out.println("图纸编号：" + (bastuzhi.getTuzhibianhao() != null ? bastuzhi.getTuzhibianhao() : "未设置"));

            // 校验图纸名称
            String tuzhiName = bastuzhi.getTuzhimingcheng();
            if (tuzhiName == null || tuzhiName.trim().isEmpty()) {
                System.out.println("❌ 错误：图纸名称为空或仅包含空格");
                renderJson(Result.badRequest("图纸信息不能为空且图纸名称必须填写"));
                System.out.println("===== 图纸保存请求处理结束（名称为空） =====");
                return;
            }
            System.out.println("✅ 图纸名称校验通过：" + tuzhiName);

            // 打印服务调用前的日志
            System.out.println("【步骤2】调用服务层保存图纸");
            boolean success = tuzhiService.save(bastuzhi);

            // 打印服务层执行结果
            if (success) {
                // 关键修正：确保保存成功后返回生成的ID
                Integer savedId = bastuzhi.getId();
                System.out.println("✅ 服务层保存成功，生成ID：" + savedId);

                if (savedId == null) {
                    System.out.println("⚠️ 警告：服务返回成功但ID为空，可能是实体未正确更新");
                }

                renderJson(Result.success("图纸保存成功").putData("tuzhiId", savedId));
                System.out.println("===== 图纸保存请求处理结束（成功） =====");
            } else {
                System.out.println("❌ 服务层保存失败，返回false");
                renderJson(Result.serverError("保存图纸失败"));
                System.out.println("===== 图纸保存请求处理结束（失败） =====");
            }

        } catch (Exception e) {
            // 异常处理日志
            System.out.println("【错误】图纸保存过程中发生异常");
            System.out.println("异常类型：" + e.getClass().getName());
            System.out.println("异常信息：" + e.getMessage());
            e.printStackTrace(); // 打印堆栈信息
            renderJson(Result.serverError("保存图纸时发生错误: " + e.getMessage()));
            System.out.println("===== 图纸保存请求处理结束（异常） =====");
        }
    }
    @ActionKey("/bastuzhi/update")
    @HttpMethod("PUT")
    public void update(Bastuzhi bastuzhi) {
        try {
            if (bastuzhi == null || bastuzhi.getId() == null) {
                renderJson(Result.badRequest("图纸ID不能为空"));
                return;
            }
            boolean success = tuzhiService.update(bastuzhi);
            if (success) {
                renderJson(Result.success("图纸更新成功"));
            } else {
                renderJson(Result.serverError("图纸部门失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("图纸ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新图纸时发生错误: " + e.getMessage()));
        }
    }
    // 保存文件到服务器
    private String saveFile(UploadFile uploadFile) {
        try {
            File file = uploadFile.getFile();
            String fileName = uploadFile.getOriginalFileName();
            String savePath = "/path/to/save/files/" + fileName; // 替换为实际的保存路径
            Path targetPath = Paths.get(savePath);
            Files.move(file.toPath(), targetPath);
            return savePath;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @ActionKey("/bastuzhi/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("图纸ID不能为空"));
            return;
        }

        try {
            boolean success = tuzhiService.deleteById(Integer.parseInt(id));
            if (success) {
                renderJson(Result.success("图纸删除成功"));
            } else {
                renderJson(Result.notFound("图纸不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("图纸ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除图纸时发生错误: " + e.getMessage()));
        }
    }
}