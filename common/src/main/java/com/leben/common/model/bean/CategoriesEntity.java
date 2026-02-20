package com.leben.common.model.bean;

import com.leben.base.widget.linkage.ILinkageCategory;

import java.io.Serializable;

public class CategoriesEntity implements ILinkageCategory, Serializable {

    private Long id;
    private String name;
    private String icon;
    private Integer sort;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public Integer getSort() {
        return sort;
    }

    @Override
    public String getCategoryName() {
        return getName();
    }

}
