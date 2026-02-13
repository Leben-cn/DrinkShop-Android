package com.leben.user.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.user.contract.SubmitCommentContract;
import com.leben.user.model.bean.CommentSubmitEntity;
import com.leben.user.model.network.ApiService;
import com.leben.user.model.network.NetworkManager;

public class SubmitCommentPresenter extends BasePresenter<SubmitCommentContract.View> implements SubmitCommentContract.Presenter {

    @Override
    public void submitComment(CommentSubmitEntity commentSubmit) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .submitComment(commentSubmit)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onSubmitCommentSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onSubmitCommentFailed(throwable.getMessage());
                    }
                }));
    }
}
