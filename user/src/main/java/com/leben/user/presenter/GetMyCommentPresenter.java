package com.leben.user.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.user.contract.GetMyCommentContract;
import com.leben.user.model.network.ApiService;
import com.leben.user.model.network.NetworkManager;

public class GetMyCommentPresenter extends BasePresenter<GetMyCommentContract.View>implements GetMyCommentContract.Presenter {
    @Override
    public void getMyComment() {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getMyComment()
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetMyCommentSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetMyCommentFailed(throwable.getMessage());
                    }
                }));
    }
}
