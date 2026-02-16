package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.GetAllOrderContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class GetAllOrderPresenter extends BasePresenter<GetAllOrderContract.View> implements GetAllOrderContract.Presenter {
    @Override
    public void getAllOrder() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getAllOrder()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetAllOrderSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetAllOrderFailed(throwable.getMessage());
                    }
                }));
    }
}
