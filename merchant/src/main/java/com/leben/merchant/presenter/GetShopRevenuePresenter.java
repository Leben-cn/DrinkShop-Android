package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.GetShopRevenueContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class GetShopRevenuePresenter extends BasePresenter<GetShopRevenueContract.View> implements GetShopRevenueContract.Presenter {
    @Override
    public void getShopRevenue() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getShopRevenue()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data -> {
                    if (getView() != null) {
                        getView().onGetShopRevenueSuccess(data);
                    }
                }, throwable -> {
                    if (getView() != null) {
                        getView().onGetShopRevenueFailed(throwable.getMessage());
                    }
                }));
    }
}
