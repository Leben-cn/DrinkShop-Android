package com.leben.common.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.SessionEntity;
import java.util.List;

public interface GetSessionListContract {
    interface View extends IBaseView {
        void onGetSessionListSuccess(List<SessionEntity> data);
        void onGetSessionListFailed(String errorMsg);
    }
    interface Presenter{
        void getSessionList();
    }
}
