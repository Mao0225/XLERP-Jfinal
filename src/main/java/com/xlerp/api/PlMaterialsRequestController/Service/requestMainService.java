package com.xlerp.api.PlMaterialsRequestController.Service;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.PlMatInoutList;
import com.xlerp.common.model.PlMaterialRequestAllocation;
import com.xlerp.common.model.PlMaterialRequestDetail;
import com.xlerp.common.model.PlMaterialRequestMain;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

/**
 * 领料单业务服务类
 */
public class requestMainService {

    private static final PlMaterialRequestMain dao = new PlMaterialRequestMain();
    private static final PlMaterialRequestDetail detailDao = new PlMaterialRequestDetail();
    private static final PlMaterialRequestAllocation allocDao = new PlMaterialRequestAllocation();


    /**
     * 分页查询领料单主表---
     */
    public Page<PlMaterialRequestMain> paginate(int pageNumber, int pageSize, String requestNo,
                                                String applicantName, String departmentName,
                                                Integer status, String startDate, String endDate, Integer role) {
        String select = "select *";
        StringBuilder from = new StringBuilder(" from pl_material_request_main where 1=1");
        List<Object> params = new ArrayList<>();

        // 1. 动态构建查询条件
        if (StrKit.notBlank(requestNo)) {
            from.append(" and requestNo like ?");
            params.add("%" + requestNo + "%");
        }
        if (StrKit.notBlank(applicantName)) {
            from.append(" and applicantName like ?");
            params.add("%" + applicantName + "%");
        }
        if (StrKit.notBlank(departmentName)) {
            from.append(" and departmentName like ?");
            params.add("%" + departmentName + "%");
        }

        // 2. 权限与状态逻辑处理 (核心错误修复点)
        if (status != null) {
            from.append(" and status = ?");
            params.add(status);
        } else if (role != null && role == 2) {
            // 库管员角色且未指定状态时，只能看到“待审核”及以后的记录 (排除草稿状态0)
            from.append(" and status >= 1");
        }

        // 3. 时间过滤
        if (StrKit.notBlank(startDate)) {
            from.append(" and createTime >= ?");
            params.add(startDate);
        }
        if (StrKit.notBlank(endDate)) {
            from.append(" and createTime <= ?");
            params.add(endDate);
        }

        from.append(" order by id desc");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }



    /**
     * 分页查询领料单主表---
     */
    public Page<PlMaterialRequestMain> mypaginate(int pageNumber, int pageSize, String requestNo,
                                                String applicantName, String departmentName,
                                                Integer status, String startDate, String endDate, Integer userId) {
        String select = "select *";
        StringBuilder from = new StringBuilder(" from pl_material_request_main where 1=1");
        List<Object> params = new ArrayList<>();

        // 1. 动态构建查询条件
        if (StrKit.notBlank(requestNo)) {
            from.append(" and requestNo like ?");
            params.add("%" + requestNo + "%");
        }
        if (StrKit.notBlank(applicantName)) {
            from.append(" and applicantName like ?");
            params.add("%" + applicantName + "%");
        }
        if (StrKit.notBlank(departmentName)) {
            from.append(" and departmentName like ?");
            params.add("%" + departmentName + "%");
        }

        // 2. 权限与状态逻辑处理 (核心错误修复点)
        if (status != null) {
            from.append(" and status = ?");
            params.add(status);
        }

        if (userId != null){
            from.append(" and applicantId = ?");
            params.add(userId);
        }

        // 3. 时间过滤
        if (StrKit.notBlank(startDate)) {
            from.append(" and createTime >= ?");
            params.add(startDate);
        }
        if (StrKit.notBlank(endDate)) {
            from.append(" and createTime <= ?");
            params.add(endDate);
        }

        from.append(" order by id desc");

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
    }

    /**
     * 根据ID查找领料单
     */
    /**
     * 根据 userId 查询历史领料记录及其明细
     */
    public Page<Record> getMyHistoryWithDetails(int pageNumber, int pageSize, Long userId) {
        String select = "select " +
                "d.id, " +
                "d.requestId, " +
                "d.materialId, " +
                "d.requestQty, " +
                "d.approvedQty, " +
                "COALESCE(d.actualQty, 0) as actualQty, " +
                "COALESCE(u.usedQty, d.usedQty, 0) as usedQty, " +
                "(COALESCE(d.actualQty, 0) - COALESCE(u.usedQty, d.usedQty, 0)) as availableQty, " +
                "d.detailStatus, " +
                "d.remark as detailRemark, " +
                "d.createTime as detailCreateTime, " +
                "d.updateTime as detailUpdateTime, " +
                "m.id as mainId, " +
                "m.requestNo, " +
                "m.applicantId, " +
                "m.applicantName, " +
                "m.departmentName, " +
                "m.projectId, " +
                "m.projectName, " +
                "m.requestReason, " +
                "m.status as mainStatus, " +
                "m.remark as mainRemark, " +
                "m.createTime as requestCreateTime, " +
                "m.updateTime as requestUpdateTime, " +
                "b.no as itemCode, " +
                "b.name as itemName, " +
                "b.spec as itemSpec, " +
                "b.unit as itemUnit, " +
                "b.inclass as itemInclass";

        String from = "from pl_material_request_detail d " +
                "inner join pl_material_request_main m on d.requestId = m.id " +
                "left join basitem b on d.materialId = b.id " +
                "left join ( " +
                "    select matDetailId, COALESCE(sum(usedQty), 0) as usedQty " +
                "    from pl_reporder_material " +
                "    group by matDetailId " +
                ") u on u.matDetailId = d.id " +
                "where m.applicantId = ? and COALESCE(d.actualQty, 0) > 0 " +
                "order by d.id desc";

        return Db.paginate(pageNumber, pageSize, select, from, userId);
    }

    public PlMaterialRequestMain findById(Long id) {
        return dao.findById(id);
    }

    /**
     * 查找领料单及其明细（关联物料表和出库分配表）
     */
    public Map<String, Object> findWithDetails(Long requestId) {
        // 1. 查找主表
        PlMaterialRequestMain main = dao.findById(requestId);
        if (main == null) {
            return null;
        }

        // 2. 查找明细表并关联 basitem 表（保留这段高级查询）
        String detailSql = "select d.*, " +
                "b.no as itemCode, " +
                "b.name as itemName, " +
                "b.spec as itemSpec, " +
                "b.unit as itemUnit, " +
                "b.inclass as itemInclass " +
                "from pl_material_request_detail d " +
                "left join basitem b on d.materialId = b.id " +
                "where d.requestId = ? " +
                "order by d.id";

        List<Record> details = Db.find(detailSql, requestId);

        // 3. 为每个明细查询关联的出库记录（闭环溯源关键）
        for (Record detail : details) {
            Long detailId = detail.getLong("id");

            String allocationSql = "select a.*, " +
                    "l.inspOrderNo, " +
                    "l.warehouse, " +
                    "l.supplierName, " +
                    "l.type " +
                    "from pl_material_request_allocation a " +
                    "left join pl_mat_inout_list l on a.outboundId = l.id " +
                    "where a.detailId = ?";

            List<Record> allocations = Db.find(allocationSql, detailId);
            detail.set("allocations", allocations);
        }

        // 4. 直接组装返回即可，不要在下面重新定义变量
        Map<String, Object> result = new HashMap<>();
        result.put("main", main);
        result.put("details", details);
        return result;
    }

    /**
     * 接口 6：编辑领料单（全量更新主表及明细）
     */
    public boolean updateWithDetails(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);
        JSONObject mainData = jsonObject.getJSONObject("main");
        JSONArray detailArray = jsonObject.getJSONArray("details");
        Long requestId = mainData.getLong("id");

        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                try {
                    // 1. 更新主表信息
                    PlMaterialRequestMain main = dao.findById(requestId);
                    if (main == null) throw new RuntimeException("领料单不存在");

                    // 只有草稿(0)或审核拒绝(12)才能编辑，根据你的状态定义来
                    // if (main.getStatus() != 0 && main.getStatus() != 12) throw new RuntimeException("当前状态不可编辑");

                    main.setRequestReason(mainData.getString("requestReason"));
                    main.setRemark(mainData.getString("remark"));
                    main.setUpdateTime(new Date());
                    if (!main.update()) throw new RuntimeException("更新主表失败");

                    // 2. 删除原有明细（先清空，再重建，保证数据一致性）
                    Db.update("DELETE FROM pl_material_request_detail WHERE requestId = ?", requestId);

                    // 3. 重新插入明细
                    if (detailArray != null && !detailArray.isEmpty()) {
                        for (int i = 0; i < detailArray.size(); i++) {
                            JSONObject dJson = detailArray.getJSONObject(i);
                            PlMaterialRequestDetail detail = new PlMaterialRequestDetail();

                            detail.setRequestId(requestId);
                            detail.setMaterialId(dJson.getLong("materialId"));
                            detail.setRequestQty(dJson.getBigDecimal("requestQty"));
                            detail.setApprovedQty(BigDecimal.ZERO);
                            detail.setActualQty(BigDecimal.ZERO);
                            detail.setDetailStatus(0); // 重置为待审核
                            detail.setCreateTime(new Date());
                            detail.setUpdateTime(new Date());
                            detail.setRemark(dJson.getString("remark"));

                            if (!detail.save()) throw new RuntimeException("更新明细失败");
                        }
                    }
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
    }



    /**
     * 保存领料单及其明细---
     */
    public Map<String, Object> saveWithDetails(String jsonData) {
        JSONObject jsonObject = JSON.parseObject(jsonData);

        // 获取主表数据
        JSONObject mainData = jsonObject.getJSONObject("main");
        JSONArray detailArray = jsonObject.getJSONArray("details");

        // 创建主表记录
        PlMaterialRequestMain main = new PlMaterialRequestMain();

        // 设置基本字段
        main.setRequestNo(mainData.getString("requestNo"));
        main.setApplicantId(mainData.getLong("applicantId"));
        main.setApplicantName(mainData.getString("applicantName"));
        main.setDepartmentName(mainData.getString("departmentName"));
        main.setProjectId(mainData.getLong("projectId"));
        main.setProjectName(mainData.getString("projectName"));
        main.setRequestReason(mainData.getString("requestReason"));
        main.setKeeperId(mainData.getLong("keeperId"));
        main.setKeeperName(mainData.getString("keeperName"));
        main.setStatus(0); // 默认待审核状态
        main.setRemark(mainData.getString("remark"));
        main.setCreateTime(new Date());
        main.setUpdateTime(new Date());
        main.setCreateBy(mainData.getString("createBy"));

        // 保存主表
        if (!main.save()) {
            throw new RuntimeException("保存领料单主表失败");
        }

        Long requestId = main.getId();
        List<Long> detailIds = new ArrayList<>();

        // 保存明细表
        if (detailArray != null && detailArray.size() > 0) {
            for (int i = 0; i < detailArray.size(); i++) {
                JSONObject detailData = detailArray.getJSONObject(i);

                PlMaterialRequestDetail detail = new PlMaterialRequestDetail();
                detail.setRequestId(requestId);
                detail.setMaterialId(detailData.getLong("materialId"));
                detail.setRequestQty(detailData.getBigDecimal("requestQty"));
                detail.setApprovedQty(BigDecimal.ZERO); // 初始为0，审核时填写
                detail.setActualQty(BigDecimal.ZERO); // 初始为0，发放时填写
                detail.setDetailStatus(0); // 初始待审核状态
                detail.setRemark(detailData.getString("remark"));
                detail.setCreateTime(new Date());
                detail.setUpdateTime(new Date());

                if (!detail.save()) {
                    throw new RuntimeException("保存领料单明细失败");
                }
                detailIds.add(detail.getId());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("requestId", requestId);
        result.put("detailIds", detailIds);
        result.put("detailCount", detailIds.size());

        return result;
    }

    /**
     * 更新领料单主表----
     */
    public boolean update(PlMaterialRequestMain main) {
        main.setUpdateTime(new Date());
        return main.update();
    }

    /**
     * 删除领料单及其明细---只有未配货的才能删
     */
    public boolean deleteWithDetails(Long requestId) {
        // 先删除明细
        int deletedDetails = Db.update("delete from pl_material_request_detail where requestId = ?", requestId);

        // 再删除主表
        PlMaterialRequestMain main = dao.findById(requestId);
        if (main != null) {
            return main.delete();
        }
        return false;
    }

    /**
     * 更新领料单状态-----暂时不用
     */
    public boolean updateStatus(Long requestId, Integer status, String remark) {
        PlMaterialRequestMain main = dao.findById(requestId);
        if (main == null) {
            return false;
        }

        main.setStatus(status);
        if (StrKit.notBlank(remark)) {
            main.setRemark(remark);
        }
        main.setUpdateTime(new Date());

        // 如果是审核状态，设置审核时间
        if (status == 1 || status == 4) {
            main.setApproveTime(new Date());
        }

        return main.update();
    }

    /**
     * 批量更新状态-----暂时不用
     */
    public int batchUpdateStatus(String[] ids, Integer status, String remark) {
        int count = 0;
        for (String idStr : ids) {
            try {
                Long requestId = Long.parseLong(idStr.trim());
                if (updateStatus(requestId, status, remark)) {
                    count++;
                }
            } catch (NumberFormatException e) {
                continue;
            }
        }
        return count;
    }

    /**
     * 审核领料单
     */
    public boolean reviewRequest(Long requestId, Integer status, String approvedQty,
                                 String keeperComment, Integer userId, String userName) {
        // 更新主表状态

        PlMaterialRequestMain main = dao.findById(requestId);
        if (main == null) {
            return false;
        }

        main.setStatus(status);
        main.setKeeperId(Long.valueOf(userId));
        main.setKeeperName(userName);
        main.setApproveTime(new Date());
        if (StrKit.notBlank(keeperComment)) {
            main.setRemark(keeperComment);
        }
        main.setUpdateTime(new Date());

        // 如果主表更新失败，直接返回
        if (!main.update()) {
            return false;
        }

        return true;
    }


    /**
     * 获取待审核领料单列表（库管员视角）---
     */
    public Page<PlMaterialRequestMain> getPendingRequestList(Integer keeperId, int pageNumber, int pageSize) {
        String select = "select *";
        String from = "from pl_material_request_main where status in (0, 1) order by id desc";

        return dao.paginate(pageNumber, pageSize, select, from);
    }

    /**
     * 根据领料单ID查找明细
     */
    public List<PlMaterialRequestDetail> findDetailsByRequestId(Long requestId) {
        return detailDao.find("select * from pl_material_request_detail where requestId = ? order by id", requestId);
    }

    /**
     * 根据物料ID查询库存批次信息（为核心）---
     * 为某个领料项寻找"货源"
     */
    public List<Record> findStockBatchesByItemId(Long itemId) {
        String sql = "SELECT m.* " +
                "FROM pl_mat_inout_list m " +
                "LEFT JOIN pl_insp_order i on i.orderNo = m.inspOrderNo " +
                "WHERE i.itemId = ? AND m.type = 1 " +
                "ORDER BY m.operateTime ASC ";        // 默认先进先出

        List<Record> records = Db.find(sql, itemId);

        return records;
    }


    private static PlMatInoutList matDao = new PlMatInoutList().dao();
    /**
     * 执行配货出库（核心逻辑）---
     * @param detailId 领料明细ID
     * @param dispatchData 批次分配数据 JSON 字符串 [{"inboundId":1, "qty":10}]
     * @param userName 操作人姓名
     */
    public Map<String, Object> executeDispatch(Long detailId, String dispatchData, String userName) {
        final Map<String, Object> result = new HashMap<>();

        // 使用 IAtom 确保事务安全
        boolean success = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                try {
                    // 1. 获取领料明细，校验是否存在
                    PlMaterialRequestDetail detail = detailDao.findById(detailId);
                    if (detail == null) throw new RuntimeException("领料明细不存在");

                    JSONArray dispatchList = JSON.parseArray(dispatchData);
                    //查看前端传过来的配货数据，两个值，一个inboundId也就是入库记录ID，一个qty也就是数量，说明从哪条入记录里面出
                    if (dispatchList == null || dispatchList.isEmpty()) throw new RuntimeException("请选择配货批次");

                    BigDecimal totalQty = BigDecimal.ZERO;//初始化总数

                    // 2. 遍历每一条分配批次--因为是支持批量配货的，就是这一个物料我可以选择多个入库记录，每个记录不同数量，共同组成这个领料项目的出库单
                    for (int i = 0; i < dispatchList.size(); i++) {
                        JSONObject item = dispatchList.getJSONObject(i);
                        Long inboundId = item.getLong("inboundId"); // 入库记录ID
                        BigDecimal qty = item.getBigDecimal("qty"); // 本次分配数量

                        // a. 锁定并获取原入库记录（FOR UPDATE 防止并发扣减产生负数）
                        PlMatInoutList inbound = matDao.findFirst("SELECT * FROM pl_mat_inout_list WHERE id = ? AND type = 1 FOR UPDATE", inboundId);
                        if (inbound == null) throw new RuntimeException("未找到对应的入库记录");

                        BigDecimal remaining = inbound.getRemainingQuantity();
                        if (remaining == null || remaining.compareTo(qty) < 0) {
                            throw new RuntimeException("批次 [" + inbound.getBatchNo() + "] 库存不足，剩余: " + remaining);
                        }

                        // b. 创建出库记录，复用入库记录的所有物料/批次/检验信息
                        PlMatInoutList outbound = new PlMatInoutList();

                        // 核心关联字段
                        outbound.setParentId(inboundId);         // 关联原始入库ID
                        outbound.setType((byte) 2);                    // 2: 出库
                        outbound.setActualQuantity(qty.negate());// 数量记为负数
                        outbound.setRemainingQuantity(BigDecimal.ZERO); // 出库记录本身无剩余量
                        outbound.setOperateTime(new Date());//操作时间
                        outbound.setWriter(userName);//操作人
//                        outbound.setTerm();//期间，暂时不用
                        // 字段复用（闭环追溯关键）
                        outbound.setInspOrderNo(inbound.getInspOrderNo());//检验单号
                        outbound.setWarehouse(inbound.getWarehouse());//仓库
                        outbound.setContractNo(inbound.getContractNo());
                        outbound.setContractName(inbound.getContractName());
                        outbound.setSupplierName(inbound.getSupplierName());//供应商
                        outbound.setMaterialCode(inbound.getMaterialCode());
                        outbound.setMaterialName(inbound.getMaterialName());
                        outbound.setMaterialSpec(inbound.getMaterialSpec());
                        outbound.setMaterialUnit(inbound.getMaterialUnit());
                        outbound.setBatchNo(inbound.getBatchNo());//炉批号
                        outbound.setInclass(inbound.getInclass());//分类
                        outbound.setMaterial(inbound.getMaterial());
//                        outbound.setPrice(inbound.getPrice());

                        if (!outbound.save()) throw new RuntimeException("保存出库明细失败");

                        // c. 回写更新入库记录的剩余数量
                        inbound.setRemainingQuantity(remaining.subtract(qty));
                        if (!inbound.update()) throw new RuntimeException("更新库存剩余量失败");

                        // d. 在分配表中建立【领料明细 - 出库记录】的关联
                        Record allocation = new Record();
                        allocation.set("detailId", detailId);
                        allocation.set("outboundId", outbound.getId());
                        allocation.set("quantity", qty);
                        allocation.set("createTime", new Date());
                        Db.save("pl_material_request_allocation", allocation);

                        totalQty = totalQty.add(qty);
                    }

                    detail.setDetailStatus(1);//设置为待领取状态，因为刚配货肯定是待领取
                    detail.update();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("error", e.getMessage());
                    return false;
                }
            }
        });

        if (!success) throw new RuntimeException((String) result.get("error"));
        return result;
    }

    private void updateMainStatus(Long requestId) {
        // 修正1：SQL 必须按 requestId 查询，而不是 id
        List<Record> details = Db.find("SELECT detailStatus FROM pl_material_request_detail WHERE requestId = ?", requestId);
        if (details.isEmpty()) return;

        // 修正2：判断是否所有明细都已领完 (状态2)
        boolean allFinished = details.stream().allMatch(d -> d.getInt("detailStatus") == 2);

        // 修正3：只要存在已领(2)或部分领(3)的明细，主单就进入“21-部分领取/处理中”
        boolean anyStarted = details.stream().anyMatch(d -> {
            Integer s = d.getInt("detailStatus");
            return s == 2 || s == 3;
        });

        int mainStatus;
        if (allFinished) {
            mainStatus = 20; // 已完成
        } else if (anyStarted) {
            mainStatus = 21; // 部分领取/配货中
        } else {
            // 如果都没开始领，保持“待领取/已审核”状态
            // 建议这里根据你之前的定义，待配货状态应该是 11
            mainStatus = 11;
        }

        Db.update("UPDATE pl_material_request_main SET status = ?, updateTime = ? WHERE id = ?", mainStatus, new Date(), requestId);
    }



    /**
     * 撤销配货记录（反向操作）
     * @param allocationId 关联表ID（即 pl_material_request_allocation 的主键）
     */
    public boolean cancelDispatch(Long allocationId, String userName) {
        return Db.tx(() -> {
            // 1. 查找关联记录
            PlMaterialRequestAllocation allocation = allocDao.findById(allocationId);
            if (allocation == null) {
                throw new RuntimeException("未找到配货关联记录");
            }

            Long outboundId = allocation.getOutboundId();
            Long detailId = allocation.getDetailId();
            BigDecimal qtyToReturn = allocation.getQuantity(); // 配货时存入的数量

            // 2. 查找对应的出库记录，获取 parentId (即原入库批次ID)
            PlMatInoutList outbound = matDao.findById(outboundId);
            if (outbound == null || outbound.getType() != 2) {
                throw new RuntimeException("未找到对应的出库明细");
            }
            Long inboundId = outbound.getParentId();//拿到该出库记录对应的入库记录的id

            // 3. 回滚库存：将数量加回原入库批次的 remainingQuantity
            // 注意：使用 SQL 直接累加更安全，防止并发冲突
            String updateInboundSql = "UPDATE pl_mat_inout_list SET remainingQuantity = remainingQuantity + ? WHERE id = ? AND type = 1";
            int updated = Db.update(updateInboundSql, qtyToReturn, inboundId);
            if (updated == 0) {
                throw new RuntimeException("回滚入库库存失败，原入库记录可能已被删除");
            }

            // 5. 物理删除出库关联领料表记录
            allocation.delete();
            // 6. 出库记录处理：建议逻辑删除或标记作废，不要直接物理删除，保留审计痕迹，目前是直接物理删除
//            outbound.setMemo("撤销配货回滚：" + (outbound.getMemo() == null ? "" : outbound.getMemo()));
//            outbound.setActualQuantity(BigDecimal.ZERO); // 数量归零
            return outbound.delete();// 删除

        });
    }

    public boolean deleteDetail(Long detailId) {
        return detailDao.deleteById(detailId);
    }


    /**
     * 获取配货记录---
     * @param detailId 明细ID
     */
    public List<Record> getDispatchRecords(Long detailId) {
        /**
         * 优化点：
         * 1. 字段明确化：不要使用 i.*，避免带出不必要的重叠字段（如 id, createTime）。
         * 2. 关联增强：直接把“中间表”记录的分配数量(quantity)带出来，因为出库表的 actualQuantity 是负数且包含其他业务逻辑。
         * 3. 溯源扩展：关联查询出对应的入库批次号(batchNo)，方便前端直接展示。
         */
        String sql = "SELECT " +
                "m.id as allocationId, m.quantity as allocatedQty, m.createTime as dispatchTime,m.status," +
                "i.id as outboundId, i.batchNo, i.warehouse, i.materialCode, i.materialName, " +
                "i.materialSpec, i.materialUnit, i.price, i.inspOrderNo, i.parentId as inboundId " +
                "FROM pl_material_request_allocation m " +
                "INNER JOIN pl_mat_inout_list i ON m.outboundId = i.id " +
                "WHERE m.detailId = ? " +
                "ORDER BY m.createTime DESC";

        return Db.find(sql, detailId);
    }


    /**
     * 接口 8：获取领料单完整明细及其出库配货记录
     * 实现：领料单 -> 领料明细 -> (关联表) -> 出库记录
     */
    public List<Record> getDetailsWithAllocations(Long requestId) {
        // 1. 首先查出该领料单下的所有明细，并关联物料基础信息
        String detailSql = "SELECT d.*, m.applicantName," +
                "b.no as itemCode, b.name as itemName, b.spec as itemSpec, b.unit as itemUnit " +
                "FROM pl_material_request_detail d " +
                "LEFT JOIN basitem b ON d.materialId = b.id " +
                "LEFT JOIN pl_material_request_main m ON d.requestId = m.id " +
                "WHERE d.requestId = ? " +
                "ORDER BY d.id ASC";

        List<Record> details = Db.find(detailSql, requestId);

        // 2. 遍历明细，为每一行明细抓取它背后的出库记录
        for (Record detail : details) {
            Long detailId = detail.getLong("id");

            // 这里的 SQL 关联了中间表 m 和出入库记录表 i
            // 从而实现：明细 -> 出库记录 的追踪
            String allocationSql = "SELECT " +
                    "m.id as allocationId, m.quantity as allocatedQty, m.createTime as dispatchTime,m.status,m.confirmTime," +
                    "i.batchNo, i.warehouse, i.inspOrderNo, i.supplierName, i.writer " +
                    "FROM pl_material_request_allocation m " +
                    "INNER JOIN pl_mat_inout_list i ON m.outboundId = i.id " +
                    "WHERE m.detailId = ? " +
                    "ORDER BY m.createTime DESC";

            List<Record> allocations = Db.find(allocationSql, detailId);

            // 将配货记录列表塞进明细对象的 allocations 字段中
            detail.set("allocations", allocations);
        }

        return details;
    }

    /**
     * 确认领取
     *
     * 业务规则：
     * 1. 把当前出库分配记录 allocation 状态改成“已领取”(status=1)
     * 2. 把对应明细 detail 的 actualQty 累加本次领取数量
     * 3. 根据明细 actualQty 和 requestQty 比较，更新明细状态：
     *    - actualQty == requestQty：全部领取（2）
     *    - actualQty < requestQty：部分领取（3）
     * 4. 再根据整张申请单的所有明细状态，更新主单状态：
     *    - 所有明细都是“全部领取(2)”：主单状态=20（全部领取）
     *    - 只要存在已领取/部分领取（2或3），但不是全部都领取完：主单状态=21（部分领取）
     */
    public boolean confirmReceive(int id) {
        // 当前时间，作为更新时间/确认时间
        DateTime now = DateTime.now();

        // =========================
        // 1. 查询出库分配记录 allocation
        // =========================
        PlMaterialRequestAllocation allocation = allocDao.findById(id);
        if (allocation == null) {
            // 分配记录不存在，直接返回 false
            return false;
        }

        // 如果这条记录已经确认领取过了，就不允许重复确认
        if (allocation.getStatus() != null && allocation.getStatus() == 1) {
            return false;
        }

        // 本次领取数量，防止空指针，默认按 0 处理
        BigDecimal quantity = allocation.getQuantity() == null
                ? BigDecimal.ZERO
                : allocation.getQuantity();

        // =========================
        // 2. 查询对应的申请明细 detail
        // =========================
        Long detailId = allocation.getDetailId();
        PlMaterialRequestDetail detail = detailDao.findById(detailId);
        if (detail == null) {
            // 明细不存在，无法继续
            return false;
        }

        // 已领取数量 actualQty，防止空指针
        BigDecimal actualQty = detail.getActualQty() == null
                ? BigDecimal.ZERO
                : detail.getActualQty();

        // 申请数量 requestQty，防止空指针
        BigDecimal requestQty = detail.getRequestQty() == null
                ? BigDecimal.ZERO
                : detail.getRequestQty();

        // 计算确认本次领取后的新实际领取数量
        BigDecimal newActualQty = actualQty.add(quantity);

        // =========================
        // 3. 校验：不能超领
        // =========================
        // 如果确认后的实际领取数量 > 申请数量，说明超领，直接返回 false
        if (newActualQty.compareTo(requestQty) > 0) {
            return false;
        }

        // =========================
        // 4. 更新明细 detail
        // =========================
        // 更新实际领取数量
        detail.setActualQty(newActualQty);

        // 根据领取情况设置明细状态
        if (newActualQty.compareTo(requestQty) >= 0) {
            // 实际领取数量 >= 申请数量，说明已全部领取
            detail.setDetailStatus(2); // 2 = 全部领取
        } else {
            // 实际领取数量 < 申请数量，说明只领取了一部分
            detail.setDetailStatus(3); // 3 = 部分领取
        }

        // 更新时间
        detail.setUpdateTime(now);

        // 把明细更新到数据库
        if (!detail.update()) {
            return false;
        }

        // =========================
        // 5. 查询主单 main
        // =========================
        Long mainId = detail.getRequestId();
        PlMaterialRequestMain main = dao.findById(mainId);
        if (main == null) {
            return false;
        }

        // =========================
        // 6. 重新查询当前主单下的所有明细
        //    用于重新判断主单状态
        // =========================
        List<PlMaterialRequestDetail> details = findDetailsByRequestId(mainId);

        // 是否全部领取
        boolean allReceived = true;

        // 是否至少有一个明细已经发生领取（全部领取/部分领取）
        boolean anyReceived = false;

        for (PlMaterialRequestDetail d : details) {
            Integer status = d.getDetailStatus();

            // 只要有一个明细状态是 2 或 3，说明整单已经开始领取了
            if (status != null && (status == 2 || status == 3)) {
                anyReceived = true;
            }

            // 只要有一个明细不是“全部领取(2)”，那就不能算整单全部领取
            if (status == null || status != 2) {
                allReceived = false;
            }
        }

        // =========================
        // 7. 更新主单状态
        // =========================
        if (allReceived) {
            // 所有明细都是“全部领取”
            main.setStatus(20); // 20 = 全部领取
        } else if (anyReceived) {
            // 至少有一条领取过，但还没全部领完
            main.setStatus(21); // 21 = 部分领取
        }

        // 更新时间
        main.setUpdateTime(now);

        // 更新主单
        if (!main.update()) {
            return false;
        }

        // =========================
        // 8. 更新 allocation 状态为已领取
        // =========================
        allocation.setStatus(1);       // 1 = 已领取
        allocation.setConfirmTime(now);

        // 返回是否更新成功
        return allocation.update();
    }

}
