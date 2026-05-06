package com.leben.common.contract;

import com.leben.base.contract.IBaseView;

public interface UpdateOrderStatusContract {
    interface View extends IBaseView {
        void onUpdateOrderStatusSuccess(String data);
        void onUpdateOrderStatusFailed(String errorMsg);
    }
    interface Presenter{
        void updateOrderStatus(Long commentId,Integer status);
    }
}
