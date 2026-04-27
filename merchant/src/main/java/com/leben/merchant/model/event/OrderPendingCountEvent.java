package com.leben.merchant.model.event;

public class OrderPendingCountEvent {
    public int pendingCount;

    public OrderPendingCountEvent(int pendingCount) {
        this.pendingCount = pendingCount;
    }
}
