package com.leben.shop.contract;

import com.leben.base.contract.IBaseView;
import com.leben.shop.model.bean.DrinkEntity;
import com.leben.shop.model.bean.DrinkQueryEntity;
import com.leben.shop.model.bean.PageEntity;

import java.util.List;

public interface QueryDrinkContract {
    interface View extends IBaseView{
        void onQueryDrinkSuccess(PageEntity<DrinkEntity> data);
        void onQueryDrinkFailed(String errorMsg);
    }
    interface Presenter{
        void queryDrink(DrinkQueryEntity entity,int page,int size);
    }
}
