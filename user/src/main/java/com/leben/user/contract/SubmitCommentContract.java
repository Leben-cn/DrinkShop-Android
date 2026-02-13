package com.leben.user.contract;

import com.leben.base.contract.IBaseView;
import com.leben.user.model.bean.CommentSubmitEntity;

public interface SubmitCommentContract {
    interface View extends IBaseView {
        void onSubmitCommentSuccess(String data);
        void onSubmitCommentFailed(String errorMsg);
    }
    interface Presenter {
        void submitComment(CommentSubmitEntity commentSubmit);
    }
}
