package com.leben.user.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.user.contract.GetMyBillContract;
import com.leben.user.model.network.ApiService;
import com.leben.user.model.network.NetworkManager;

public class GetMyBillPresenter extends BasePresenter<GetMyBillContract.View> implements GetMyBillContract.Presenter {
    @Override
    public void getMyBill() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getMyBill()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetMyBillSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetMyBillFailed(throwable.getMessage());
                    }
                }));
    }
}
