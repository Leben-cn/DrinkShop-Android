package com.leben.user.contract;

import com.leben.base.contract.IBaseView;
import com.leben.user.model.bean.RegisterEntity;

public interface RegisterContract {
    interface View extends IBaseView{
        void onRegisterSuccess(String data);
        void onRegisterFailed(String errorMsg);
    }
    interface Presenter{
        void register(RegisterEntity entity);
    }
}
