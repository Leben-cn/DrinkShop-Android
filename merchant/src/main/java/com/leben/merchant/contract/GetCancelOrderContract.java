package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.OrderEntity;
import java.util.List;

public interface GetCancelOrderContract {
    interface View extends IBaseView {
        void onGetCancelOrderSuccess(List<OrderEntity> data);
        void onGetCancelOrderFailed(String errorMsg);
    }

    interface Presenter{
        void getCancelOrder();
    }
}
