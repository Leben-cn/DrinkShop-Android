package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.DeleteShopCategoryContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class DeleteShopCategoryPresenter extends BasePresenter<DeleteShopCategoryContract.View> implements DeleteShopCategoryContract.Presenter {
    @Override
    public void deleteShopCategory(Long categoryId) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .deleteCategory(categoryId)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onDeleteShopCategorySuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onDeleteShopCategoryFailed(throwable.getMessage());
                    }
                }));
    }
}
