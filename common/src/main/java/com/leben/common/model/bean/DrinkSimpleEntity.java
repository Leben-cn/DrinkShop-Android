package com.leben.common.model.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class DrinkSimpleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private BigDecimal price;
    private String img;

    public DrinkSimpleEntity(Long id, String name, BigDecimal price, String img) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.img = img;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getImg() {
        return img;
    }
}
