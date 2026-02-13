package com.leben.shop.model.bean;

import java.math.BigDecimal;

public class DrinkQueryEntity {
    // 搜索关键词 (模糊查询)
    private String name;

    // 店铺ID (精确查询)
    private Long shopId;

    // 分类ID (精确查询)
    private Long categoryId;

    // 最低价 (范围查询)
    private java.math.BigDecimal minPrice;

    // 最高价
    private java.math.BigDecimal maxPrice;

    // 是否只看上架 (默认看上架)
    private Integer status = 1;

    private Double userLat;
    private Double userLon;

    public DrinkQueryEntity(){

    }

    public DrinkQueryEntity(String name, Long shopId, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Integer status, Double userLat, Double userLon) {
        this.name = name;
        this.shopId = shopId;
        this.categoryId = categoryId;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.status = status;
        this.userLat = userLat;
        this.userLon = userLon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Double getUserLat() {
        return userLat;
    }

    public void setUserLat(Double userLat) {
        this.userLat = userLat;
    }

    public Double getUserLon() {
        return userLon;
    }

    public void setUserLon(Double userLon) {
        this.userLon = userLon;
    }
}
