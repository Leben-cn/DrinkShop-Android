package com.leben.common.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.contract.UpdateCommentStatusContract;
import com.leben.common.model.network.ApiService;
import com.leben.common.model.network.NetworkManager;
import com.leben.common.util.RxUtils;

public class UpdateCommentStatusPresenter extends BasePresenter<UpdateCommentStatusContract.View> implements UpdateCommentStatusContract.Presenter{
    @Override
    public void updateCommentStatus(Long orderId, Integer status) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .updateCommentStatus(orderId,status)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onUpdateCommentStatusSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onUpdateCommentStatusFailed(throwable.getMessage());
                    }
                }));
    }
}
