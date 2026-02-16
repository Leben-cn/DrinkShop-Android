package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.GetCancelOrderContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class GetCancelOrderPresenter extends BasePresenter<GetCancelOrderContract.View> implements GetCancelOrderContract.Presenter {
    @Override
    public void getCancelOrder() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getRefundOrder()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetCancelOrderSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetCancelOrderFailed(throwable.getMessage());
                    }
                }));

    }
}
