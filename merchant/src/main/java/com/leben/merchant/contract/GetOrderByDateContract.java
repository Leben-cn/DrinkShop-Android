package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.OrderEntity;
import java.util.List;

public interface GetOrderByDateContract {
    interface View extends IBaseView {
        void onGetOrderByDateSuccess(List<OrderEntity> data);
        void onGetOrderByDateFailed(String errorMsg);
    }

    interface Presenter{
        void getOrderByDate(String dateStr);
    }
}
