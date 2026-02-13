package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.shop.contract.GetCancelOrderContract;
import com.leben.shop.contract.GetNoCommentOrderContract;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;

public class GetCancelOrderPresenter extends BasePresenter<GetCancelOrderContract.View> implements GetCancelOrderContract.Presenter {

    @Override
    public void getCancelOrder(Double userLat,Double userLon) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getCancelOrder(userLat,userLon)
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
