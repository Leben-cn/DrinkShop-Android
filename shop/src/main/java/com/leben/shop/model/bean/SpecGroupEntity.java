package com.leben.shop.model.bean;

import java.io.Serializable;
import java.util.List;

public class SpecGroupEntity implements Serializable {

    public long id;
    public String groupName; // "温度"
    public boolean isMultiple; // true=多选
    public List<SpecOptionEntity> options; // 该组下的选项列表

    public long getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public List<SpecOptionEntity> getOptions() {
        return options;
    }
}
