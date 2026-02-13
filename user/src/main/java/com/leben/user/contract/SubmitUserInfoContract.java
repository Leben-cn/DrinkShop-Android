package com.leben.user.contract;

import com.leben.base.contract.IBaseView;
import com.leben.user.model.bean.UserInfoEntity;

public interface SubmitUserInfoContract {
    interface View extends IBaseView {
        void onSubmitUserInfoSuccess(String data);
        void onSubmitUserInfoFailed(String errorMsg);
    }
    interface Presenter {
        void submitUserInfo(UserInfoEntity userInfoEntity);
    }
}
