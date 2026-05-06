package com.leben.common.contract;

import com.leben.base.contract.IBaseView;

public interface UpdateCommentStatusContract {
    interface View extends IBaseView {
        void onUpdateCommentStatusSuccess(String data);
        void onUpdateCommentStatusFailed(String errorMsg);
    }
    interface Presenter{
        void updateCommentStatus(Long orderId,Integer status);
    }
}
