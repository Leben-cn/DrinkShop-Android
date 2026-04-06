package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.SubmitShopInfoContract;
import com.leben.merchant.model.bean.MerchantInfoEntity;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class SubmitShopInfoPresenter extends BasePresenter<SubmitShopInfoContract.View> implements SubmitShopInfoContract.Presenter {
    @Override
    public void submitShopInfo(MerchantInfoEntity merchantInfoEntity) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .updateMerchantInfo(merchantInfoEntity)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onSubmitShopInfoSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onSubmitShopInfoFailed(throwable.getMessage());
                    }
                })
        );
    }
}
