package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.QueryDrinkContract;
import com.leben.merchant.model.bean.DrinkQueryEntity;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class QueryDrinkPresenter extends BasePresenter<QueryDrinkContract.View> implements QueryDrinkContract.Presenter {
    @Override
    public void queryDrink(DrinkQueryEntity entity,int page,int size) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .queryDrink(entity,page,size)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onQueryDrinkSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onQueryDrinkFailed(throwable.getMessage());
                    }
                }));
    }
}
