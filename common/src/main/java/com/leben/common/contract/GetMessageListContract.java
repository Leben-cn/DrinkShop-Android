package com.leben.common.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.ChatMessageEntity;
import java.util.List;

public interface GetMessageListContract {
    interface View extends IBaseView {
        void onGetMessageListSuccess(List<ChatMessageEntity> data);
        void onGetMessageListFailed(String errorMsg);
    }
    interface Presenter{
        void getMessageList(Long targetId,String targetRole);
    }
}
