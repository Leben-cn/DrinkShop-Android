package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.shop.contract.GetShopCommentContract;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;

public class GetShopCommentPresenter extends BasePresenter<GetShopCommentContract.View>implements GetShopCommentContract.Presenter {
    @Override
    public void getShopComment(Long shopId) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getShopComment(shopId)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetShopCommentSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetShopCommentFailed(throwable.getMessage());
                    }
                }));
    }
}
