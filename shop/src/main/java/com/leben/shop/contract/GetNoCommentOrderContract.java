package com.leben.shop.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.OrderEntity;

import java.util.List;

public interface GetNoCommentOrderContract {
    interface View extends IBaseView{
        void onGetNoCommentOrderSuccess(List<OrderEntity> data);
        void onGetNoCommentOrderFailed(String errorMsg);
    }
    interface Presenter{
        void getNoCommentOrder(Double userLat,Double userLon);
    }
}
