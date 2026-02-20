package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.GetShopCategoryContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class GetShopCategoryPresenter extends BasePresenter<GetShopCategoryContract.View> implements GetShopCategoryContract.Presenter{
    @Override
    public void getShopCategory() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getShopCategory()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetShopCategorySuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetShopCategoryFailed(throwable.getMessage());
                    }
                }));
    }
}
