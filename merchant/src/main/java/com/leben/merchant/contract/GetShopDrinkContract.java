package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.DrinkEntity;
import java.util.List;

public interface GetShopDrinkContract {
    interface View extends IBaseView {
        void onGetShopDrinkSuccess(List<DrinkEntity> data);
        void onGetShopDrinkFailed(String errorMsg);
    }

    interface Presenter {
        void getShopDrinks(Long shopId);
    }
}
