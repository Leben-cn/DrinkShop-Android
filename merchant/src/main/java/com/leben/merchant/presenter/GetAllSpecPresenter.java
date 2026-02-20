package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.GetAllSpecContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class GetAllSpecPresenter extends BasePresenter<GetAllSpecContract.View> implements GetAllSpecContract.Presenter{
    @Override
    public void getAllSpec() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getAllSpec()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetAllSpecSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetAllSpecFailed(throwable.getMessage());
                    }
                }));
    }
}
