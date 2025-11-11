package com.xlerp.api.ItemManagement.Service;

import com.xlerp.api.ItemManagement.Dto.ClassTreeDTO;
import com.xlerp.common.model.BasItemClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BasItemClassService {
    private static final BasItemClass dao = new BasItemClass();


    public List<BasItemClass> getList(String param) {
        // 基础 SQL（where 1=1 方便后续拼接 and 条件）
        StringBuilder sql = new StringBuilder("select * from bas_item_class where 1=1 ");
        // 存储查询参数（避免 SQL 注入）
        List<Object> params = new ArrayList<>();

        // 判断 param 不为 null 且不为空字符串（trim() 忽略前后空格）
        if (param != null && !param.trim().isEmpty()) {
            // 关键修改：用 AND 拼接，并且用 () 包裹 OR 条件（确保逻辑正确）
            sql.append("AND (classcode like ? OR classname like ?) ");
            // 添加模糊匹配参数（前后加 %）
            String likeParam = "%" + param.trim() + "%";
            params.add(likeParam);
            params.add(likeParam);
        }

        // 拼接排序条件
        sql.append("order by id desc");

        // 执行查询
        return dao.find(sql.toString(), params.toArray());
    }

    public BasItemClass findById(int i) {
        return dao.findById(i);
    }

    public boolean save(BasItemClass basItemClass) {
        return basItemClass.save();
    }

    public boolean update(BasItemClass basItemClass) {
        return basItemClass.update();
    }

    public boolean deleteById(int i) {
        return dao.deleteById(i);
    }



    public List<ClassTreeDTO> getTreeList(String params) {
        // 1. 获取扁平的原实体类列表
        List<BasItemClass> flatList = getList(params);
        // 2. 转换为新格式 DTO，并构建 ID -> DTO 的映射（key 是原实体类的 id）
        Map<Integer, ClassTreeDTO> dtoMap = new HashMap<>();
        List<ClassTreeDTO> dtoList = new ArrayList<>();
        for (BasItemClass item : flatList) {
            ClassTreeDTO dto = new ClassTreeDTO(item); // 用有参构造创建 DTO
            dtoMap.put(item.getId(), dto); // key 是原实体类的 id
            dtoList.add(dto);
        }

        // 3. 构建父子关系
        List<ClassTreeDTO> treeList = new ArrayList<>();
        for (ClassTreeDTO dto : dtoList) {
            // 获取原实体类的 parentId（从嵌套的 itemClass 中取）
            Integer parentId = dto.getItemClass().getParentId();
            // 一级分类：parentId=0
            if (parentId == 0) {
                treeList.add(dto);
            } else {
                // 根据 parentId 查找父 DTO
                ClassTreeDTO parentDto = dtoMap.get(parentId);
                if (parentDto != null) {
                    parentDto.getChildren().add(dto); // 子节点加入父节点的 children
                } else {
                    treeList.add(dto);
                }
            }
        }

        return treeList;
    }
}