// src/com/xlerp/api/PlInspectionController/dto/CreateStdDTO.java
package com.xlerp.api.PlInspectionController.dto;

import com.xlerp.common.model.PlInspStd;
import com.xlerp.common.model.PlInspStdItem;

import java.util.List;

public class CreateStdDTO {
    private PlInspStd std;
    private List<PlInspStdItem> items;

    public CreateStdDTO(PlInspStd std, List<PlInspStdItem> items) {
        this.std = std;
        this.items = items;
    }
    public CreateStdDTO() {
        // 无参构造法
    }
    // 必须有 getter/setter！JFinal 用反射赋值
    public PlInspStd getStd() {
        return std;
    }

    public void setStd(PlInspStd std) {
        this.std = std;
    }

    public List<PlInspStdItem> getItems() {
        return items;
    }

    public void setItems(List<PlInspStdItem> items) {
        this.items = items;
    }
}