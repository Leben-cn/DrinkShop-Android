package com.leben.merchant.model.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class SpecOptionEntity implements Serializable {
    private Long specOptionId;
    private BigDecimal priceAdjust;
    private Integer isDefault;

    public Long getSpecOptionId() {
        return specOptionId;
    }

    public void setSpecOptionId(Long specOptionId) {
        this.specOptionId = specOptionId;
    }

    public BigDecimal getPriceAdjust() {
        return priceAdjust;
    }

    public void setPriceAdjust(BigDecimal priceAdjust) {
        this.priceAdjust = priceAdjust;
    }

    public Integer getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Integer isDefault) {
        this.isDefault = isDefault;
    }


}
