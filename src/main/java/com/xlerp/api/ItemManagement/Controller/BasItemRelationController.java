package com.xlerp.api.ItemManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.ItemManagement.Service.BasItemRelationService;
import com.xlerp.common.model.BasItemRelation;

import java.util.Map;


@Before(HttpMethodInterceptor.class)
public class BasItemRelationController extends Controller {
    private final BasItemRelationService service = new BasItemRelationService();



    @ActionKey("/bas_item_relation/tree")
    @HttpMethod("GET")
    public void getMaterialTree() {
        Integer id = getParaToInt("id");
        if (id == null) {
            renderJson(Result.serverError("参数错误"));
            return;
        }
        try {
            Map<String, Object> materialTree = service.getMaterialTree(id);
            renderJson(Result.success("查询成功").putData("tree", materialTree));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.serverError("查询失败"));
        }
    }


    //实现增删改方法
    @ActionKey("/bas_item_relation/add")
    @HttpMethod("POST")
    public void save(BasItemRelation basItemRelation) {
        boolean success = basItemRelation.save();
        renderJson(success ? Result.success("创建成功").putData("id", basItemRelation.getId()) : Result.serverError("创建失败"));
    }

    @ActionKey("/bas_item_relation/update")
    @HttpMethod("PUT")
    public void update(BasItemRelation basItemRelation) {
        boolean success = basItemRelation.update();
        renderJson(success ? Result.success("更新成功") : Result.serverError("更新失败"));
    }

    @ActionKey("/bas_item_relation/delete")
    @HttpMethod("DELETE")
    public void delete() {
        Integer id = getParaToInt("id");
        boolean success = service.deleteById(id);
        renderJson(success ? Result.success("删除成功") : Result.serverError("删除失败"));
    }
}
