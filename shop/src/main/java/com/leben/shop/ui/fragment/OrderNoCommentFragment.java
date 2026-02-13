package com.leben.shop.ui.fragment;

import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.common.LocationManager;
import com.leben.shop.contract.GetNoCommentOrderContract;
import com.leben.common.model.bean.OrderEntity;
import com.leben.shop.presenter.GetNoCommentOrderPresenter;
import com.leben.shop.ui.adapter.OrderAdapter;

import java.util.List;

public class OrderNoCommentFragment extends BaseRecyclerFragment<OrderEntity> implements GetNoCommentOrderContract.View {

    @InjectPresenter
    GetNoCommentOrderPresenter getNoCommentOrderPresenter;

    private Double latitude;
    private Double longitude;

    @Override
    protected BaseRecyclerAdapter<OrderEntity> createAdapter() {
        return new OrderAdapter(getContext());
    }

    @Override
    public void onRefresh() {
        getNoCommentOrderPresenter.getNoCommentOrder(latitude,longitude);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        latitude = LocationManager.getInstance().getLatitude();
        longitude = LocationManager.getInstance().getLongitude();
        autoRefresh();
    }

    @Override
    public void onGetNoCommentOrderSuccess(List<OrderEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetNoCommentOrderFailed(String errorMsg) {
        refreshListFailed(errorMsg);
        showError(errorMsg);
    }
}
