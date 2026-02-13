package com.leben.shop.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.ShopEntity;

public interface GetShopInfoContract {
    interface View extends IBaseView {
        void onGetShopInfoSuccess(ShopEntity data);
        void onGetShopInfoFailed(String errorMsg);
    }
    interface Presenter{
        void getShopInfo(Long shopId,Double userLat,Double userLon);
    }
}
