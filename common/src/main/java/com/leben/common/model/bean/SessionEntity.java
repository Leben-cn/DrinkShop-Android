package com.leben.common.model.bean;

public class SessionEntity {

    public Long id;
    public Long targetId;        // 对方的ID
    public String targetName;    // 对方昵称（商家名/用户名）
    public String targetIcon;    // 对方头像
    public String lastMessage;   // 最后一条消息
    public String lastTime;      // 格式化后的时间
    public int unreadCount;      // 未读数
    public int targetRole;       // 对方角色：1商家，2管理员，0用户

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

    public String getTargetIcon() {
        return targetIcon;
    }

    public void setTargetIcon(String targetIcon) {
        this.targetIcon = targetIcon;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getTargetRole() {
        return targetRole;
    }

    public void setTargetRole(int targetRole) {
        this.targetRole = targetRole;
    }
}
