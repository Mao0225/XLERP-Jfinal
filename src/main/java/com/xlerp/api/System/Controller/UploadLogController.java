package com.xlerp.api.System.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.System.Service.UploadLogService;
import com.xlerp.common.model.SysUploadLog;

@Before(HttpMethodInterceptor.class)
public class UploadLogController extends Controller {
    private final UploadLogService service = new UploadLogService();

    @ActionKey("/uploadLog/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("page");
        String pageSize = getPara("size");
        String interfaceName = getPara("interfaceName");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<SysUploadLog> page = service.paginate(pageNum, pageSz,interfaceName);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/uploadLog/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("ID不能为空"));
            return;
        }

        try {
            SysUploadLog log = service.findById(Integer.parseInt(id));
            if (log != null) {
                renderJson(Result.success("查询成功").putData("log", log));
            } else {
                renderJson(Result.notFound("未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("ID格式错误"));
        }
    }

}