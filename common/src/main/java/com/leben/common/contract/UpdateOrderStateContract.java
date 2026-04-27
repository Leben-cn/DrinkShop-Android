package com.leben.common.contract;

import com.leben.base.contract.IBaseView;

public interface UpdateOrderStateContract {
    interface View extends IBaseView {
        void onUpdateOrderStateSuccess(String data);
        void onUpdateOrderStateFailed(String errorMsg);
    }
    interface Presenter{
        void updateOrderState(Long orderId,Integer status);
    }
}
