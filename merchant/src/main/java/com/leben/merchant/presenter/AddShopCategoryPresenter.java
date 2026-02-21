package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.AddShopCategoryContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class AddShopCategoryPresenter extends BasePresenter<AddShopCategoryContract.View> implements AddShopCategoryContract.Presenter {
    @Override
    public void addShopCategory(String name) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .addCategory(name)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onAddShopCategorySuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onAddShopCategoryFailed(throwable.getMessage());
                    }
                }));
    }
}
