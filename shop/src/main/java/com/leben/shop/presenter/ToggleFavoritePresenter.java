package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.shop.contract.ToggleFavoriteContract;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;

public class ToggleFavoritePresenter extends BasePresenter<ToggleFavoriteContract.View> implements ToggleFavoriteContract.Presenter{
    @Override
    public void toggleFavorite(Long shopId) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .toggleFavorite(shopId)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onToggleFavoriteSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onToggleFavoriteFailed(throwable.getMessage());
                    }
                }));
    }
}
