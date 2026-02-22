package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.UpdateCategorySortContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;
import java.util.List;

public class UpdateCategorySortPresenter extends BasePresenter<UpdateCategorySortContract.View> implements UpdateCategorySortContract.Presenter {
    @Override
    public void updateCategorySort(List<Long> ids) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .updateShopCategory(ids)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onUpdateCategorySortSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onUpdateCategorySortFailed(throwable.getMessage());
                    }
                }));
    }
}
