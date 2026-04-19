package com.leben.common.model.bean;

import java.math.BigDecimal;

public class LoginEntity {

    private String token;

    private UserInfo userInfo;
    private ShopInfo shopInfo;

    public static class UserInfo {
        private Long id;
        private String username;  // 账号
        private String nickname;  // 昵称
        private String avatar;    // 头像 URL
        private String phone;     // 电话
        private String password;
        // private String role;   // 如果前端需要根据角色做显隐，可以加上

        public Long getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getNickname() {
            return nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getPhone() {
            return phone;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class ShopInfo {
        private Long id;
        private String account;
        private String password;
        private String shopName;     // 对应实体类的 name
        private String img;          // 店铺头像
        private String description;  // 简介
        private String phone;
        private BigDecimal minOrder; // 起送价
        private BigDecimal deliveryFee; // 配送费
        private Double rating;       // 评分
        private Integer totalSales;  // 销量
        private Integer status;      // 状态
        private Double longitude;
        private Double latitude;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setMinOrder(BigDecimal minOrder) {
            this.minOrder = minOrder;
        }

        public void setDeliveryFee(BigDecimal deliveryFee) {
            this.deliveryFee = deliveryFee;
        }

        public void setRating(Double rating) {
            this.rating = rating;
        }

        public void setTotalSales(Integer totalSales) {
            this.totalSales = totalSales;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Long getId() {
            return id;
        }

        public String getAccount() {
            return account;
        }

        public String getShopName() {
            return shopName;
        }

        public String getImg() {
            return img;
        }

        public String getDescription() {
            return description;
        }

        public String getPhone() {
            return phone;
        }

        public BigDecimal getMinOrder() {
            return minOrder;
        }

        public BigDecimal getDeliveryFee() {
            return deliveryFee;
        }

        public Double getRating() {
            return rating;
        }

        public Integer getTotalSales() {
            return totalSales;
        }

        public Integer getStatus() {
            return status;
        }

        public Double getLongitude() {
            return longitude;
        }

        public Double getLatitude() {
            return latitude;
        }
    }

    public String getToken() {
        return token;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public ShopInfo getShopInfo() {
        return shopInfo;
    }

    public void setShopInfo(ShopInfo shopInfo) {
        this.shopInfo = shopInfo;
    }
}
