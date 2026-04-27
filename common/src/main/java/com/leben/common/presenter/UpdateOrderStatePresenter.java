package com.leben.common.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.contract.UpdateOrderStateContract;
import com.leben.common.model.network.ApiService;
import com.leben.common.model.network.NetworkManager;
import com.leben.common.util.RxUtils;

public class UpdateOrderStatePresenter extends BasePresenter<UpdateOrderStateContract.View> implements UpdateOrderStateContract.Presenter{
    @Override
    public void updateOrderState(Long orderId, Integer status) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .updateOrderStatus(orderId,status)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onUpdateOrderStateSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onUpdateOrderStateFailed(throwable.getMessage());
                    }
                }));
    }
}
