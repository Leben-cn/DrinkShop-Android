package com.leben.common.model.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long productId;       // 商品ID
    private String productName;   // 商品名称 (快照)
    private String productImg;    // 商品图片 (快照)
    private int quantity;         // 购买数量

    // 【关键】必须存下单时的单价（含规格加价后的价格）
    // 防止后续商家改价导致订单金额对不上
    private BigDecimal price;

    // 【关键】规格信息
    private String specDesc;      // 规格描述文本 (如 "少冰, 半糖")
    // 如果需要“再来一单”功能，最好把规格ID也存下来，例如 "101,205"
    private String specIds;

    // 构造函数、Getters & Setters
    public OrderItemEntity() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSpecDesc() {
        return specDesc;
    }

    public void setSpecDesc(String specDesc) {
        this.specDesc = specDesc;
    }

    public String getSpecIds() {
        return specIds;
    }

    public void setSpecIds(String specIds) {
        this.specIds = specIds;
    }
}