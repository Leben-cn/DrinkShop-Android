package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.GetPendingOrderContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class GetPendingOrderPresenter extends BasePresenter<GetPendingOrderContract.View> implements GetPendingOrderContract.Presenter {
    @Override
    public void getPendingOrder() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getPendingOrder()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetPendingOrderSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetPendingOrderFailed(throwable.getMessage());
                    }
                }));
    }
}
