package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.OrderEntity;
import java.util.List;

public interface GetPendingOrderContract {
    interface View extends IBaseView {
        void onGetPendingOrderSuccess(List<OrderEntity> data);
        void onGetPendingOrderFailed(String errorMsg);
    }

    interface Presenter{
        void getPendingOrder();
    }
}
