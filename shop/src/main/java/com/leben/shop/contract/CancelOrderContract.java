package com.leben.shop.contract;

import com.leben.base.contract.IBaseView;

public interface CancelOrderContract {
    interface View extends IBaseView{
        void onCancelOrderSuccess(String data);
        void onCancelOrderFailed(String errorMsg);
    }
    interface Presenter{
        void cancelOrder(Long orderId);
    }
}
