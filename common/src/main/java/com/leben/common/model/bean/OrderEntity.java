package com.leben.common.model.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class OrderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    // --- 1. 订单基础信息 ---
    private Long id;              // 数据库主键
    private String orderNo;       // 订单号 (例如: 2026020412345678)
    private String createTime;    // 下单时间
    private Integer status;       // 订单状态 (0:待制作, 1:已完成/已送达, 2:已取消)

    // --- 2. 店铺信息 (快照) ---
    private Long shopId;
    private String shopName;      // 防止店铺改名，存快照
    private String shopLogo;

    // --- 3. 金额明细 (核心) ---
    // 最终实付总额 = 商品总价 + 打包费 + 配送费 - 优惠
    private BigDecimal payAmount;

    private BigDecimal goodsTotalPrice; // 纯商品总价
    private BigDecimal packingFee;      // 总打包费 (从 CartController 获取)
    private BigDecimal deliveryFee;     // 配送费 (从 ShopEntity 获取)
    private BigDecimal discountAmount;  // 优惠金额 (预留字段)

    // --- 4. 收货人信息 (快照) ---
    // 即使主要地址改了，历史订单的地址也不应该变
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;     // 完整地址 (如: 桐树坞后山)

    private Boolean isComment;

    // --- 5. 其他 ---
    private String remark;              // 订单备注 (口味、偏好)
    private List<OrderItemEntity> items;// 购买的商品列表

    // 辅助方法：获取商品总数
    public int getTotalQuantity() {
        int count = 0;
        if (items != null) {
            for (OrderItemEntity item : items) {
                count += item.getQuantity();
            }
        }
        return count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo;
    }

    public BigDecimal getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(BigDecimal payAmount) {
        this.payAmount = payAmount;
    }

    public BigDecimal getGoodsTotalPrice() {
        return goodsTotalPrice;
    }

    public void setGoodsTotalPrice(BigDecimal goodsTotalPrice) {
        this.goodsTotalPrice = goodsTotalPrice;
    }

    public BigDecimal getPackingFee() {
        return packingFee;
    }

    public void setPackingFee(BigDecimal packingFee) {
        this.packingFee = packingFee;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public List<OrderItemEntity> getItems() {
        return items;
    }

    public void setItems(List<OrderItemEntity> items) {
        this.items = items;
    }

    public Boolean getComment() {
        return isComment;
    }

    public void setComment(Boolean comment) {
        isComment = comment;
    }
}