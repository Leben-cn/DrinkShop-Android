package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.SaveDrinkContract;
import com.leben.merchant.model.bean.DrinkRequestEntity;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class SaveDrinkPresenter extends BasePresenter<SaveDrinkContract.View> implements SaveDrinkContract.Presenter {
    @Override
    public void saveDrink(DrinkRequestEntity entity) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .saveDrink(entity)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onSaveDrinkSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onSaveDrinkFailed(throwable.getMessage());
                    }
                }));
    }
}
