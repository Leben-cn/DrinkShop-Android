package com.leben.user.contract;

import com.leben.base.contract.IBaseView;
import com.leben.user.model.bean.BillEntity;
import java.util.List;

public interface GetMyBillContract {
    interface View extends IBaseView {
        void onGetMyBillSuccess(List<BillEntity> data);
        void onGetMyBillFailed(String errorMsg);
    }
    interface Presenter {
        void getMyBill();
    }
}
