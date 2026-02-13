package com.leben.user.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.user.contract.GetMyFavoriteContract;
import com.leben.user.model.network.ApiService;
import com.leben.user.model.network.NetworkManager;

public class GetMyFavoritePresenter extends BasePresenter<GetMyFavoriteContract.View> implements GetMyFavoriteContract.Presenter{
    @Override
    public void getMyFavorite(Double userLat, Double userLon) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getMyFavorite(userLat, userLon)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetMyFavoriteSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetMyFavoriteFailed(throwable.getMessage());
                    }
                }));
    }
}
