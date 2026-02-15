package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.merchant.model.bean.LoginEntity;

public interface LoginContract {
    interface View extends IBaseView {
        void onLoginSuccess(LoginEntity data);
        void onLoginFailed(String errorMsg);
    }
    interface Presenter {
        void login(String account,String password);
    }
}
