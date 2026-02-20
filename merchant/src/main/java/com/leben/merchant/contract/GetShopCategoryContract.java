package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.ShopCategoriesEntity;
import com.leben.common.model.bean.SpecOptionEntity;
import java.util.List;

public interface GetShopCategoryContract {
    interface View extends IBaseView {
        void onGetShopCategorySuccess(List<ShopCategoriesEntity> data);
        void onGetShopCategoryFailed(String errorMsg);
    }

    interface Presenter{
        void getShopCategory();
    }
}
