package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;

public interface DelectDrinkContract {
    interface View extends IBaseView{
        void onDelectDrinkSuccess(String data);
        void onDelectDrinkFailed(String errorMsg);
    }
    interface Presenter{
        void delectDrink(Long drinkId);
    }
}
