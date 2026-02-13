package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.shop.contract.GetNoCommentOrderContract;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;

public class GetNoCommentOrderPresenter extends BasePresenter<GetNoCommentOrderContract.View> implements GetNoCommentOrderContract.Presenter {

    @Override
    public void getNoCommentOrder(Double userLat,Double userLon) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getNoCommentOrder(userLat, userLon)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetNoCommentOrderSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetNoCommentOrderFailed(throwable.getMessage());
                    }
                }));
    }
}
