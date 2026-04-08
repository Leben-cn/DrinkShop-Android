package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.DelectDrinkContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class DelectDrinkPresenter extends BasePresenter<DelectDrinkContract.View> implements DelectDrinkContract.Presenter{
    @Override
    public void delectDrink(Long drinkId) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .delectDrink(drinkId)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onDelectDrinkSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onDelectDrinkFailed(throwable.getMessage());
                    }
                })
        );
    }
}
