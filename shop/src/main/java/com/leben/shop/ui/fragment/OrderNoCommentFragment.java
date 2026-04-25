package com.leben.shop.ui.fragment;

import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.common.LocationManager;
import com.leben.common.model.event.LocationEvent;
import com.leben.shop.contract.GetNoCommentOrderContract;
import com.leben.common.model.bean.OrderEntity;
import com.leben.shop.presenter.GetNoCommentOrderPresenter;
import com.leben.shop.ui.adapter.OrderAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    public void onInit() {
        super.onInit();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
    }

    @Override
    public void onGetNoCommentOrderSuccess(List<OrderEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetNoCommentOrderFailed(String errorMsg) {
        refreshListFailed("获取订单失败");
        LogUtils.error("获取订单失败："+errorMsg);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(LocationEvent event) {
        this.latitude = event.latitude;
        this.longitude = event.longitude;
        // 拿到位置后再请求订单
        onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
