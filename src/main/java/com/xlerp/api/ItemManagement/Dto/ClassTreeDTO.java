package com.xlerp.api.ItemManagement.Dto;

import com.xlerp.common.model.BasItemClass;
import java.util.ArrayList;
import java.util.List;

/**
 * 参考 CreateStdDTO 格式：嵌套实体类 + 子节点列表
 */
public class ClassTreeDTO {
    // 嵌套原实体类（对应 CreateStdDTO 的 std 字段）
    private BasItemClass itemClass;
    // 子节点列表（对应 CreateStdDTO 的 items 字段）
    private List<ClassTreeDTO> children;

    // 无参构造（必须，JFinal 反射赋值用）
    public ClassTreeDTO() {
    }

    // 有参构造（从原实体类创建 DTO）
    public ClassTreeDTO(BasItemClass itemClass) {
        this.itemClass = itemClass;
        this.children = new ArrayList<>(); // 初始化子节点列表
    }

    // 全 getter/setter（必须，序列化和反射都需要）
    public BasItemClass getItemClass() {
        return itemClass;
    }

    public void setItemClass(BasItemClass itemClass) {
        this.itemClass = itemClass;
    }

    public List<ClassTreeDTO> getChildren() {
        return children;
    }

    public void setChildren(List<ClassTreeDTO> children) {
        this.children = children;
    }
}