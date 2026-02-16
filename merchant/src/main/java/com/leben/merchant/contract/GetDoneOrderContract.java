package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.OrderEntity;
import java.util.List;

public interface GetDoneOrderContract {
    interface View extends IBaseView {
        void onGetDoneOrderSuccess(List<OrderEntity> data);
        void onGetDoneOrderFailed(String errorMsg);
    }

    interface Presenter{
        void getDoneOrder();
    }
}
