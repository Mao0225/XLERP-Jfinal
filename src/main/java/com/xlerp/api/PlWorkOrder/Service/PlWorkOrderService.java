package com.xlerp.api.PlWorkOrder.Service;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.xlerp.api.System.Service.BasNoService;
import com.xlerp.common.model.BasItemRelation;
import com.xlerp.common.model.Basitem;
import com.xlerp.common.model.PlWorkOrder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlWorkOrderService {
    private static final PlWorkOrder dao = new PlWorkOrder();
    private static final BasItemRelation relationDao = new BasItemRelation().dao();
    private static final Basitem basitemDao = new Basitem().dao();
    private static final BasNoService basNoService = new BasNoService();
    public Page<PlWorkOrder> paginate(int pageNumber, int pageSize,
                                      String contractNo, String contractName, String woNo,
                                      String status) {

        // 达梦数据库用 LISTAGG 来拼接报工流程
        String reportAgg =
                "(select LISTAGG(r.processName || '|' || r.workshopName || '|' || r.status || '|' || r.writer, ';') " +
                        "within group(order by r.id) " +
                        "from pl_report_work_order r where r.woNo = p.woNo) as processes";

        // 查询字段
        String select = "select p.*, " + reportAgg + ", " +
                "bc.no as contractNo, bc.name as contractName," +
                "bci.itemnum as contractAmount, bi.name as itemName, bci.itemunit as itemUnit," +
                "bci.noticeid, bci.noticedrawno, bci.noticeinstead, bci.noticename, bci.noticeauther," +
                "bci.noticebuilddate, bci.noticecomment, tz.tuzhiurl ";

        // 构建 FROM
        StringBuilder from = new StringBuilder("from pl_work_order p ");
        from.append("left join pl_production_order po on po.ipoNo = p.ipoNo ");
        from.append("left join bascontractitem bci on po.poItemId = bci.id ");
        from.append("left join bastuzhi tz on bci.noticetuzhiid = tz.id ");
        from.append("left join bascontract bc on bc.no = bci.no ");
        from.append("left join basitem bi on bci.itemid = bi.id ");
        from.append("where 1 = 1 ");

        List<Object> params = new ArrayList<>();

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

        return dao.paginate(pageNumber, pageSize, select, from.toString(), params.toArray());
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
     * 获取扁平化的半成品BOM列表
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
        // 顶层的需求数量就是传入的计划数量
        parentNode.put("requiredQuantity", planQuantity);
        // 标记一下是顶层（可选，方便前端区分）
        parentNode.put("nodeType", "root");
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
                childNode.put("unitQuantity", relationQty);         // 单个父件对应的单耗（保留参考）
                childNode.put("relationId", relation.getId());      // 关系ID
                childNode.put("nodeType", "child");                 // 标记为子节点

                // 加入扁平列表
                resultList.add(childNode);

                // 6. 递归向下：当前的子节点变成了下一级的父节点
                // 重点：传入的数量是刚才计算出来的 currentTotalQty，这样下下级就会基于这个数量继续乘
                recursiveSearch(childItem.getId(), currentTotalQty, resultList);
            }
        }
    }
}