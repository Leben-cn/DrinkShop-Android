package com.leben.merchant.model.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class DrinkRequestEntity implements Serializable {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal packingFee;
    private Integer stock;
    private String img;
    private Long categoryId;
    private Long shopCategoryId;
    private Integer status;       // 1:上架, 2:下架
    private List<SpecOptionEntity> specs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPackingFee() {
        return packingFee;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getShopCategoryId() {
        return shopCategoryId;
    }

    public void setShopCategoryId(Long shopCategoryId) {
        this.shopCategoryId = shopCategoryId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<SpecOptionEntity> getSpecs() {
        return specs;
    }

    public void setSpecs(List<SpecOptionEntity> specs) {
        this.specs = specs;
    }
}
