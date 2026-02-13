package com.leben.shop.contract;

import com.leben.base.contract.IBaseView;

public interface CheckFavoriteContract {
    interface View extends IBaseView{
        void onCheckFavoriteSuccess(Boolean data);
        void onCheckFavoriteFailed(String errorMsg);
    }
    interface Presenter {
        void checkFavorite(Long shopId);
    }
}
