package com.xlerp.api.Tongzhi.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.Tongzhi.Service.TongzhiService;
import com.xlerp.common.model.Bascontractitem;
import com.xlerp.common.model.Bastuzhi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Before(HttpMethodInterceptor.class)
public class TongzhiController extends Controller {
    private final TongzhiService tongzhiService = new TongzhiService();

    @ActionKey("/tongzhi/getpage")
    @HttpMethod("GET")
    public void getpage() {
        //制定通知之前，获取活通列表
        //测试接口：http://localhost:8099/tongzhi/getpage
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String term = getPara("term");
        String contractNo = getPara("contractNo");
        String projectName = getPara("projectName");
        String salesmanNo = getPara("salesmanNo");
        String rule = getPara("rule");
        String owenr = getPara("owenr");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = tongzhiService.getContractList(pageNum, pageSz, term, contractNo, projectName, salesmanNo, rule,owenr);
            renderJson(Result.success("查询合同列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
    @ActionKey("/tongzhi/gettuzhilist")
    @HttpMethod("GET")
    public void gettuzhilist() {
        //获取图纸列表，这个是指定通知功能，获取图纸列表的接口，刘国奇
        //测试接口 ：http://localhost:8099/tongzhi/gettuzhilist
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String tuzhibianhao = getPara("tuzhibianhao");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Bastuzhi> page = tongzhiService.gettuzhilist(pageNum, pageSz, tuzhibianhao);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/tongzhi/updateitem")
    @HttpMethod("PUT")
    //修改单条通知信息
    public void updateitem(Bascontractitem bascontractitem) {
        try {
            boolean success = tongzhiService.updateitem(bascontractitem);
            if (success) {
                renderJson(Result.success("物料更新成功"));
            } else {
                renderJson(Result.serverError("更新物料失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新物料时发生错误: " + e.getMessage()));
        }
    }
}