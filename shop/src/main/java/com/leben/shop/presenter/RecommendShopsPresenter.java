package com.leben.shop.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.shop.contract.RecommendShopsContract;
import com.leben.shop.model.network.ApiService;
import com.leben.shop.model.network.NetworkManager;
import com.leben.common.util.RxUtils;

public class RecommendShopsPresenter extends BasePresenter<RecommendShopsContract.View> implements RecommendShopsContract.Presenter{

    @Override
    public void getRecommendShops(int page, int size, Long seed,Double userLat,Double userLon) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getRecommendShops(page,size,seed,userLat,userLon)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(
                        pageData->{
                            if(getView()!=null){
                                getView().onRecommendShopsSuccess(pageData);
                            }
                        },
                        throwable ->{
                            if(getView()!=null){
                                getView().onRecommendShopsFailed(throwable.getMessage());
                            }
                        }
                ));
    }
}
