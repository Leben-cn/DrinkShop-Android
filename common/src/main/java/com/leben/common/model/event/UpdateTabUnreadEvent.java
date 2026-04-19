package com.leben.common.model.event;

public class UpdateTabUnreadEvent {

    private int totalCount;

    public UpdateTabUnreadEvent(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getUnreadCount() {
        return totalCount;
    }
}
