package com.leben.user.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.ShopEntity;

import java.util.List;

public interface GetMyFavoriteContract {
    interface View extends IBaseView{
        void onGetMyFavoriteSuccess(List<ShopEntity> data);
        void onGetMyFavoriteFailed(String errorMsg);
    }
    interface Presenter{
        void getMyFavorite(Double userLat,Double userLon);
    }
}
