package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.shop.contract.SubmitOrderContract;
import com.leben.common.model.bean.OrderEntity;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;

public class SubmitOrderPresenter extends BasePresenter<SubmitOrderContract.View> implements SubmitOrderContract.Presenter {

    @Override
    public void submitOrder(OrderEntity orderEntity) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .submitOrder(orderEntity)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if(getView()!=null){
                        getView().onSubmitOrderSuccess(data);
                    }
                },throwable -> {
                    if(getView()!=null){
                        getView().onSubmitOrderFailed(throwable.getMessage());
                    }
                }));
    }
}
