package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import java.util.Map;

public interface GetShopTodayStatsContract {

    interface View extends IBaseView {
        void onGetShopTodayStatsSuccess(Map<String, Object> data);
        void onGetShopTodayStatsFailed(String errorMsg);
    }

    interface Presenter {
        void getShopTodayStats();
    }
}
