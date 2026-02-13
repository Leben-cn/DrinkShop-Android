package com.leben.shop.model.event;

import java.math.BigDecimal;

public class CartEvent {
    private int totalQuantity;
    private BigDecimal totalPrice;

    public CartEvent(int totalQuantity, BigDecimal totalPrice) {
        this.totalQuantity = totalQuantity;
        this.totalPrice = totalPrice;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
}
