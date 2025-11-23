package com.xlerp.api.ItemManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.ItemManagement.Service.BasItemService;
import com.xlerp.common.model.Basitem;

import java.util.Map;

@Before(HttpMethodInterceptor.class)
public class BasItemController extends Controller {
    private final BasItemService basItemService = new BasItemService();

    @ActionKey("/basitem/getpage")
    @HttpMethod("GET")
    public void getpage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String itemNo = getPara("itemNo");
        String itemName = getPara("itemName");
        String spec = getPara("spec");
        String firstClassId = getPara("firstClassId");
        String secondClassId = getPara("secondClassId");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = basItemService.paginate(pageNum, pageSz,  itemNo, itemName, firstClassId, secondClassId, spec);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }
    @ActionKey("/basitem/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            Basitem basItem = basItemService.findById(Integer.parseInt(id));
            if (basItem != null) {
                renderJson(Result.success("查询物料成功").putData("basItem", basItem));
            } else {
                renderJson(Result.notFound("物料未找到"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        }
    }
    @ActionKey("/basitem/save")
    @HttpMethod("POST")
    public void save(Basitem basItem) {
        try {
            boolean success = basItemService.save(basItem);
            if (success) {
                // 成功：返回物料ID，方便前端后续操作（如刷新、编辑）
                renderJson(Result.success("物料保存成功")
                        .putData("itemId", basItem.getId()));
            } else {
                // 失败：明确提示是编号重复（核心失败原因）
                renderJson(Result.badRequest("物料编号已存在，请更换编号后重试"));
            }
        } catch (NumberFormatException e) {
            // 兼容前端可能的数值格式遗漏（如重量、价格未校验）
            renderJson(Result.badRequest("数值格式错误（重量/价格需为有效数字）"));
        } catch (Exception e) {
            renderJson(Result.serverError("物料保存失败，请联系管理员"));
        }
    }
    @ActionKey("/basitem/update")
    @HttpMethod("PUT")
    public void update(Basitem basItem) {

        try {

            boolean success = basItemService.update(basItem);
            if (success) {
                renderJson(Result.success("物料更新成功"));
            } else {
                renderJson(Result.serverError("更新物料失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID或数值格式错误（如重量或价格）"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新物料时发生错误: " + e.getMessage()));
        }
    }

    @ActionKey("/basitem/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            boolean success = basItemService.deleteById(Integer.parseInt(id.trim()));
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




    //上传表格文件自动导入基础物料信息
    @ActionKey("/basitem/importItem")
    @HttpMethod("POST")
    public void importItem() {
        try {
            UploadFile file = getFile("itemListFile"); // "itemListFile" is the form field name
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
            Map<String, Object> result = basItemService.parseBasitemExcel(file.getFile());
            file.getFile().delete(); //清除上传文件

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