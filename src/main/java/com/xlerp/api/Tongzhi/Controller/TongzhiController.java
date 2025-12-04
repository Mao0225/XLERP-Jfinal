package com.xlerp.api.Tongzhi.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.Tongzhi.Service.TongzhiService;

@Before(HttpMethodInterceptor.class)
public class TongzhiController extends Controller {
    private final TongzhiService tongzhiService = new TongzhiService();


    //更新单个合同产品的通知状态
    @ActionKey("/tongzhi/updateStatus")
    @HttpMethod("GET")
    public void updateStatus() {
        String id = getPara("id");
        String status = getPara("status");
        boolean success = tongzhiService.updateStatus(id, status);
        renderJson( success ? Result.success("更新成功") : Result.serverError("更新失败"));
    }

    //通过通知Id更新该批通知的状态noticeid
    @ActionKey("/tongzhi/updateBatchStatus")
    @HttpMethod("GET")
    public void updateBatchStatus() {
        String noticeid = getPara("noticeid");
        String status = getPara("status");
        boolean success = tongzhiService.updateBatchStatus(noticeid, status);
        renderJson( success ? Result.success("更新成功") : Result.serverError("更新失败"));
    }

    //获取通知备料单根据合同号
    @ActionKey("/tongzhi/getMaterialForm")
    @HttpMethod("GET")
    public void getMaterialForm() {
        String noticeid = getPara("noticeid");
        String status = getPara("status");
        boolean success = tongzhiService.updateBatchStatus(noticeid, status);
        renderJson( success ? Result.success("更新成功") : Result.serverError("更新失败"));
    }

    //接收部分通知的id集合，和本次通知的通知编号和通知名称来制定这些id的记录的编号和名称
    @ActionKey("/tongzhi/updateBatchNotice")
    @HttpMethod("GET")
    public void updateBatchNotice() {
        String noticeNo = getPara("noticeNo");
        String noticeName = getPara("noticeName");
        //这个传递的是15，25这样的
        String ids = getPara("ids");
        System.out.println(noticeNo + " " + noticeName + " " + ids);
        boolean success = tongzhiService.updateBatchNotice(noticeNo, noticeName, ids);
        renderJson( success ? Result.success("更新成功") : Result.serverError("更新失败"));
    }

    //获取通知分页列表，根据noticeid合并
    @ActionKey("/tongzhi/getNoticeGroup")
    @HttpMethod("GET")
    public void getNoticeGroup() {
        String noticeid = getPara("noticeid");
        String noticeName = getPara("noticeName");
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        try {
            Page page = TongzhiService.getNoticeGroup(noticeid, noticeName, pageNumber, pageSize);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("查询失败"));
        }
    }





}