package com.leben.user.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.user.contract.DeleteAddressContract;
import com.leben.user.model.network.ApiService;
import com.leben.user.model.network.NetworkManager;

public class DeleteAddressPresenter extends BasePresenter<DeleteAddressContract.View> implements DeleteAddressContract.Presenter {
    @Override
    public void deleteAddress(Long id) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .deleteAddress(id)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onDeleteAddressSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onDeleteAddressFailed(throwable.getMessage());
                    }
                }));
    }
}
