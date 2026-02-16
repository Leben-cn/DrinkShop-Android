package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.GetDoneOrderContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class GetDoneOrderPresenter extends BasePresenter<GetDoneOrderContract.View> implements GetDoneOrderContract.Presenter{
    @Override
    public void getDoneOrder() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getCompletedOrder()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() !=null) {
                        getView().onGetDoneOrderSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetDoneOrderFailed(throwable.getMessage());
                    }
                }));
    }
}
