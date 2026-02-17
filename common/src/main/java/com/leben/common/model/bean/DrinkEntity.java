package com.leben.common.model.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DrinkEntity implements Serializable {

    private Long id;

    private String name;

    private BigDecimal price;

    private String description;

    private String img;

    private Double mark;

    private Integer salesVolume;

    private String createTime;

    private Integer status;
    //平台大分类
    private CategoriesEntity categories;
    //店铺自定义分类
    private ShopCategoriesEntity shopCategories;

    private String distance;

    public List<SpecOptionEntity> specs;

    private BigDecimal packingFee;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getImg() {
        return img;
    }

    public Double getMark() {
        return mark;
    }

    public Integer getSalesVolume() {
        return salesVolume;
    }

    public String getCreateTime() {
        return createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public CategoriesEntity getCategories() {
        return categories;
    }

    public ShopCategoriesEntity getShopCategories() {
        return shopCategories;
    }

    public String getDistance() {
        return distance;
    }

    @SerializedName("specs")
    public List<SpecOptionEntity> getSpecs() {
        // 如果是 null 就返回一个空的 List，防止崩溃
        return Objects.requireNonNullElseGet(specs, ArrayList::new);
    }


    public BigDecimal getPackingFee() {
        return packingFee;
    }
}
