package com.leben.user.model.bean;

import java.util.List;

public class CommentSubmitEntity {
    private Long orderId;
    private String content; // 评价文字内容
    private String picture;
    private List<ProductRating> items; // 商品评分列表

    public static class ProductRating {
        private Long productId; // 对应 drinks 表的 id
        private Integer rating; // 1-5 星

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public Integer getRating() {
            return rating;
        }

        public void setRating(Integer rating) {
            this.rating = rating;
        }
    }

    public CommentSubmitEntity(){

    }
    public CommentSubmitEntity(Long orderId, String content, String picture, List<ProductRating> items) {
        this.orderId = orderId;
        this.content = content;
        this.picture = picture;
        this.items = items;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<ProductRating> getItems() {
        return items;
    }

    public void setItems(List<ProductRating> items) {
        this.items = items;
    }
}
