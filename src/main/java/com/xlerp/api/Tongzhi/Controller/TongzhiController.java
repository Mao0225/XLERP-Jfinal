package com.xlerp.api.Tongzhi.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
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


}