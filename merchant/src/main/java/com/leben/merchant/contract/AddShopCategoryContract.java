package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;

public interface AddShopCategoryContract {
    interface View extends IBaseView {
        void onAddShopCategorySuccess(String data);
        void onAddShopCategoryFailed(String errorMsg);
    }
    interface Presenter{
        void addShopCategory(String name);
    }
}
