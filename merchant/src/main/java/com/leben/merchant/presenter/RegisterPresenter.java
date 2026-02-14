package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.RegisterContract;
import com.leben.merchant.model.bean.MerchantRegisterEntity;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter{
    @Override
    public void register(MerchantRegisterEntity entity) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .register(entity)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onRegisterSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onRegisterFailed(throwable.getMessage());
                    }
                }));
    }
}
