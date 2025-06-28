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

            Page<Record> page = tongzhiService.getContractList(pageNum, pageSz, term, contractNo, projectName, salesmanNo, rule, owenr);
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

    //按通知生产提料单开始
    @ActionKey("/tongzhi/gettongzhipage")
    @HttpMethod("GET")
    public void gettongzhipage() {
        // 获取参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String noticeid = getPara("noticeid");
        String noticename = getPara("noticename");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = tongzhiService.gettongzhipage(pageNum, pageSz, noticeid, noticename);
            renderJson(Result.success("查询通知列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
    //按通知生产提料单结束

    //按照通知编号查询所有的通知内容
    @ActionKey("/tongzhi/gettongzhibyid")
    @HttpMethod("GET")
    public void gettongzhibyid() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String noticeid = getPara("noticeid");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            if (noticeid == null || noticeid.trim().isEmpty()) {
                renderJson(Result.badRequest("noticeid 不能为空"));
                return;
            }

            Page<Record> page = tongzhiService.gettongzhibyid(pageNum, pageSz, noticeid);
            renderJson(Result.success("查询通知信息成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


// 确认通知接口
    @ActionKey("/tongzhi/querentongzhi")
    @HttpMethod("GET")
    public void querentongzhi() {
        // 测试接口：http://localhost:8099/tongzhi/querentongzhi
        String noticeid = getPara("noticeid");
        System.out.println("noticeid:"+noticeid);
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.querentongzhi(noticeid);
        if (success) {
            renderJson(Result.success("确认通知成功"));
        } else {
            renderJson(Result.serverError("确认通知失败"));
        }
    }

    // 反确认通知接口
    @ActionKey("/tongzhi/fanquerentongzhi")
    @HttpMethod("PUT")
    public void fanquerentongzhi() {
        // 测试接口：http://localhost:8099/tongzhi/fanquerentongzih
        String noticeid = getPara("noticeid");
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.fanquerentongzih(noticeid);
        if (success) {
            renderJson(Result.success("反确认通知成功"));
        } else {
            renderJson(Result.serverError("反确认通知失败"));
        }
    }

    // 校验通知接口
    @ActionKey("/tongzhi/jiaoyantongzhi")
    @HttpMethod("PUT")
    public void jiaoyantongzhi() {
        String noticeinstead = getPara("noticeinstead");
        String noticeid = getPara("noticeid");
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.jiaoyantongzhi(noticeinstead, noticeid);
        if (success) {
            renderJson(Result.success("校验通知成功"));
        } else {
            renderJson(Result.serverError("校验通知失败"));
        }
    }

    // 反校验通知接口
    @ActionKey("/tongzhi/fanjiaoyantongzhi")
    @HttpMethod("PUT")
    public void fanjiaoyantongzhi() {
        String noticeid = getPara("noticeid");
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.fanjiaoyantongzhi(noticeid);
        if (success) {
            renderJson(Result.success("反校验通知成功"));
        } else {
            renderJson(Result.serverError("反校验通知失败"));
        }
    }

    // 审核通知接口
    @ActionKey("/tongzhi/shenhetongzhi")
    @HttpMethod("PUT")
    public void shenhetongzhi() {
        // 测试接口：http://localhost:8099/tongzhi/shenhetongzhi
        String noticeshenhe = getPara("noticeshenhe");
        String noticeid = getPara("noticeid");
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.shenhetongzhi(noticeshenhe, noticeid);
        if (success) {
            renderJson(Result.success("审核通知成功"));
        } else {
            renderJson(Result.serverError("审核通知失败"));
        }
    }

    // 反审核通知接口
    @ActionKey("/tongzhi/fanshenhetongzhi")
    @HttpMethod("PUT")
    public void fanshenhtongzhi() {
        String noticeid = getPara("noticeid");
        if (noticeid == null || noticeid.trim().isEmpty()) {
            renderJson(Result.badRequest("noticeid 不能为空"));
            return;
        }
        boolean success = tongzhiService.fanshenhtongzhi(noticeid);
        if (success) {
            renderJson(Result.success("反审核通知成功"));
        } else {
            renderJson(Result.serverError("反审核通知失败"));
        }
    }


}