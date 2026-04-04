package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.merchant.model.bean.DrinkRequestEntity;

public interface SaveDrinkContract {
    interface View extends IBaseView{
        void onSaveDrinkSuccess(String data);
        void onSaveDrinkFailed(String errorMsg);
    }
    interface Presenter{
        void saveDrink(DrinkRequestEntity entity);
    }
}
