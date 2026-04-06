package com.leben.merchant.model.bean;

import java.math.BigDecimal;

public class MerchantInfoEntity {
    private String img;          // 店铺头像
    private String name;         // 店铺名
    private String account;      // 账号
    private String password;     // 密码
    private String phone;        // 联系电话
    private BigDecimal deliveryFee; // 运费
    private BigDecimal minOrder;    // 起送价
    private String description;  // 店铺描述

    public MerchantInfoEntity() {

    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDeliveryFee(BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public void setMinOrder(BigDecimal minOrder) {
        this.minOrder = minOrder;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
