package com.xlerp.api.Contract.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.Contract.Service.BasContractService;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.common.model.Bascontract;
import com.xlerp.common.model.Bascontractitem;

import java.util.List;
import java.util.Map;

@Before(HttpMethodInterceptor.class)
public class BasContractController extends Controller {
    private final BasContractService bascontractService = new BasContractService();



    @ActionKey("/bascontract/getContractList")
    @HttpMethod("GET")
    public void getContractList() {
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

            Page<Record> page = bascontractService.getContractList(pageNum, pageSz, term, contractNo, projectName, salesmanNo, rule,owenr);
            renderJson(Result.success("查询合同列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


    @ActionKey("/bascontract/getContractInfoByNo")
    @HttpMethod("GET")
    public void getContractInfoByNo() {
        String contractNo = getPara("contractNo");
        if (contractNo == null || contractNo.trim().isEmpty()) {
            renderJson(Result.badRequest("合同号不能为空"));
        }
        try {
            Record contractInfo = bascontractService.getContractInfoByNo(contractNo);
            List<Record> itemList = bascontractService.getContractItemByNo(contractNo);
            renderJson(Result.success("查询合同列表成功").putData("contractInfo", contractInfo).putData("itemList", itemList));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    @ActionKey("/bascontract/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("合同ID不能为空"));
            return;
        }

        try {
            Bascontract bascontract = bascontractService.findById(Integer.parseInt(id.trim()));
            if (bascontract != null) {
                renderJson(Result.success("查询合同成功").putData("bascontract", bascontract));
            } else {
                renderJson(Result.notFound("合同未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("合同ID格式错误"));
        }
    }

    @ActionKey("/bascontract/save")
    @HttpMethod("POST")
    public void save(Bascontract bascontract) {
        if (bascontract.getNo() == null || bascontract.getNo().trim().isEmpty()) {
            renderJson(Result.badRequest("合同编号不能为空"));
            return;
        }
        if (bascontract.getName() == null || bascontract.getName().trim().isEmpty()) {
            renderJson(Result.badRequest("合同名称不能为空"));
            return;
        }
        Bascontract con = bascontractService.findbyNo(bascontract.getNo());
        if (con != null) {
            renderJson(Result.badRequest("合同编号已存在"));
            return;
        }
        try {
            bascontract.setIsdelete(0); // 设置isdelete字段为0对于新纪录
            boolean success = bascontractService.save(bascontract);
            if (success) {
                renderJson(Result.success("合同保存成功").putData("bascontractId", bascontract.getId()));
            } else {
                renderJson(Result.serverError("保存合同失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存合同时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/bascontract/update")
    @HttpMethod("PUT")
    public void update(Bascontract bascontract) {
        try {
            boolean success = bascontractService.update(bascontract);
            if (success) {
                renderJson(Result.success("合同更新成功"));
            } else {
                renderJson(Result.serverError("更新合同失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("合同ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新合同时发生错误: " + e.getMessage()));
        }
    }


    @ActionKey("/bascontract/updateStatusById")
    @HttpMethod("PUT")
    public void updateStatusById() {
        String jsonBody = getRawData(); // 获取原始 JSON 字符串
// 然后使用 FastJson/Gson 等解析
        JSONObject jsonObj = JSON.parseObject(jsonBody);
        String contractId = jsonObj.getString("contractId");
        String status = jsonObj.getString("status");
        try {
            boolean success = bascontractService.updateStatusById(contractId, status);
            if (success) {
                renderJson(Result.success("合同状态更新成功"));
            } else {
                renderJson(Result.serverError("更新合同状态失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("合同ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新合同状态时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/bascontract/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("contractId");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("合同ID不能为空"));
            return;
        }

        try {
            boolean success = bascontractService.deleteById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("合同删除成功"));
            } else {
                renderJson(Result.notFound("合同不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("合同ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除合同时发生错误: " + e.getMessage()));
        }
    }

    //获取合同物料
    @ActionKey("/bascontract/getitem")
    @HttpMethod("GET")
    public void getitem() {
        String id = getPara("itemId");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }
        try {
            Record bascontractitem = bascontractService.finditemById(Integer.parseInt(id.trim()));
            if (bascontractitem != null) {
                renderJson(Result.success("查询物料成功").putData("item", bascontractitem));
            } else {
                renderJson(Result.notFound("物料未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        }
    }

    @ActionKey("/bascontract/saveitem")
    @HttpMethod("POST")
    public void saveitem(Bascontractitem bascontractitem) {
        try {
            bascontractitem.setIsdelete(0); // 设置isdelete字段为0对于新纪录
            boolean success = bascontractService.saveitem(bascontractitem);
            if (success) {
                renderJson(Result.success("物料保存成功").putData("itemId", bascontractitem.getId()));
            } else {
                renderJson(Result.serverError("保存物料失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("保存物料时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/bascontract/updateitem")
    @HttpMethod("PUT")
    public void updateitem(Bascontractitem bascontractitem) {
        try {
            boolean success = bascontractService.updateitem(bascontractitem);
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

    @ActionKey("/bascontract/deleteitem")
    @HttpMethod("DELETE")
    public void deleteitem() {
        String id = getPara("itemId");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            boolean success = bascontractService.deleteitemById(Integer.parseInt(id.trim()));
            if (success) {
                renderJson(Result.success("物料删除成功"));
            } else {
                renderJson(Result.notFound("物料不存在或删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除物料时发生错误: " + e.getMessage()));
        }
    }

    //获取确认后的合同列表
    @ActionKey("/bascontract/getConfirmedList")
    @HttpMethod("GET")
    public void getConfirmedList() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String contractNo = getPara("contractNo");
        String projectName = getPara("projectName");
        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = bascontractService.getConfirmedList(pageNum, pageSz, contractNo, projectName);
            renderJson(Result.success("查询合同列表成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


    //上传表格文件自动导入合同物料信息
    @ActionKey("/bascontract/importContractItem")
    @HttpMethod("POST")
    public void importContractItem() {
        try {
            UploadFile file = getFile("itemListFile"); // "itemListFile" is the form field name
            String contractNo = getPara("contractNo");
            System.out.println("导入文件的合同号contractNo:" + contractNo);
            if (file == null) {
                renderJson(Result.badRequest("未上传文件"));
                return;
            }

            // 验证文件大小 (e.g., max 10MB)
            String fileName = file.getFileName().toLowerCase();
            if (!fileName.endsWith(".xls") && !fileName.endsWith(".xlsx")) {
                file.getFile().delete();
                renderJson(Result.badRequest("仅支持 .xls 或 .xlsx 文件"));
                return;
            }
            if (file.getFile().length() > 10 * 1024 * 1024) { // 10MB limit
                file.getFile().delete();
                renderJson(Result.badRequest("文件大小超过10MB限制"));
                return;
            }

            // Parse file and get result
            Map<String, Object> result = bascontractService.parseContractExcel(file.getFile(),contractNo);
            file.getFile().delete(); // Clean up uploaded file

            renderJson(Result.success("文件解析完成")
                    .putData("successCount", result.get("successCount"))
                    .putData("failedRows", result.get("failedRows"))
                    .putData("failedCount", result.get("failedCount"))
                    .putData("totalRows", result.get("totalRows")));
//                    .putData("itemList", result.get("itemList")));
        } catch (Exception e) {
            renderJson(Result.badRequest("文件解析失败: " + e.getMessage()));
        }
    }
}