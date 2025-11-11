package com.xlerp.api.ItemManagement.Service;

import com.xlerp.common.model.BasItemRelation;
import com.xlerp.common.model.Basitem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BasItemRelationService {

    private final BasItemRelation relationDao = new BasItemRelation().dao();
    private static final Basitem basitemDao = new Basitem().dao();

    public boolean deleteById(Integer id) {
        return relationDao.deleteById(id);
    }

    /**
     * 简化版：递归查询物料树（保留核心功能，去掉冗余代码）
     * @param parentItemId 父物料ID
     * @return 树状结构（物料详情 + 子物料列表）
     */
    public Map<String, Object> getMaterialTree(Integer parentItemId) {
        // 1. 查询物料详情 + 直接转Map（用JFinal Model原生方法，避免手动put）
        Basitem parentMaterial = basitemDao.findById(parentItemId);
        if (parentMaterial == null) return null;
        Map<String, Object> parentNode = parentMaterial.toMap(); // 直接转Map，包含所有字段

        // 2. 查询子物料关系（简化SQL，保留核心条件）
        List<BasItemRelation> childRelations = relationDao.find(
                "select * from bas_item_relation where parentItemId = ? ",
                parentItemId
        );

        // 3. 递归组装子节点（简化循环逻辑）
        List<Map<String, Object>> childNodes = new ArrayList<>();
        for (BasItemRelation relation : childRelations) {
            Map<String, Object> childNode = getMaterialTree(relation.getChildItemId());
            if (childNode != null) {
                childNode.put("relationQuantity", relation.getQuantity()); // 仅保留核心用量字段
                childNode.put("relationId", relation.getId());
                childNodes.add(childNode);
            }
        }

        // 4. 添加子节点列表（确保children字段存在，前端解析友好）
        parentNode.put("children", childNodes);

        return parentNode;
    }

}
