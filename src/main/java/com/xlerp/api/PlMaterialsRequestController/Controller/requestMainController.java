package com.xlerp.api.PlMaterialsRequestController.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.api.Common.HttpMethod;
import com.xlerp.api.Common.HttpMethodInterceptor;
import com.xlerp.api.Common.Result;
import com.xlerp.api.PlMaterialsRequestController.Service.requestMainService;
import com.xlerp.common.model.PlMaterialRequestMain;

import java.util.List;
import java.util.Map;

/**
 * 领料单管理控制器
 */
@Before(HttpMethodInterceptor.class)
public class requestMainController extends Controller {

    private final requestMainService requestMainService = new requestMainService();

    /**
     * 分页查询领料单主表
     */
    @ActionKey("/material_request_main/getpage")
    @HttpMethod("GET")
    public void getPage() {
        String pageNumber = getPara("pageNumber", "1");
        String pageSize = getPara("pageSize", "20");
        String requestNo = getPara("requestNo");
        String applicantName = getPara("applicantName");
        String departmentName = getPara("departmentName");
        Integer status = getParaToInt("status");
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        Integer role = getParaToInt("role");//申请和库管员

        try {
            int pageNum = Integer.parseInt(pageNumber);
            int pageSz = Integer.parseInt(pageSize);

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<PlMaterialRequestMain> page = requestMainService.paginate(pageNum, pageSz, requestNo, applicantName, departmentName, status, startDate, endDate,role);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }

    /**
     * 根据ID查询领料单详情（包含明细）
     */
    /**
     * 根据 userId 分页查询本人的领料明细记录，并平铺主表信息
     */
    @ActionKey("/material_request_main/getMyHistoryWithDetails")
    @HttpMethod("GET")
    public void getMyHistoryWithDetails() {
        String pageNumber = getPara("pageNumber", "1");
        String pageSize = getPara("pageSize", "20");
        Long userId = getParaToLong("userId");
        if (userId == null) {
            renderJson(Result.badRequest("userId不能为空"));
            return;
        }

        try {
            int pageNum = Integer.parseInt(pageNumber);
            int pageSz = Integer.parseInt(pageSize);

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<Record> page = requestMainService.getMyHistoryWithDetails(pageNum, pageSz, userId);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("查询失败: " + e.getMessage()));
        }
    }

    @ActionKey("/material_request_main/getdetail")
    @HttpMethod("GET")
    public void getDetail() {
        String id = getPara("id");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("领料单ID不能为空"));
            return;
        }

        try {
            Long requestId = Long.parseLong(id);
            Map<String, Object> result = requestMainService.findWithDetails(requestId);
            if (result != null) {
                renderJson(Result.success("查询成功").putData("data", result));
            } else {
                renderJson(Result.notFound("未找到该领料单"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("领料单ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("查询失败: " + e.getMessage()));
        }
    }


    @ActionKey("/material_request/updateAll")
    @HttpMethod("POST")
    public void updateAll() {
        String jsonData = getRawData(); // 获取前端发来的完整主从 JSON
        try {
            boolean success = requestMainService.updateWithDetails(jsonData);
            if (success) {
                renderJson(Result.success("领料单及明细更新成功"));
            } else {
                renderJson(Result.badRequest("更新失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError(e.getMessage()));
        }
    }

    /**
     * 查询单个领料单主表记录
     */
    @ActionKey("/material_request_main/get")
    @HttpMethod("GET")
    public void get() {
        String id = getPara("id");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("领料单ID不能为空"));
            return;
        }

        try {
            Long requestId = Long.parseLong(id);
            PlMaterialRequestMain main = requestMainService.findById(requestId);
            if (main != null) {
                renderJson(Result.success("查询成功").putData("data", main));
            } else {
                renderJson(Result.notFound("未找到该领料单"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("领料单ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 创建领料单（包含明细）---没问题了
     */
    @ActionKey("/material_request_main/save")
    @HttpMethod("POST")
    public void save() {
        try {
            // 获取前端传来的JSON数据
            String jsonData = getRawData();
            Map<String, Object> result = requestMainService.saveWithDetails(jsonData);
            renderJson(Result.success("保存成功").putData("data", result));
        } catch (Exception e) {
            renderJson(Result.serverError("保存失败: " + e.getMessage()));
        }
    }

    /**
     * 更新领料单主表
     */
    @ActionKey("/material_request_main/update")
    @HttpMethod("PUT")
    public void update(PlMaterialRequestMain  main) {
        try {
            boolean success = requestMainService.update(main);
            if (success) {
                renderJson(Result.success("更新成功"));
            } else {
                renderJson(Result.serverError("更新失败"));
            }
        } catch (Exception e) {
            renderJson(Result.serverError("更新失败: " + e.getMessage()));
        }
    }

    /**
     * 删除领料单（包含明细）
     */
    @ActionKey("/material_request_main/delete")
    @HttpMethod("DELETE")
    public void delete() {
        String id = getPara("id");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("领料单ID不能为空"));
            return;
        }

        try {
            Long requestId = Long.parseLong(id);
            boolean success = requestMainService.deleteWithDetails(requestId);
            if (success) {
                renderJson(Result.success("删除成功"));
            } else {
                renderJson(Result.serverError("删除失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("领料单ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("删除失败: " + e.getMessage()));
        }
    }


    /**
     * 删除领料单明细
     */
    @ActionKey("/material_request_main/deletedetail")
    @HttpMethod("DELETE")
    public void deleteDetail() {
        String id = getPara("id");
        if (id == null || id.trim().isEmpty()) {
            renderJson(Result.badRequest("领料单明细ID不能为空"));
            return;
        }
        try {
            Long detailId = Long.parseLong(id);
            boolean success = requestMainService.deleteDetail(detailId);
            if (success) {
                renderJson(Result.success("删除成功"));
            }else {
                renderJson(Result.serverError("删除失败"));
            }
        }catch (NumberFormatException e){
            renderJson(Result.badRequest("服务器错误"));
        }
    }


    /**
     * 批量更新状态
     */
    @ActionKey("/material_request_main/batchupdatestatus")
    @HttpMethod("POST")
    public void batchUpdateStatus() {
        String ids = getPara("ids");
        String status = getPara("status");
        String remark = getPara("remark");

        if (ids == null || ids.trim().isEmpty()) {
            renderJson(Result.badRequest("领料单ID列表不能为空"));
            return;
        }

        if (status == null || status.trim().isEmpty()) {
            renderJson(Result.badRequest("状态不能为空"));
            return;
        }

        try {
            String[] idArray = ids.split(",");
            Integer newStatus = Integer.parseInt(status);
            int successCount = requestMainService.batchUpdateStatus(idArray, newStatus, remark);
            renderJson(Result.success("批量更新成功，共更新 " + successCount + " 条记录").putData("count", successCount));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("状态格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("批量更新失败: " + e.getMessage()));
        }
    }

    /**
     * 审核领料单（通过/拒绝）
     */
    @ActionKey("/material_request_main/review")
    @HttpMethod("POST")
    public void review() {
        String requestId = getPara("requestId");
        String status = getPara("status");
        String approvedQty = getPara("approvedQty");
        String keeperComment = getPara("keeperComment");

        if (requestId == null || requestId.trim().isEmpty()) {
            renderJson(Result.badRequest("领料单ID不能为空"));
            return;
        }

        if (status == null || status.trim().isEmpty()) {
            renderJson(Result.badRequest("审核状态不能为空"));
            return;
        }

        try {
            Long id = Long.parseLong(requestId);
            Integer reviewStatus = Integer.parseInt(status);
            Integer userId = getParaToInt("userId");
            String userName = getAttr("username");

            boolean success = requestMainService.reviewRequest(id, reviewStatus, approvedQty,
                keeperComment, userId, userName);
            if (success) {
                renderJson(Result.success("审核成功"));
            } else {
                renderJson(Result.serverError("审核失败"));
            }
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("参数格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("审核失败: " + e.getMessage()));
        }
    }


    /**
     * 分页查询领料单主表-查询本人的领料记录
     */
    @ActionKey("/material_request_main/getMyList")
    @HttpMethod("GET")
    public void getMyList() {
        String pageNumber = getPara("pageNumber", "1");
        String pageSize = getPara("pageSize", "20");
        String requestNo = getPara("requestNo");
        String applicantName = getPara("applicantName");
        String departmentName = getPara("departmentName");
        Integer status = getParaToInt("status");
        String startDate = getPara("startDate");
        String endDate = getPara("endDate");
        Integer userId = getParaToInt("userId");

        try {
            int pageNum = Integer.parseInt(pageNumber);
            int pageSz = Integer.parseInt(pageSize);

            if (pageNum < 1 || pageSz < 1) {
                renderJson(Result.badRequest("页码或每页大小必须为正整数"));
                return;
            }

            Page<PlMaterialRequestMain> page = requestMainService.mypaginate(
                    pageNum, pageSz, requestNo, applicantName, departmentName, status, startDate, endDate, userId
            );
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        }
    }


    /**
     * 获取待审核领料单列表（库管员视角）
     */
    @ActionKey("/material_request_main/getpendinglist")
    @HttpMethod("GET")
    public void getPendingList() {
        String pageNumber = getPara("pageNumber", "1");
        String pageSize = getPara("pageSize", "20");
        Integer userId = getParaToInt("userId");

        try {
            int pageNum = Integer.parseInt(pageNumber);
            int pageSz = Integer.parseInt(pageSize);

            Page<PlMaterialRequestMain> page = requestMainService.getPendingRequestList(
                userId, pageNum, pageSz);
            renderJson(Result.success("查询成功").putData("page", page));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("页码或每页大小格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 物料库存批次查询（为核心）
     * 为某个领料项寻找"货源"
     */
    @ActionKey("/material_stock/getbatchbysitemid")
    @HttpMethod("GET")
    public void getBatchByItemId() {
        String itemId = getPara("itemId");

        if (itemId == null || itemId.trim().isEmpty()) {
            renderJson(Result.badRequest("物料ID不能为空"));
            return;
        }

        try {
            Long materialId = Long.parseLong(itemId);
            List<Record> stockBatches = requestMainService.findStockBatchesByItemId(materialId);
            renderJson(Result.success("查询成功").putData("data", stockBatches));
        } catch (NumberFormatException e) {
            renderJson(Result.badRequest("物料ID格式错误"));
        } catch (Exception e) {
            renderJson(Result.serverError("查询失败: " + e.getMessage()));
        }
    }

    /**
     * 接口：执行出库分配
     */
    @ActionKey("/material_stock/executeDispatch")
    @HttpMethod("GET")
    public void executeDispatch() {
        // 1. 获取领料明细ID
        Long detailId = getParaToLong("detailId");

        // 2. 获取分配详情 JSON（例如：[{"inboundId":101, "qty":5.5}, {"inboundId":102, "qty":10.0}]）
        // 注意：如果是前端发送的 raw JSON，请使用 getRawData()
        String dispatchData = getPara("dispatchData");
        String userName = getPara("userName");

        System.out.println("detailId: " + detailId);
        System.out.println("dispatchData: " + dispatchData);
        // 3. 获取操作人信息（通常从登录 Session 中拿）
//        String userName = getSessionAttr("userName");
        if (userName == null) userName = "系统管理员";

        if (detailId == null || StrKit.isBlank(dispatchData)) {
            renderJson(Result.badRequest("缺少必要参数：detailId 或 dispatchData"));
            return;
        }

        try {
            // 调用 Service
            Map<String, Object> res = requestMainService.executeDispatch(detailId, dispatchData, userName);

            // 按照你习惯的格式返回
            renderJson(Result.success("出库分配成功").putData("info", res));
        } catch (Exception e) {
            // 捕获 Service 抛出的 RuntimeException（例如“库存不足”）
            renderJson(Result.serverError("出库失败: " + e.getMessage()));
        }
    }


    /**
     * 从领料-出库关联表查询该条领料已关联的出库记录
     */
    @ActionKey("/material_stock/getDispatchRecords")
    @HttpMethod("GET")
    public void getDispatchRecords() {
        Long detailId = getParaToLong("detailId");
        try {
            List<Record> records = requestMainService.getDispatchRecords(detailId);
            renderJson(Result.success("查询成功").putData("records", records));
        } catch (Exception e){
            renderJson(Result.serverError("查询失败: " + e.getMessage()));
        }
    }



    /**
     * 接口 11：撤销配货记录
     * 反向回滚库存并清除关联记录
     */
    @ActionKey("/material_stock/cancelDispatch")
    @HttpMethod("POST") // 涉及数据变更，建议用 POST
    public void cancelDispatch() {
        // 获取关联表的主键 ID

        // 读取原始请求体
        String rawData = getRawData();
        // 判断请求体是否为空
        if (rawData == null || rawData.trim().isEmpty()) {
            renderJson(Result.badRequest("请求体不能为空"));
            return;
        }
        // 把 JSON 字符串转成 JSONObject
        JSONObject jsonObject = JSON.parseObject(rawData);
        // 从 JSON 里取 id
        Long allocationId = jsonObject.getLong("allocationId");

        // 从 Session 获取当前操作人
        String userName = getSessionAttr("userName");
        System.out.println("userName: " + userName);

        if (allocationId == null) {
            renderJson(Result.badRequest("缺少必要参数：allocationId"));
            return;
        }

        try {
            boolean success = requestMainService.cancelDispatch(allocationId, userName);
            if (success) {
                renderJson(Result.success("撤销配货成功"));
            } else {
                renderJson(Result.badRequest("撤销操作失败,服务器错误"));
            }
        } catch (Exception e) {
            // 记录日志并返回错误信息
            e.printStackTrace();
            renderJson(Result.serverError("撤销失败: " + e.getMessage()));
        }
    }


    /**
     * 获取领料详情（包含分级配货记录）
     */
    @ActionKey("/material_request/getDetailsWithAllocations")
    @HttpMethod("GET")
    public void getDetailsWithAllocations() {
        Long requestId = getParaToLong("requestId");

        if (requestId == null) {
            renderJson(Result.badRequest("缺少领料单ID"));
            return;
        }

        try {
            List<Record> details = requestMainService.getDetailsWithAllocations(requestId);
            // 返回格式对接你之前的 Result 封装
            renderJson(Result.success("查询成功").putData("details", details));
        } catch (Exception e) {
            renderJson(Result.serverError("详情加载失败: " + e.getMessage()));
        }
    }



    /**
     * 领料人确认领取
     */
    @ActionKey("/material_request_allocation/confirmReceive")
    @HttpMethod("POST")
    public void confirmReceive() {
        // 读取原始请求体
        String rawData = getRawData();
        // 判断请求体是否为空
        if (rawData == null || rawData.trim().isEmpty()) {
            renderJson(Result.badRequest("请求体不能为空"));
            return;
        }
        // 把 JSON 字符串转成 JSONObject
        JSONObject jsonObject = JSON.parseObject(rawData);
        // 从 JSON 里取 id
        Integer id = jsonObject.getInteger("id");
        // 判空
        if (id == null || id == 0) {
            renderJson(Result.badRequest("缺少ID"));
            return;
        }
        // 调用业务方法
        boolean success = requestMainService.confirmReceive(id);
        if (success) {
            renderJson(Result.success("领料人确认领取成功"));
        } else {
            renderJson(Result.badRequest("领料人确认领取失败"));
        }
    }
}
