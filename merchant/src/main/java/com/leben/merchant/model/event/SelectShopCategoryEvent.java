package com.leben.merchant.model.event;

import com.leben.common.model.bean.ShopCategoriesEntity;

public class SelectShopCategoryEvent {

    private ShopCategoriesEntity category;

    public SelectShopCategoryEvent(ShopCategoriesEntity category) {
        this.category = category;
    }

    public ShopCategoriesEntity getCategory() {
        return category;
    }
}
