package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.GetOrderByDateContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class GetOrderByDatePresenter extends BasePresenter<GetOrderByDateContract.View> implements GetOrderByDateContract.Presenter {
    @Override
    public void getOrderByDate(String dateStr) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getOrderByDate(dateStr)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetOrderByDateSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetOrderByDateFailed(throwable.getMessage());
                    }
                }));
    }
}
