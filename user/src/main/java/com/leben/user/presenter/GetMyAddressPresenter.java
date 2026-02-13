package com.leben.user.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.user.contract.GetMyAddressContract;
import com.leben.user.model.network.ApiService;
import com.leben.user.model.network.NetworkManager;

public class GetMyAddressPresenter extends BasePresenter<GetMyAddressContract.View> implements GetMyAddressContract.Presenter {

    @Override
    public void getMyAddress() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getMyAddress()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if(getView()!=null){
                        getView().onGetMyAddressSuccess(data);
                    }
                },throwable -> {
                    if(getView()!=null){
                        getView().onGetMyAddressFailed(throwable.getMessage());
                    }
                }));
    }
}
