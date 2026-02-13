package com.leben.user.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.CommentEntity;
import java.util.List;

public interface GetMyCommentContract {
    interface View extends IBaseView {
        void onGetMyCommentSuccess(List<CommentEntity> data);
        void onGetMyCommentFailed(String errorMsg);
    }
    interface Presenter {
        void getMyComment();
    }
}
