package com.leben.user.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.LoginEntity;

public interface LoginContract {
    interface View extends IBaseView {
        void onLoginSuccess(LoginEntity data);
        void onLoginFailed(String errorMsg);
    }
    interface Presenter {
        void login(String account,String password);
    }
}
