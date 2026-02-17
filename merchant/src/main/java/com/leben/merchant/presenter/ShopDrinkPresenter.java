package com.leben.merchant.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.merchant.contract.GetShopDrinkContract;
import com.leben.merchant.model.network.ApiService;
import com.leben.merchant.model.network.NetworkManager;

public class ShopDrinkPresenter extends BasePresenter<GetShopDrinkContract.View> implements GetShopDrinkContract.Presenter {

    @Override
    public void getShopDrinks(Long shopId) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getShopDrink(shopId)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(
                        data->{
                            if(getView()!=null){
                                getView().onGetShopDrinkSuccess(data);
                            }
                        },
                        throwable -> {
                            if(getView()!=null){
                                getView().onGetShopDrinkFailed(throwable.getMessage());
                            }
                        }
                ));
    }
}
