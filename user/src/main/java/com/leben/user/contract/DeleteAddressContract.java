package com.leben.user.contract;

import com.leben.base.contract.IBaseView;

public interface DeleteAddressContract {
    interface View extends IBaseView {
        void onDeleteAddressSuccess(String data);
        void onDeleteAddressFailed(String errorMsg);
    }
    interface Presenter {
        void deleteAddress(Long id);
    }
}
