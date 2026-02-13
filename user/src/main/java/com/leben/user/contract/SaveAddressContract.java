package com.leben.user.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.AddressEntity;

public interface SaveAddressContract {

    interface View extends IBaseView {
        void onSaveAddressSuccess(String data);
        void onSaveAddressFailed(String errorMsg);
    }
    interface Presenter {
        void saveAddress(AddressEntity addressEntity);
    }
}
