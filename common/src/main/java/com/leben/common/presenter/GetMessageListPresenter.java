package com.leben.common.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.contract.GetMessageListContract;
import com.leben.common.model.network.ApiService;
import com.leben.common.model.network.NetworkManager;
import com.leben.common.util.RxUtils;

public class GetMessageListPresenter extends BasePresenter<GetMessageListContract.View> implements GetMessageListContract.Presenter {
    @Override
    public void getMessageList(Long targetId,String targetRole) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .getMessageList(targetId,targetRole)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if (getView() != null) {
                        getView().onGetMessageListSuccess(data);
                    }
                },throwable -> {
                    if (getView() != null) {
                        getView().onGetMessageListFailed(throwable.getMessage());
                    }
                }));
    }
}
