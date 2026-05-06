package com.leben.common.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.contract.UpdateOrderStatusContract;
import com.leben.common.model.network.ApiService;
import com.leben.common.model.network.NetworkManager;
import com.leben.common.util.RxUtils;

public class UpdateOrderStatusPresenter extends BasePresenter<UpdateOrderStatusContract.View> implements UpdateOrderStatusContract.Presenter{
    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .updateOrderStatus(orderId,status)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onUpdateOrderStatusSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onUpdateOrderStatusFailed(throwable.getMessage());
                    }
                }));
    }
}
