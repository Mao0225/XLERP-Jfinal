package com.xlerp.api.PlWorkOrder.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.System.Service.BasNoService;
import com.xlerp.common.model.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlWorkOrderService {
    private static final PlWorkOrder dao = new PlWorkOrder();
    private static final BasItemRelation relationDao = new BasItemRelation().dao();
    private static final Basitem basitemDao = new Basitem().dao();
    private static final BasNoService basNoService = new BasNoService();
    private static final BasProcessRoute routeDao = new BasProcessRoute().dao();
    private static final PlReportWorkOrder rwoDao = new PlReportWorkOrder().dao();
    public Page<PlWorkOrder> paginate(int pageNumber, int pageSize,
                                      String contractNo, String contractName, String woNo,
                                      String status) {

        // 1. 简化 Select 部分，移除了复杂的 LISTAGG 子查询
        String select = "select p.*, " +
                "bc.no as contractNo, bc.name as contractName," +
                "bci.itemnum as contractAmount, bi.name as itemName, bci.itemunit as itemUnit," +
                "bci.noticeid, bci.noticedrawno, bci.noticeinstead, bci.noticename, bci.noticeauther," +
                "bci.noticebuilddate, bci.noticecomment, tz.tuzhiurl ";

        // 2. 构建 FROM 和 JOIN (保持原有逻辑，确保关联数据准确)
        StringBuilder from = new StringBuilder("from pl_work_order p ");
        from.append("left join pl_production_order po on po.ipoNo = p.ipoNo ");
        from.append("left join bascontractitem bci on po.poItemId = bci.id ");
        from.append("left join bastuzhi tz on bci.noticetuzhiid = tz.id ");
        from.append("left join bascontract bc on bc.no = bci.no ");
        from.append("left join basitem bi on bci.itemid = bi.id ");
        from.append("where 1 = 1 ");

        List<Object> params = new ArrayList<>();

        // 3. 构建查询条件
        if (contractNo != null && !contractNo.isEmpty()) {
            from.append("and bci.no like ? ");
            params.add("%" + contractNo + "%");
        }

        if (contractName != null && !contractName.isEmpty()) {
            from.append("and bc.name like ? ");
            params.add("%" + contractName + "%");
        }

        if (woNo != null && !woNo.isEmpty()) {
            from.append("and p.woNo like ? ");
            params.add("%" + woNo + "%");
        }

        if (status != null && !status.isEmpty()) {
            from.append("and p.status = ? ");
            params.add(status);
        }

        from.append("order by p.id desc");

        // 4. 执行分页查询
        Page<PlWorkOrder> page = dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());

        // 5. 【新增逻辑】遍历分页结果，根据 itemId 填充工序列表
        // JFinal 的 Page.getList() 返回的是 Model 列表
        for (PlWorkOrder order : page.getList()) {
            // 获取 p.itemId
            Integer itemId = order.getInt("itemId");

            if (itemId != null) {
                // 调用你写好的 getByItemId 方法
                List<BasProcessRoute> routes = getByItemId(itemId);

                // 将查询到的工序列表放入 Model 的额外属性中
                // 前端 JSON 会多出一个 "processRoutes" 字段
                order.put("processRoutes", routes);
            }
        }

        return page;
    }



    public PlWorkOrder findById(int id) {
        return dao.findFirst("select * from pl_work_order where id = ? ", id);
    }

    public boolean save(PlWorkOrder pl_work_order) {
        if (pl_work_order.getWoNo() == null || pl_work_order.getWoNo().isEmpty()) {
            pl_work_order.setWoNo(basNoService.getNewNoNyName("scgd"));
        }
        return pl_work_order.save();
    }

    public boolean update(PlWorkOrder pl_work_order) {
        return pl_work_order.update();
    }

    public boolean DeleteById(int id) {
        return dao.deleteById( id);
    }

    public boolean batchDelete(List<Integer> ids) {

        return dao.deleteByIds(ids);
    }

    public boolean updateStatus(String id, String status) {
        return Db.update("update pl_work_order set status = ? where id = ? ", status, id) > 0;
    }

    public List<PlWorkOrder> findByIpoNo(String ipoNo) {
        return dao.find("select * from pl_work_order where ipoNo = ? ", ipoNo);
    }



    /**
     * 获取扁平化的半成品BOM列表（包含工序信息）
     * @param parentItemId 父物料ID
     * @param planQuantity 父物料计划生产数量（基数）
     * @return 包含父物料及所有半成品子物料的扁平列表
     */
    public List<Map<String, Object>> getMaterialFlatList(Integer parentItemId, Integer planQuantity) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        // 1. 查询顶层父物料详情
        Basitem parentMaterial = basitemDao.findById(parentItemId);
        if (parentMaterial == null) return resultList;

        // 2. 处理顶层父节点（无条件加入列表）
        Map<String, Object> parentNode = parentMaterial.toMap();
        parentNode.put("requiredQuantity", planQuantity);
        parentNode.put("nodeType", "root");

        // 【新增逻辑】查询父物料的工序并放入Map
        List<BasProcessRoute> parentRoutes = getByItemId(parentItemId);
        parentNode.put("processRoutes", parentRoutes);

        resultList.add(parentNode);

        // 3. 开始递归查找并平铺子节点
        recursiveSearch(parentItemId, planQuantity, resultList);

        return resultList;
    }

    /**
     * 递归辅助方法
     * @param parentId 当前父节点ID
     * @param parentTotalQty 当前父节点的总需求数量（用于级联计算）
     * @param resultList 结果集合
     */
    private void recursiveSearch(Integer parentId, Integer parentTotalQty, List<Map<String, Object>> resultList) {
        // 1. 查询当前父节点下的所有直接子关联
        List<BasItemRelation> childRelations = relationDao.find(
                "select * from bas_item_relation where parentItemId = ? ",
                parentId
        );

        if (childRelations == null || childRelations.isEmpty()) {
            return;
        }

        // 2. 遍历子节点
        for (BasItemRelation relation : childRelations) {
            // 获取子物料详情
            Basitem childItem = basitemDao.findById(relation.getChildItemId());
            if (childItem == null) continue;

            // 获取物料分类 (inClass)
            String inClass = childItem.getInclass();

            // 3. 核心筛选逻辑：只有inClass包含"半成品"才处理
            if (inClass != null && inClass.contains("半成品")) {

                // 4. 核心数量计算：父级总数量 * 当前关联用量 = 当前子级总需求量
                BigDecimal relationQtyBig = relation.getQuantity();
                if (relationQtyBig == null) continue; // 无用量则跳过
                Integer relationQty = relationQtyBig.intValue();
                Integer currentTotalQty = parentTotalQty * relationQty;

                // 5. 组装数据
                Map<String, Object> childNode = childItem.toMap();
                childNode.put("requiredQuantity", currentTotalQty); // 计算后的总需求量
                childNode.put("unitQuantity", relationQty);         // 单个父件对应的单耗
                childNode.put("relationId", relation.getId());      // 关系ID
                childNode.put("nodeType", "child");                 // 标记为子节点

                // 【新增逻辑】查询当前子物料(半成品)的工序并放入Map
                List<BasProcessRoute> childRoutes = getByItemId(childItem.getId());
                childNode.put("processRoutes", childRoutes);

                // 加入扁平列表
                resultList.add(childNode);

                // 6. 递归向下
                recursiveSearch(childItem.getId(), currentTotalQty, resultList);
            }
        }
    }

    /**
     * 根据物料ID查询工序列表
     * @param itemId 物料ID
     * @return 工序列表
     */
    public List<BasProcessRoute> getByItemId(int itemId) {
        // 确保 routeDao 已经被注入或在此类中可用
        String sql = "select * from bas_process_route where itemId = ? order by sort asc"; // 建议加上排序
        return routeDao.find(sql, itemId);
    }

    public List<Map<String, Object>> getWorkOrderComplete(String woNo, Integer itemId) {
        // 1. 查询报工单，按工序分组并计算总完成数量
        // 注意：请根据实际数据库字段修改 'qualified_qty' (合格数) 或 'report_qty' (报工数)
        String reportSql = "SELECT processCode, SUM(amount) as totalQty " +
                "FROM pl_report_work_order " +
                "WHERE woNo = ? " +
                "GROUP BY processCode";

        List<PlReportWorkOrder> reportList = rwoDao.find(reportSql, woNo);

        // 2. 将报工数据转换为 Map<ProcessCode, Quantity> 结构，方便后续快速匹配
        // 如果 processCode 可能为空，注意处理 NullPointerException
        Map<String, BigDecimal> completedMap = reportList.stream()
                .collect(Collectors.toMap(
                        r -> r.getStr("processCode"),
                        r -> r.getBigDecimal("totalQty") == null ? BigDecimal.ZERO : r.getBigDecimal("totalQty")
                ));

        // 3. 调用 getByItemId 获取标准的工艺路线列表 (Plan)
        List<BasProcessRoute> routes = getByItemId(itemId);

        // 4. 组装结果数据 (Merge)
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (BasProcessRoute route : routes) {
            // 将 Model 转为 Map，方便添加自定义字段
            // JFinal Model 自带 _getAttrs() 或 toRecord().getColumns() 可以获取属性Map
            Map<String, Object> itemMap = new HashMap<>(route.toRecord().getColumns());

            // 获取当前工序的编号
            String processCode = route.getStr("processCode");

            // 从报工Map中获取对应的完成数量，如果没找到则默认为0
            BigDecimal completedQty = completedMap.getOrDefault(processCode, BigDecimal.ZERO);

            // 将统计结果放入 Map
            itemMap.put("completedQty", completedQty);

            // 可选：计算剩余数量 (假设 route 中有 standardQty 字段)
            // BigDecimal standardQty = route.getBigDecimal("standardQty");
            // itemMap.put("remainQty", standardQty.subtract(completedQty));

            resultList.add(itemMap);
        }

        return resultList;
    }


}