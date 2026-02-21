package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;

public interface DeleteShopCategoryContract {
    interface View extends IBaseView {
        void onDeleteShopCategorySuccess(String data);
        void onDeleteShopCategoryFailed(String errorMsg);
    }
    interface Presenter{
        void deleteShopCategory(Long categoryId);
    }
}
