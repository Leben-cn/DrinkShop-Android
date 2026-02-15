package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.LoginContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter{
    @Override
    public void login(String account, String password) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .login(account, password)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onLoginSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onLoginFailed(throwable.getMessage());
                    }
                }));
    }
}
