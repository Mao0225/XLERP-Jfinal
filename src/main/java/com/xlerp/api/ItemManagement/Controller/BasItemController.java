package com.xlerp.api.ItemManagement.Controller;

import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.ItemManagement.Service.BasItemService;
import com.xlerp.common.model.Basitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        String inclass = getPara("inclass");
        String type = getPara("type");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = basItemService.paginate(pageNum, pageSz,  itemNo, itemName, inclass, type);
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
        // 校验必填字段

        try {
            boolean success = basItemService.save(basItem);
            if (success) {
                renderJson(Result.success("物料保存成功").putData("itemId", basItem.getId()));
            } else {
                renderJson(Result.serverError("保存物料失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("数值格式错误（如重量或价格）"));
        } catch (Exception e) {
            renderJson(Result.serverError("保存物料时发生错误: " + e.getMessage()));
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

    //刘国奇，获取原材料，用于为图纸配置原材料清单
    @ActionKey("/basitem/getyuancailiaopage")
    @HttpMethod("GET")
    public void getyuancailiaopage() {
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");
        String itemNo = getPara("itemNo");
        String itemName = getPara("itemName");
        String inclass = getPara("inclass");
        String type = getPara("type");

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page page = basItemService.tuzhiyuancailiaopaginate(pageNum, pageSz,  itemNo, itemName, inclass, type);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
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



    @ActionKey("/basitem/material/tree")
    @HttpMethod("GET")
    public void getMaterialTree() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            List<Map<String, Object>> materialTree = basItemService.getItemMaterialTree(Integer.parseInt(id));
            renderJson(Result.success("获取物料材料树成功").putData("materialTree", materialTree));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("获取物料材料树失败: " + e.getMessage()));
        }
    }

    @ActionKey("/basitem/material/list")
    @HttpMethod("GET")
    public void getMaterialList() {
        String id = getPara("id");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            List<Map<String, Object>> materialList = basItemService.getItemAllMaterials(Integer.parseInt(id));
            renderJson(Result.success("获取物料材料列表成功").putData("materialList", materialList));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("获取物料材料列表失败: " + e.getMessage()));
        }
    }


    @ActionKey("/basitem/material/addRelation")
    @HttpMethod("POST")
    public void addMaterialRelation() {
        try {
            // 1. 获取请求体的JSON字符串（适用于application/json格式）
            String json = getRawData();
            if (StrKit.isBlank(json)) {
                renderJson(Result.badRequest("请求体不能为空"));
                return;
            }

            // 2. 解析JSON为Map（或自定义实体类）
            Map<String, Object> params = com.alibaba.fastjson.JSON.parseObject(json, Map.class);

            // 3. 从Map中提取参数（注意：参数名需与前端传递的JSON key完全一致）
            String parentItemId = params.get("parentItemId") != null ? params.get("parentItemId").toString() : null;
            String childItemId = params.get("childItemId") != null ? params.get("childItemId").toString() : null;
            String quantity = params.get("quantity") != null ? params.get("quantity").toString() : null;
            String memo = params.get("memo") != null ? params.get("memo").toString() : null;

            // 打印参数用于调试
            System.out.println("解析到的参数:");
            System.out.println("parentItemId: " + parentItemId);
            System.out.println("childItemId: " + childItemId);
            System.out.println("quantity: " + quantity);
            System.out.println("memo: " + memo);

            // 4. 校验父物料ID和子物料ID
            if (StrKit.isBlank(parentItemId) || StrKit.isBlank(childItemId)) {
                System.out.println("参数验证失败: parentItemId或childItemId为空");
                renderJson(Result.badRequest("父物料ID和子物料ID不能为空"));
                return;
            }

            // 5. 解析参数为对应类型
            int parentId = Integer.parseInt(parentItemId.trim());
            int childId = Integer.parseInt(childItemId.trim());
            BigDecimal qty = (StrKit.notBlank(quantity)) ? new BigDecimal(quantity.trim()) : BigDecimal.ONE;

            // 6. 调用服务层添加关系
            boolean success = basItemService.addMaterialRelation(parentId, childId, qty, memo);
            if (success) {
                System.out.println("添加子物料关系成功");
                renderJson(Result.success("添加子物料成功"));
            } else {
                System.out.println("添加子物料关系失败 - 服务层返回false");
                renderJson(Result.serverError("添加子物料失败"));
            }

        } catch (NumberFormatException e) {
            System.out.println("参数格式错误: " + e.getMessage());
            renderJson(Result.badRequest("物料ID或数量格式错误"));
        } catch (Exception e) {
            System.out.println("添加子物料关系异常: " + e.getMessage());
            e.printStackTrace();
            renderJson(Result.serverError("添加子物料时发生错误: " + e.getMessage()));
        }
    }
    @ActionKey("/basitem/material/removeRelation")
    @HttpMethod("DELETE")
    public void removeMaterialRelation() {
        String relationId = getPara("relationId");

        if (relationId == null || relationId.trim().isEmpty()) {
            renderJson(Result.badRequest("关系ID不能为空"));
            return;
        }

        try {
            boolean success = basItemService.removeMaterialRelation(Integer.parseInt(relationId));
            if (success) {
                renderJson(Result.success("移除子物料成功"));
            } else {
                renderJson(Result.notFound("关系不存在或移除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("关系ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("移除子物料时发生错误: " + e.getMessage()));
        }
    }
    @ActionKey("/basitem/material/childList")
    @HttpMethod("GET")
    public void getChildMaterialList() {
        String id = getPara("id");
        String pageNumber = getPara("pageNumber");
        String pageSize = getPara("pageSize");

        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            int pageNum = (pageNumber != null && !pageNumber.trim().isEmpty()) ? Integer.parseInt(pageNumber) : 1;
            int pageSz = (pageSize != null && !pageSize.trim().isEmpty()) ? Integer.parseInt(pageSize) : 10;

            // 获取子物料列表
            List<Record> childMaterials = basItemService.getChildMaterials(Integer.parseInt(id));

            // 手动分页处理
            int total = childMaterials.size();
            int fromIndex = (pageNum - 1) * pageSz;
            int toIndex = Math.min(fromIndex + pageSz, total);

            List<Record> pagedList = childMaterials.subList(fromIndex, toIndex);

            // 转换为前端需要的格式 - 修复数据类型转换
            List<Map<String, Object>> resultList = new ArrayList<>();
            for (Record record : pagedList) {
                Map<String, Object> item = new HashMap<>();
                item.put("relation_id", record.getInt("relation_id"));
                item.put("id", record.getInt("id"));
                item.put("no", record.getStr("no"));
                item.put("name", record.getStr("name"));
                item.put("spec", record.getStr("spec"));
                item.put("unit", record.getStr("unit"));
                item.put("relation_quantity", record.getBigDecimal("relation_quantity"));  // ✅ 修复
                item.put("type", record.getInt("type"));
                item.put("inclass", record.getStr("inclass"));
                item.put("weight", record.getBigDecimal("weight"));                        // ✅ 修复
                item.put("planned_price", record.getBigDecimal("planned_price"));          // ✅ 修复
                item.put("avg_price", record.getBigDecimal("avg_price"));                  // ✅ 修复
                item.put("tech_memo", record.getStr("tech_memo"));
                item.put("memo", record.getStr("memo"));
                resultList.add(item);
            }

            Map<String, Object> resultData = new HashMap<>();
            resultData.put("list", resultList);
            resultData.put("totalRow", total);
            resultData.put("pageNumber", pageNum);
            resultData.put("pageSize", pageSz);
            resultData.put("totalPage", (int) Math.ceil((double) total / pageSz));

            renderJson(Result.success("获取子物料列表成功").putData("page", resultData));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID或分页参数格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("获取子物料列表失败: " + e.getMessage()));
        }
    }

    @ActionKey("/basitem/material/updateRelation")
    @HttpMethod("PUT")
    public void updateMaterialRelation() {
        try {
            // 1. 获取请求体JSON
            String json = getRawData();
            if (StrKit.isBlank(json)) {
                renderJson(Result.badRequest("请求体不能为空"));
                return;
            }

            // 2. 解析参数
            Map<String, Object> params = com.alibaba.fastjson.JSON.parseObject(json, Map.class);
            String relationIdStr = params.get("relationId") != null ? params.get("relationId").toString() : null;
            String quantityStr = params.get("quantity") != null ? params.get("quantity").toString() : null;
            String memo = params.get("memo") != null ? params.get("memo").toString() : null;

            // 3. 校验必填参数（关系ID）
            if (StrKit.isBlank(relationIdStr)) {
                renderJson(Result.badRequest("关系ID不能为空"));
                return;
            }

            // 4. 转换参数类型
            int relationId = Integer.parseInt(relationIdStr.trim());
            BigDecimal quantity = (StrKit.notBlank(quantityStr)) ? new BigDecimal(quantityStr.trim()) : null;

            // 5. 调用服务层更新
            boolean success = basItemService.updateMaterialRelation(relationId, quantity, memo);
            if (success) {
                renderJson(Result.success("更新物料关系成功"));
            } else {
                renderJson(Result.notFound("关系不存在或更新失败"));
            }

        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("关系ID或数量格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("更新物料关系时发生错误: " + e.getMessage()));
        }
    }
}