package com.leben.shop.contract;

import com.leben.base.contract.IBaseView;
import com.leben.shop.model.bean.DrinkEntity;
import java.util.List;

public interface ShopMenuContract {
    interface View extends IBaseView {
        void onGetShopMenuSuccess(List<DrinkEntity> data);
        void onGetShopMenuFailed(String errorMsg);
    }

    interface Presenter {
        void getShopMenuDrinks(Long shopId,Double userLat,Double userLon);
    }
}
