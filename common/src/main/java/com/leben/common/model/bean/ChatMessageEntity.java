package com.leben.common.model.bean;

public class ChatMessageEntity {

    public Long id;
    public Long sessionId; // 建议加上，方便后续逻辑
    private Long senderId;
    public String senderRole;
    public String content;
    public String sendTime; // 修改为 String，匹配后端的 ISO 格式
    public int msgType;
    public int isRead;      // 建议加上，UI 可能会用到已读/未读状态

    public ChatMessageEntity() {
    }

    public ChatMessageEntity(Long id, Long sessionId, Long senderId, String senderRole, String content, String sendTime, int msgType, int isRead) {
        this.id = id;
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.senderRole = senderRole;
        this.content = content;
        this.sendTime = sendTime;
        this.msgType = msgType;
        this.isRead = isRead;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public int getIsRead() {
        return isRead;
    }

    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }
}