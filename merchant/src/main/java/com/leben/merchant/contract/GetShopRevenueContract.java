package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import java.math.BigDecimal;

public interface GetShopRevenueContract {
    interface View extends IBaseView {
        void onGetShopRevenueSuccess(BigDecimal data);
        void onGetShopRevenueFailed(String errorMsg);
    }

    interface Presenter {
        void getShopRevenue();
    }
}
