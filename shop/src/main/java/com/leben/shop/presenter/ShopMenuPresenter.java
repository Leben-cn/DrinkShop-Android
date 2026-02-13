package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.shop.contract.ShopMenuContract;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;
import com.leben.common.util.RxUtils;

public class ShopMenuPresenter extends BasePresenter<ShopMenuContract.View> implements ShopMenuContract.Presenter {

    @Override
    public void getShopMenuDrinks(Long shopId, Double userLat, Double userLon) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getShopMenu(shopId,userLat,userLon)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(
                        data->{
                            if(getView()!=null){
                                getView().onGetShopMenuSuccess(data);
                            }
                        },
                        throwable -> {
                            if(getView()!=null){
                                getView().onGetShopMenuFailed(throwable.getMessage());
                            }
                        }
                ));
    }
}
