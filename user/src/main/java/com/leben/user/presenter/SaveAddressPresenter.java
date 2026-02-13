package com.leben.user.presenter;

import com.leben.base.presenter.BasePresenter;
import com.leben.common.util.RxUtils;
import com.leben.user.contract.SaveAddressContract;
import com.leben.common.model.bean.AddressEntity;
import com.leben.user.model.network.ApiService;
import com.leben.user.model.network.NetworkManager;

public class SaveAddressPresenter extends BasePresenter<SaveAddressContract.View> implements SaveAddressContract.Presenter {

    @Override
    public void saveAddress(AddressEntity addressEntity) {
        mCompositeDisposable.add(NetworkManager.getInstance().create(ApiService.class)
                .saveAddress(addressEntity)
                .compose(RxUtils.rxSchedulerHelper())
                .subscribe(data->{
                    if(getView()!=null){
                        getView().onSaveAddressSuccess(data);
                    }
                },throwable -> {
                    if(getView()!=null){
                        getView().onSaveAddressFailed(throwable.getMessage());
                    }
                }));
    }
}
