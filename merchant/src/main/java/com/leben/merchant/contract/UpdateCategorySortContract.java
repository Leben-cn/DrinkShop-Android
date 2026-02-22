package com.leben.merchant.contract;

import com.leben.base.contract.IBaseView;
import java.util.List;

public interface UpdateCategorySortContract {
    interface View extends IBaseView {
        void onUpdateCategorySortSuccess(String data);
        void onUpdateCategorySortFailed(String errorMsg);
    }
    interface Presenter{
        void updateCategorySort(List<Long> ids);
    }
}
