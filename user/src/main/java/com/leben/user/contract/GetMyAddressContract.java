package com.leben.user.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.AddressEntity;

import java.util.List;

public interface GetMyAddressContract {

    interface View extends IBaseView {
        void onGetMyAddressSuccess(List<AddressEntity> data);
        void onGetMyAddressFailed(String errorMsg);
    }
    interface Presenter {
        void getMyAddress();
    }

}
