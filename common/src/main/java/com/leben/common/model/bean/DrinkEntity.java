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

    private Integer stock;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setMark(Double mark) {
        this.mark = mark;
    }

    public void setSalesVolume(Integer salesVolume) {
        this.salesVolume = salesVolume;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setCategories(CategoriesEntity categories) {
        this.categories = categories;
    }

    public void setShopCategories(ShopCategoriesEntity shopCategories) {
        this.shopCategories = shopCategories;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setSpecs(List<SpecOptionEntity> specs) {
        this.specs = specs;
    }

    public void setPackingFee(BigDecimal packingFee) {
        this.packingFee = packingFee;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
