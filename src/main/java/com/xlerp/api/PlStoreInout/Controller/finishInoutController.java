package com.xlerp.api.PlStoreInout.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlStoreInout.Service.finishInoutService;
import com.xlerp.common.model.PlFinishInoutList;

@Before(HttpMethodInterceptor.class)
public class finishInoutController extends Controller {



    private  final finishInoutService service = new finishInoutService();



    @ActionKey("/pl_finish_inout/getpage")
    @HttpMethod("GET")
    public void getpage() {
        // 获取分页参数
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String type = getPara("type");
        String materialName = getPara("materialName");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            // 调用修改后的分页查询方法，传入所有查询条件
            Page page = service.paginate(pageNum, pageSz,type,materialName);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


    //保存
    @ActionKey("/pl_finish_inout/save")
    @HttpMethod("POST")
    public void save(PlFinishInoutList pl_finish_inout ) {
        try {
            boolean success = pl_finish_inout.save();
            if (success) {
                renderJson(Result.success("保存成功"));
            } else {
                renderJson(Result.badRequest("保存失败"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.badRequest("保存失败"));
        }
    }
}
