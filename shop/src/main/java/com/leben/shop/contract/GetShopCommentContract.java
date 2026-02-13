package com.leben.shop.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.CommentEntity;

import java.util.List;

public interface GetShopCommentContract {
    interface View extends IBaseView {
        void onGetShopCommentSuccess(List<CommentEntity> data);
        void onGetShopCommentFailed(String errorMsg);
    }
    interface Presenter {
        void getShopComment(Long shopId);
    }
}
