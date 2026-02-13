package com.leben.common.model.bean;

public class CommentEntity {

    private String userName;
    private String userAvatar;
    private String merchantName;
    private String merchantAvatar;
    private Long orderId;
    private String productName;
    private Integer score;
    private String content;
    private String picture;
    private String createTime;

    public String getUserName() {
        return userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public String getMerchantAvatar() {
        return merchantAvatar;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getScore() {
        return score;
    }

    public String getContent() {
        return content;
    }

    public String getPicture() {
        return picture;
    }

    public String getCreateTime() {
        return createTime;
    }
}
