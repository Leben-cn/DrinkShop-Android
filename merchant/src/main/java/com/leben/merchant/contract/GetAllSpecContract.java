package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import com.leben.common.model.bean.OrderEntity;
import com.leben.common.model.bean.SpecGroupEntity;
import com.leben.common.model.bean.SpecOptionEntity;

import java.util.List;

public interface GetAllSpecContract {

    interface View extends IBaseView {
        void onGetAllSpecSuccess(List<SpecOptionEntity> data);
        void onGetAllSpecFailed(String errorMsg);
    }

    interface Presenter{
        void getAllSpec();
    }
}
