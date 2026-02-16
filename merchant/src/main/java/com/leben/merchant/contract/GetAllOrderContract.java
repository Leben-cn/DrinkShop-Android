package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.OrderEntity;
import java.util.List;

public interface GetAllOrderContract {

    interface View extends IBaseView{
        void onGetAllOrderSuccess(List<OrderEntity> data);
        void onGetAllOrderFailed(String errorMsg);
    }

    interface Presenter{
        void getAllOrder();
    }
}
