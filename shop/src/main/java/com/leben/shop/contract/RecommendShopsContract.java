package com.leben.shop.contract;

import com.leben.base.contract.IBaseView;
import com.leben.shop.model.bean.PageEntity;
import com.leben.common.model.bean.ShopEntity;

public interface RecommendShopsContract {

    interface View extends IBaseView {
        void onRecommendShopsSuccess(PageEntity<ShopEntity> pageData);
        void onRecommendShopsFailed(String errorMsg);
    }

    interface Presenter {
        void getRecommendShops(int page, int size,Long seed,Double userLat,Double userLon);
    }
}