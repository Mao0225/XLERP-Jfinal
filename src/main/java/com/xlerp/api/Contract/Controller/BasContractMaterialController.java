package com.xlerp.api.Contract.Controller;


import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.Contract.Service.BasContractMaterialService;
import com.xlerp.common.model.BasContractMaterial;

import java.util.List;

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
}
