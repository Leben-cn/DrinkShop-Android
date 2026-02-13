package com.leben.shop.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.OrderEntity;

public interface SubmitOrderContract {
    interface View extends IBaseView {
        void onSubmitOrderSuccess(Long data);
        void onSubmitOrderFailed(String errorMsg);
    }
    interface Presenter{
        void submitOrder(OrderEntity orderEntity);
    }
}
