package com.leben.merchant.model.bean;

import java.math.BigDecimal;

public class LoginEntity {

    private String token;

    private ShopInfo shopInfo;

    public static class ShopInfo {
        private Long id;
        private String account;
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

    public ShopInfo getShopInfo() {
        return shopInfo;
    }
}
