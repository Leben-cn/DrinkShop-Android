package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.common.model.bean.PageEntity;
import com.leben.merchant.model.bean.DrinkQueryEntity;

public interface QueryDrinkContract {
    interface View extends IBaseView{
        void onQueryDrinkSuccess(PageEntity<DrinkEntity> data);
        void onQueryDrinkFailed(String errorMsg);
    }
    interface Presenter{
        void queryDrink(DrinkQueryEntity entity, int page, int size);
    }
}
