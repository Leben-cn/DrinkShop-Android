package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.shop.contract.GetShopInfoContract;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class GetShopInfoPresenter extends BasePresenter<GetShopInfoContract.View> implements GetShopInfoContract.Presenter {

    @Override
    public void getShopInfo(Long shopId,Double userLat,Double userLon) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getShopInfo(shopId,userLat,userLon)
                .compose(RxUtils.rxSchedulerHelper())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetShopInfoSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetShopInfoFailed(throwable.getMessage());
                    }
                }));
    }
}
