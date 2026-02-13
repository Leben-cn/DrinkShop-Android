package com.leben.common.model.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class ShopEntity  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private BigDecimal deliveryFee;
    private BigDecimal minOrder;
    private String img;
    private String description;
    private Integer totalSales;
    private Double rating;
    private String distance;
    private Integer status;
    private List<DrinkSimpleEntity> drinks;

    public List<DrinkSimpleEntity> getDrinks() {
        return drinks;
    }

    public String getDistance() {
        return distance;
    }

    public Double getRating() {
        return rating;
    }

    public Integer getTotalSales() {
        return totalSales;
    }

    public String getImg() {
        return img;
    }

    public BigDecimal getMinOrder() {
        return minOrder;
    }

    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Integer getStatus() {
        return status;
    }
}
