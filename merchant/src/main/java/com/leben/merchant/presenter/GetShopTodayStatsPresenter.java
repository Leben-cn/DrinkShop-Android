package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.GetShopTodayStatsContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class GetShopTodayStatsPresenter extends BasePresenter<GetShopTodayStatsContract.View> implements GetShopTodayStatsContract.Presenter{
    @Override
    public void getShopTodayStats() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getShopTodayStats()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null){
                        getView().onGetShopTodayStatsSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetShopTodayStatsFailed(throwable.getMessage());
                    }
                }));
    }
}
