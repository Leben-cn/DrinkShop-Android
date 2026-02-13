package com.leben.shop.ui.fragment;

import android.annotation.SuppressLint;

import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.common.LocationManager;
import com.leben.shop.constant.ShopConstant;
import com.leben.shop.contract.GetCancelOrderContract;
import com.leben.common.model.bean.OrderEntity;
import com.leben.shop.presenter.GetCancelOrderPresenter;
import com.leben.shop.ui.adapter.OrderAdapter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class OrderCancelFragment extends BaseRecyclerFragment<OrderEntity> implements GetCancelOrderContract.View {

    @InjectPresenter
    GetCancelOrderPresenter getCancelOrderPresenter;

    private Double latitude;
    private Double longitude;

    @Override
    protected BaseRecyclerAdapter<OrderEntity> createAdapter() {
        return new OrderAdapter(getContext());
    }

    @Override
    public void onRefresh() {
        getCancelOrderPresenter.getCancelOrder(latitude,longitude);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        if (mAdapter instanceof OrderAdapter) {
            OrderAdapter orderAdapter = (OrderAdapter) mAdapter;

            orderAdapter.setOnOrderActionClickListener(new OrderAdapter.OnOrderActionClickListener() {
                @Override
                public void onCancelOrder(OrderEntity order) {

                }

                @Override
                public void onGoToComment(OrderEntity order) {
                    ARouter.getInstance()
                            .build(ShopConstant.Router.USER_COMMENT)
                            .withSerializable("order", order)
                            .navigation();
                }
            });

            orderAdapter.observableClicks()
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(event->{
                        ARouter.getInstance()
                                .build(ShopConstant.Router.ORDER_DETAIL)
                                .withSerializable("order", event.order)
                                .navigation();
                    },throwable -> {
                        LogUtils.error("点击事件流出错: " + throwable.getMessage());
                    });
        }
    }

    @Override
    public void initData() {
        latitude = LocationManager.getInstance().getLatitude();
        longitude = LocationManager.getInstance().getLongitude();
        autoRefresh();
    }

    @Override
    public void onGetCancelOrderSuccess(List<OrderEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetCancelOrderFailed(String errorMsg) {
        refreshListFailed(errorMsg);
        showError(errorMsg);
    }
}
