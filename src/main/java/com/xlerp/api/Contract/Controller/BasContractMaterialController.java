package com.xlerp.api.Contract.Controller;


import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.Contract.Service.BasContractMaterialService;
import com.xlerp.common.model.BasContractMaterial;

import java.util.List;
import java.util.Map;

@Before(HttpMethodInterceptor.class)
public class BasContractMaterialController extends Controller {

    private BasContractMaterialService service = new BasContractMaterialService();
    @ActionKey("/bas_contract_material/getMaterialList")
    @HttpMethod("GET")
    public void getMaterialList() {
        String contractNo = getPara("contractNo");
        try {
            if (contractNo == null || contractNo.trim().isEmpty()){
                renderJson(Result.badRequest("合同编号不能为空"));
            }
            List record = service.getMaterialList(contractNo);
            renderJson(Result.success("查询备料列表成功").putData("record", record));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("合同号格式错误"));
        }
    }

    @ActionKey("/bas_contract_material/getpage")
    @HttpMethod("GET")
    public void getPage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String contractNo = getPara("contractNo");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = service.paginate(pageNum, pageSz, contractNo);
            renderJson(Result.success("查询备料列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    //更新备料单
    @ActionKey("/bas_contract_material/update")
    @HttpMethod("PUT")
    public void update(BasContractMaterial basContractMaterial) {
        boolean success = basContractMaterial.update();
        renderJson(success ? Result.success("更新成功") : Result.badRequest("更新失败"));
    }

    @ActionKey("/bas_contract_material/save")
    @HttpMethod("POST")
    public void save(BasContractMaterial basContractMaterial) {
        try {
            boolean success = basContractMaterial.save();
            renderJson(success ? Result.success("保存成功") : Result.badRequest("保存失败"));
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Result.badRequest("服务器错误，保存失败"));
        }
    }


    @ActionKey("/bas_contract_material/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");
        try {
            boolean success = service.deleteById(id);
            renderJson(success ? Result.success("删除成功") : Result.badRequest("删除失败"));
        }catch (Exception e){
            e.printStackTrace();
            renderJson(Result.badRequest("删除失败"));
        }
    }


    @ActionKey("/bas_contract_material/deleteByContractNo")
    @HttpMethod("DELETE")
    public void deleteByContractNo() {
        String contractNo = getPara("contractNo");
        try {
            boolean success = service.deleteByContractNo(contractNo);
            renderJson(success ? Result.success("删除成功") : Result.badRequest("删除失败"));
        }catch (Exception e){
            e.printStackTrace();
            renderJson(Result.badRequest("删除失败"));
        }
    }


    //获取备料单列表-通过物料关系查询的，没有存
    @ActionKey("/bas_contract_material/generateMaterialList")
    @HttpMethod("GET")
    public void generateMaterialList() {
        String contractNo = getPara("contractNo");
        if (contractNo == null || contractNo.trim().isEmpty()) {
            renderJson(Result.badRequest("合同号不能为空"));
        }
        try {
            List<Map<String, Object>> itemList = service.getContractMaterialLeafListWithMerge(contractNo);
            renderJson(Result.success("查询合同物料列表成功").putData("record", itemList));
        }catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

}
