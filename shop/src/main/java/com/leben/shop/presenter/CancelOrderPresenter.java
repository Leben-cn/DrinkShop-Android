package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.shop.contract.CancelOrderContract;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;

public class CancelOrderPresenter extends BasePresenter<CancelOrderContract.View> implements CancelOrderContract.Presenter {
    @Override
    public void cancelOrder(Long orderId) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .cancelOrder(orderId)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onCancelOrderSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onCancelOrderFailed(throwable.getMessage());
                    }
                }));
    }
}
