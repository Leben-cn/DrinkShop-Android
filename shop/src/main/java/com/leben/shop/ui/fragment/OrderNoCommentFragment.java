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

/**
 * Created by youjiahui on 2026/4/12.
 */

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
        refreshListFailed("获取订单失败");
        showError("获取订单失败："+errorMsg);
    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }

    @Override
    public void onResume() {
        super.onResume();
        autoRefresh();
    }

    @Override
    protected boolean shouldAddDefaultSpaceDecoration() {
        return false;
    }
}
