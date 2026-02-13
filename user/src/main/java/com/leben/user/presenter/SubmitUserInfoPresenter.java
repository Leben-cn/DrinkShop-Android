package com.leben.user.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.user.contract.SubmitUserInfoContract;
import com.leben.user.model.bean.UserInfoEntity;
import com.leben.user.model.network.ApiService;
import com.leben.user.model.network.NetworkManager;

public class SubmitUserInfoPresenter extends BasePresenter<SubmitUserInfoContract.View> implements SubmitUserInfoContract.Presenter {
    @Override
    public void submitUserInfo(UserInfoEntity userInfoEntity) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .updateUserInfo(userInfoEntity)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onSubmitUserInfoSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onSubmitUserInfoFailed(throwable.getMessage());
                    }
                }));
    }
}
