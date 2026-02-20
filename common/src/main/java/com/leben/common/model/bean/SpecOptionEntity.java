package com.leben.common.model.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 对应后端的 DrinkSpecItemResponse
 * 这是一个"扁平"的对象，包含选项信息 + 分组信息
 */
public class SpecOptionEntity implements Serializable {

    public long optionId;
    public String optionName;
    public BigDecimal price;    // 加价金额
    public long groupId;        // 分组ID
    public String groupName;    // 分组名 (如: 温度)
    public int isMultiple;      // 是否多选 (1=是, 0=否)
    public int sortOrder;       // 排序字段
    public boolean isSelected;  // 是否被选中

    public long getOptionId() {
        return optionId;
    }

    public void setOptionId(long optionId) {
        this.optionId = optionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getIsMultiple() {
        return isMultiple;
    }

    public void setIsMultiple(int isMultiple) {
        this.isMultiple = isMultiple;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}