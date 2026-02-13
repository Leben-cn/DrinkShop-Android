package com.leben.shop.model.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class SpecOptionEntity implements Serializable, Comparable<SpecOptionEntity> {

    public long id;
    public String name; // "少冰"
    public BigDecimal price; // 0.0 或 2.0
    public boolean isSelected; // 【关键】前端用来记录是否被选中

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public String toString() {
        return "SpecOptionEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", isSelected=" + isSelected +
                '}';
    }

    //用于排序，保证生成的 Key 唯一
    @Override
    public int compareTo(SpecOptionEntity o) {
        return Long.compare(this.id, o.id);
    }
}
