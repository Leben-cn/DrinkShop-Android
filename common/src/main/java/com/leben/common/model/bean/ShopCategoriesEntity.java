package com.leben.common.model.bean;

import com.leben.base.widget.linkage.ILinkageCategory;

import java.util.Objects;

public class ShopCategoriesEntity implements ILinkageCategory {

    private Long id;
    private Long shopId;
    private String name;
    private Integer sort;
    private String icon;
    private Boolean isShow;
    private String createTime;
    private String updateTime;

    public Long getId() {
        return id;
    }

    public Long getShopId() {
        return shopId;
    }

    public String getName() {
        return name;
    }

    public Integer getSort() {
        return sort;
    }

    public String getIcon() {
        return icon;
    }

    public Boolean getShow() {
        return isShow;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ShopCategoriesEntity that = (ShopCategoriesEntity) o;
        return Objects.equals(id, that.id); // 只要 ID 一样就认为是同一个分类
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String getCategoryName() {
        return getName();
    }
}
