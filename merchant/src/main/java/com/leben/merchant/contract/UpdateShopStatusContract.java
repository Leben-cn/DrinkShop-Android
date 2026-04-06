package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;

public interface UpdateShopStatusContract {
    interface View extends IBaseView {
        void onUpdateShopStatusSuccess(String data);
        void onUpdateShopStatusFailed(String errorMsg);
    }
    interface Presenter{
        void updateShopStatus(Integer status);
    }
}
