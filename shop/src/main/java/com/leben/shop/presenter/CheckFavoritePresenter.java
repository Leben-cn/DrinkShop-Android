package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.shop.contract.CheckFavoriteContract;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;

public class CheckFavoritePresenter extends BasePresenter<CheckFavoriteContract.View> implements CheckFavoriteContract.Presenter {

    @Override
    public void checkFavorite(Long shopId) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .checkFavorite(shopId)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onCheckFavoriteSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onCheckFavoriteFailed(throwable.getMessage());
                    }
                }));
    }
}
