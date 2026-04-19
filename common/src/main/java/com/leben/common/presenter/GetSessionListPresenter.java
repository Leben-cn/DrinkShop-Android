package com.leben.common.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.contract.GetSessionListContract;
import com.leben.common.model.network.ApiService;
import com.leben.common.model.network.NetworkManager;
import com.leben.common.util.RxUtils;

public class GetSessionListPresenter extends BasePresenter<GetSessionListContract.View> implements GetSessionListContract.Presenter {
    @Override
    public void getSessionList() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getSessionList()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetSessionListSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetSessionListFailed(throwable.getMessage());
                    }
                }));
    }
}
