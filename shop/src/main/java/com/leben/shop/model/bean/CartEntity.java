package com.leben.shop.model.bean;

import com.leben.common.model.bean.DrinkEntity;
import com.leben.common.model.bean.SpecOptionEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CartEntity implements Serializable {

    private DrinkEntity drink; // 商品原始数据
    private int quantity;      // 购买数量

    // 选中的规格列表 (保存起来，确认订单页要用)
    private List<SpecOptionEntity> specs;

    // 规格描述文本 (例如 "少冰, 半糖")，用于 UI 展示
    private String specDesc;

    // 当前单价 (基础价 + 规格加价)
    private BigDecimal currentPrice;

    public CartEntity(DrinkEntity drink, int quantity, List<SpecOptionEntity> specs, BigDecimal currentPrice, String specDesc) {
        this.drink = drink;
        this.quantity = quantity;
        this.specs = specs;
        this.currentPrice = currentPrice;
        this.specDesc = specDesc;
    }

    public DrinkEntity getDrink() {
        return drink;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<SpecOptionEntity> getSpecs() {
        return specs;
    }

    public String getSpecDesc() {
        return specDesc;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    // 计算这一项的小计：单价 * 数量
    public BigDecimal getItemTotalPrice() {
        if (currentPrice == null) {
            return BigDecimal.ZERO;
        }
        return currentPrice.multiply(BigDecimal.valueOf(quantity));
    }


}
