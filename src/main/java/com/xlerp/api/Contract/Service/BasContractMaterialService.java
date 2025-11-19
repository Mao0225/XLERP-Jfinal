package com.xlerp.api.Contract.Service;

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.xlerp.common.model.BasContractMaterial;
import com.xlerp.common.model.BasItemRelation;
import com.xlerp.common.model.Basitem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasContractMaterialService {

    private BasContractMaterial materialDao = new BasContractMaterial();
    private BasItemRelation relationDao = new BasItemRelation();
    private Basitem basitemDao = new Basitem();
    public List getMaterialList(String contractNo) {


        String select = "SELECT cm.*,i.no as itemNo,i.name as itemName,i.spec as itemSpec,i.inclass,i.unit ";
        String from = "FROM bas_contract_material cm " +
                "LEFT JOIN basitem i ON cm.itemId = i.id " +
                "WHERE cm.contractNo = ? " +
                "ORDER BY cm.id";

        // 拼接完整的SQL语句
        String sql = select + from;
        return Db.find(sql, contractNo);
    }


    //获取合同所有产品列表
    public List<Record> getContractItemByNo(String contractNo) {
        String sql = "SELECT c.*, i.no AS itemNo, i.name AS itemName, i.spec AS itemSpec, i.drawing_standard_no AS tuzhiNo,psp.scheduleCode "+
                "FROM bascontractitem c " +
                "LEFT JOIN basitem i ON c.itemid = i.id " +
                "LEFT JOIN pl_schedule_plan psp ON c.id = psp.poItemId " +
                "WHERE c.no = ? AND c.isdelete = 0 " +
                "ORDER BY c.id";

        List<Object> params = new ArrayList<>();
        params.add(contractNo);

        return Db.find(sql, params.toArray());
    }

    /**
     * 核心方法：通过合同编号查询所有产品的物料清单（仅叶子节点/原材料，按物料编号no合并）
     * 修复：不可变列表改为可变ArrayList，支持add操作
     */
    public List<Map<String, Object>> getContractMaterialLeafListWithMerge(String contractNo) {
        List<com.jfinal.plugin.activerecord.Record> contractItems = getContractItemByNo(contractNo);
        if (contractItems.isEmpty()) {
            return new ArrayList<>();
        }

        Map<String, Map<String, Object>> leafNodeMap = new HashMap<>();

        for (Record contractItem : contractItems) {
            Integer mainItemId = contractItem.getInt("itemid");
            BigDecimal contractItemNum = contractItem.getBigDecimal("itemnum");
            // 合同数量null校验
            if (contractItemNum == null) {
                contractItemNum = BigDecimal.ZERO;
            }
            String itemMemo = contractItem.getStr("itemmemo");
            // 备注null校验
            if (itemMemo == null) {
                itemMemo = "";
            }

            // 新增：获取主产品名称（假设contractItems中存储主产品名称的字段是itemname，根据实际表结构调整！）
            String mainProductName = contractItem.getStr("itemName");
            // 主产品名称null校验，避免后续NPE
            if (mainProductName == null) {
                mainProductName = "未知产品";
            }


            List<Map<String, Object>> productLeafNodes = collectMaterialLeafNodes(mainItemId, contractItemNum);

            for (Map<String, Object> leafNode : productLeafNodes) {
                String materialNo = (String) leafNode.get("no");
                Integer materialId = (Integer) leafNode.get("id");
                // 合并key容错（no为空时用id）
                String mergeKey = (materialNo != null && !materialNo.trim().isEmpty())
                        ? materialNo.trim()
                        : materialId.toString();

                if (leafNodeMap.containsKey(mergeKey)) {
                    // 已存在：累加用量 + 追加所有关联信息（含主产品名称）
                    Map<String, Object> existingNode = leafNodeMap.get(mergeKey);

                    // 累加实际用量
                    BigDecimal existingQty = (BigDecimal) existingNode.get("actualQuantity");
                    BigDecimal currentQty = (BigDecimal) leafNode.get("actualQuantity");
                    existingNode.put("actualQuantity", existingQty.add(currentQty));

                    // 追加合同产品ID
                    List<Integer> contractItemIds = (List<Integer>) existingNode.get("contractItemIds");
                    if (!contractItemIds.contains(contractItem.getInt("id"))) { // 去重逻辑
                        contractItemIds.add(contractItem.getInt("id"));
                    }

                    // 追加合同产品数量
                    List<BigDecimal> contractItemNums = (List<BigDecimal>) existingNode.get("contractItemNums");
                    contractItemNums.add(contractItemNum);

                    // 新增：追加主产品名称
                    List<String> itemNames = (List<String>) existingNode.get("itemNames");
                    if (!itemNames.contains(mainProductName)) { // 去重逻辑
                        itemNames.add(mainProductName);
                    }
                } else {
                    // 不存在：初始化所有可变列表（含itemNames）
                    List<Integer> contractItemIds = new ArrayList<>();
                    contractItemIds.add(contractItem.getInt("id"));

                    List<BigDecimal> contractItemNums = new ArrayList<>();
                    contractItemNums.add(contractItemNum);


                    // 新增：初始化主产品名称列表
                    List<String> itemNames = new ArrayList<>();
                    itemNames.add(mainProductName);

                    // 存入叶子节点（含itemNames）
                    leafNode.put("contractItemIds", contractItemIds);
                    leafNode.put("contractItemNums", contractItemNums);
                    leafNode.put("itemNames", itemNames); // 新增：主产品名称列表
                    leafNodeMap.put(mergeKey, leafNode);
                }
            }
        }

        return new ArrayList<>(leafNodeMap.values());
    }

    /**
     * 递归收集叶子节点（保持不变，仅优化null校验）
     */
    private List<Map<String, Object>> collectMaterialLeafNodes(Integer parentItemId, BigDecimal parentTotalQuantity) {
        List<Map<String, Object>> leafNodes = new ArrayList<>();

        Basitem parentMaterial = basitemDao.findById(parentItemId);
        if (parentMaterial == null) {
            System.out.println("物料ID={} 不存在，跳过"+parentItemId);
            return leafNodes;
        }

        List<BasItemRelation> childRelations = relationDao.find(
                "select * from bas_item_relation where parentItemId = ? ",
                parentItemId
        );

        if (childRelations.isEmpty()) {
            // 叶子节点：封装信息（优化null校验）
            Map<String, Object> leafNode = parentMaterial.toMap();
            // 确保实际用量不为null
            leafNode.put("actualQuantity", parentTotalQuantity != null ? parentTotalQuantity : BigDecimal.ZERO);
            leafNodes.add(leafNode);
        } else {
            for (BasItemRelation relation : childRelations) {
                Integer childItemId = relation.getChildItemId();
                BigDecimal relationQuantity = relation.getQuantity();
                // 修复：关联单量null校验（默认1，避免multiply报错）
                if (relationQuantity == null) {
                    relationQuantity = BigDecimal.ONE;
                    System.out.println("父物料ID={} 对子物料ID={} 的关联单量为null，默认设为1"+parentItemId+childItemId);
                }

                // 计算子物料用量（避免parentTotalQuantity为null）
                BigDecimal childTotalQuantity = (parentTotalQuantity != null ? parentTotalQuantity : BigDecimal.ZERO)
                        .multiply(relationQuantity);

                leafNodes.addAll(collectMaterialLeafNodes(childItemId, childTotalQuantity));
            }
        }

        return leafNodes;
    }

    public boolean deleteById(String id) {
        return materialDao.deleteById(id);
    }

    public boolean deleteByContractNo(String contractNo) {
        return Db.delete("delete from bas_contract_material where contractNo = ?", contractNo) > 0;
    }

    public Page<Record> paginate(int pageNum, int pageSz, String contractNo,int relationStatus) {
        String select = "SELECT cm.*, i.no as itemNo, i.name as itemName, " +
                "i.spec as itemSpec, i.inclass, i.unit ";
        StringBuilder from = new StringBuilder("FROM bas_contract_material cm " +
                "LEFT JOIN basitem i ON cm.itemId = i.id " +
                "WHERE 1=1 ");

        List<Object> paramList = new ArrayList<>();

        // 动态拼接条件：只有参数不为空时才添加
        if (StrKit.notBlank(contractNo)) {
            //模糊查询
            from.append(" AND cm.contractNo LIKE CONCAT('%', ?, '%') ");
            paramList.add(contractNo);

        }

        if (relationStatus == 1) {
            // 添加参数
            from.append(" AND cm.purchaseOrderNo IS NULL ");
        }
        from.append("ORDER BY cm.id DESC");

        // 转换为数组（JFinal 需传入 Object[]）
        Object[] params = paramList.toArray(new Object[0]);

        // 分页查询（注意：select 和 from 分开传更规范，JFinal 推荐写法）
        return Db.paginate(pageNum, pageSz, select, from.toString(), params);
    }

    public Page<Record> paginateForInsp(int pageNum, int pageSz, String contractNo) {
        String select = "SELECT cm.*, i.no as itemNo, i.name as itemName," +
                "i.spec as itemSpec, i.inclass, i.unit," +
                "po.status as orderStatus,po.memo as orderFormMemo,po.writer as orderWriter,po.orderName ";//采购计划主表信息连在后面，名称状态备注
        StringBuilder from = new StringBuilder("FROM bas_contract_material cm " +
                "LEFT JOIN basitem i ON cm.itemId = i.id " +
                "LEFT JOIN pl_purchase_order po ON cm.purchaseOrderNo = po.purchaseOrderNo " +
                "WHERE 1=1 AND cm.purchaseOrderNo IS NOT NULL AND po.status = 30");
        List<Object> paramList = new ArrayList<>();

        // 动态拼接条件：只有参数不为空时才添加
        if (StrKit.notBlank(contractNo)) {
            //模糊查询
            from.append(" AND cm.contractNo LIKE CONCAT('%', ?, '%') ");
            paramList.add(contractNo);

        }
        from.append("ORDER BY cm.id DESC");

        // 转换为数组（JFinal 需传入 Object[]）
        Object[] params = paramList.toArray(new Object[0]);

        // 分页查询（注意：select 和 from 分开传更规范，JFinal 推荐写法）
        return Db.paginate(pageNum, pageSz, select, from.toString(), params);
    }

    public Record getById(String id) {
        String sql = """
        SELECT 
            cm.*,
            i.no AS itemNo,
            i.name AS itemName,
            i.spec AS itemSpec,
            i.inclass,
            i.unit,
            po.status AS orderStatus,
            po.memo AS orderFormMemo,
            po.writer AS orderWriter,
            po.orderName
        FROM bas_contract_material cm
        LEFT JOIN basitem i ON cm.itemId = i.id
        LEFT JOIN pl_purchase_order po ON cm.purchaseOrderNo = po.purchaseOrderNo
        WHERE cm.id = ?
        """;
        return Db.findFirst(sql, id);
    }
}
