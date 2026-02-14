package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.merchant.model.bean.MerchantRegisterEntity;

public interface RegisterContract {

    interface View extends IBaseView{
        void onRegisterSuccess(String data);
        void onRegisterFailed(String errorMsg);
    }
    interface Presenter{
        void register(MerchantRegisterEntity entity);
    }
}
