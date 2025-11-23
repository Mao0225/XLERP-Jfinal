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

    //获取备料单列表根据合同号，这个是已经合并过的
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


    //分页查询--用于采购计划关联备料单列表时候
    @ActionKey("/bas_contract_material/getpage")
    @HttpMethod("GET")
    public void getPage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String contractNo = getPara("contractNo");
        Integer relationStatus = getParaToInt("relationStatus") == null ? 0 : getParaToInt("relationStatus");//关联状态0是不筛选，1是筛选出未关联的
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = service.paginate(pageNum, pageSz, contractNo,relationStatus);
            renderJson(Result.success("查询备料列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


    //分页查询--用于检验单选择的时候应该是返回已经关联过采购计划的，也就是purchaseOrderNo不为空的
    @ActionKey("/bas_contract_material/getpageForInsp")
    @HttpMethod("GET")
    public void getpageForInsp() {
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

            Page<Record> page = service.paginateForInsp(pageNum, pageSz, contractNo);
            renderJson(Result.success("查询备料列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    //更新备料单--用于采购订单加信息的时候-还有制定备料单的时候加备注用
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


    //生成备料单列表-通过物料关系查询的，没有存，是先查出来看，前端点击保存后才会保存，根据一个合同号生成
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


    //生成备料计划，每个产品独立的原材料列表
    @ActionKey("/bas_contract_material/generateMaterialPlan")
    @HttpMethod("GET")
    public void generateMaterialPlan() {
        String contractNo = getPara("contractNo");
        if (contractNo == null || contractNo.trim().isEmpty()) {
            renderJson(Result.badRequest("合同号不能为空"));
        }
        try {
            List<Map<String, Object>> itemList = service.getContractMaterialPlan(contractNo);
            renderJson(Result.success("查询合同物料列表成功").putData("record", itemList));
        }catch (NumberFormatException e){
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


    //获取单个信息
    @ActionKey("/bas_contract_material/getById")
    @HttpMethod("GET")
    public void getById() {
        String id = getPara("id");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("id不能为空"));
        }
        try {
            Record record = service.getById(id);
            renderJson(Result.success("查询合同物料列表成功").putData("record", record));
        }catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

}
