package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.shop.contract.GetAllOrderContract;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;

public class GetAllOrderPresenter extends BasePresenter<GetAllOrderContract.View> implements GetAllOrderContract.Presenter {
    @Override
    public void getAllOrder(Double userLat,Double userLon) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getAllOrder(userLat, userLon)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if(getView()!=null){
                        getView().onGetAllOrderSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetAllOrderFailed(throwable.getMessage());
                    }
                }));
    }
}
