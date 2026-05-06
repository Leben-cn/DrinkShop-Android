package com.leben.user.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.user.contract.RegisterContract;
import com.leben.user.model.bean.RegisterEntity;
import com.leben.user.model.network.ApiService;
import com.leben.user.model.network.NetworkManager;

public class RegisterPresenter extends BasePresenter<RegisterContract.View> implements RegisterContract.Presenter {
    @Override
    public void register(RegisterEntity entity) {
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
