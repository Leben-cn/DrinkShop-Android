package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.merchant.model.bean.MerchantInfoEntity;

public interface SubmitShopInfoContract {
    interface View extends IBaseView{
        void onSubmitShopInfoSuccess(String data);
        void onSubmitShopInfoFailed(String errorMsg);
    }
    interface Presenter{
        void submitShopInfo(MerchantInfoEntity merchantInfoEntity);
    }
}
