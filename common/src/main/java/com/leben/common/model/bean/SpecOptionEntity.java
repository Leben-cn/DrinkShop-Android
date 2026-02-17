package com.leben.common.model.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 对应后端的 DrinkSpecItemResponse
 * 这是一个"扁平"的对象，包含选项信息 + 分组信息
 */
public class SpecOptionEntity implements Serializable {

    // ============================
    // 1. 选项信息 (对应后端字段)
    // ============================
    // 注意：变量名最好和后端 JSON 字段完全一致，或者使用 @SerializedName
    public long optionId;       // 后端叫 optionId
    public String optionName;   // 后端叫 optionName
    public BigDecimal price;    // 加价金额

    // ============================
    // 2. 分组信息 (对应后端字段)
    // ============================
    public long groupId;        // 分组ID
    public String groupName;    // 分组名 (如: 温度)
    public int isMultiple;      // 是否多选 (1=是, 0=否)
    public int sortOrder;       // 排序字段

    // ============================
    // 3. 本地状态 (前端逻辑用)
    // ============================
    public boolean isSelected;  // 是否被选中

    // Getters (为了兼容你的 Adapter 代码，可以保留 get/set)
    public long getId() { return optionId; }
    public String getName() { return optionName; } // Adapter 可能还在用 getName
    public BigDecimal getPrice() { return price; }
    public String getGroupName() { return groupName; }
    public int getIsMultiple() { return isMultiple; }
    public long getGroupId() { return groupId; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

}