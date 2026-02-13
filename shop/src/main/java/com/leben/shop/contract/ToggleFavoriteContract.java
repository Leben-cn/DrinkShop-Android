package com.leben.shop.contract;

import com.leben.base.contract.IBaseView;

public interface ToggleFavoriteContract {
    interface View extends IBaseView {
        void onToggleFavoriteSuccess(Boolean data);
        void onToggleFavoriteFailed(String errorMsg);
    }

    interface Presenter {
        void toggleFavorite(Long shopId);
    }
}
