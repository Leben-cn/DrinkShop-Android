package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.UpdateShopStatusContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class UpdateShopStatusPresenter extends BasePresenter<UpdateShopStatusContract.View> implements UpdateShopStatusContract.Presenter{
    @Override
    public void updateShopStatus(Integer status) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .updateShopStatus(status)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onUpdateShopStatusSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onUpdateShopStatusFailed(throwable.getMessage());
                    }
                })
        );
    }
}
