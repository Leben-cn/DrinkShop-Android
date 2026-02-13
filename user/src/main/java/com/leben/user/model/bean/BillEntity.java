package com.leben.user.model.bean;

import java.math.BigDecimal;

public class BillEntity {
    private String merchantName;
    private String merchantAvatar;
    private String createTime;
    private BigDecimal totalPrice;

    public String getMerchantName() {
        return merchantName;
    }

    public String getMerchantAvatar() {
        return merchantAvatar;
    }

    public String getCreateTime() {
        return createTime;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
