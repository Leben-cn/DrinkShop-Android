package com.leben.shop.ui.fragment;

import android.annotation.SuppressLint;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.common.LocationManager;
import com.leben.common.constant.CommonConstant;
import com.leben.common.model.event.LocationEvent;
import com.leben.shop.constant.ShopConstant;
import com.leben.shop.contract.GetCancelOrderContract;
import com.leben.common.model.bean.OrderEntity;
import com.leben.shop.presenter.GetCancelOrderPresenter;
import com.leben.shop.ui.adapter.OrderAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by youjiahui on 2026/4/12.
 */

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

    @Override
    public void onInit() {
        super.onInit();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
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
                            .build(CommonConstant.Router.USER_COMMENT)
                            .withSerializable("order", order)
                            .navigation();
                }
                @Override
                public void onConfirmReceipt(OrderEntity order) {

                }

                @Override
                public void onContactShop(OrderEntity order) {
                    String roleStr = "MERCHANT";
                    ARouter.getInstance()
                            .build(CommonConstant.Router.CHAT_DETAIL)
                            .withLong("targetId", order.getShopId()) // 注意：这里传的是对方真实的 ID
                            .withString("targetName", order.getShopName())
                            .withString("targetRoleStr", roleStr) // 传字符串
                            .withString("targetIcon", order.getShopLogo())
                            .navigation();
                }
            });

            orderAdapter.observableClicks()
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(event->{
                        ARouter.getInstance()
                                .build(CommonConstant.Router.ORDER_DETAIL)
                                .withSerializable("order", event.order)
                                .navigation();
                    },throwable -> {
                        LogUtils.error("点击事件流出错: " + throwable.getMessage());
                    });
        }
    }

    @Override
    public void initData() {
        // 1. 检查是否有缓存位置，如果有，直接用缓存位置请求一次
        Double cachedLat = LocationManager.getInstance().getLatitude();
        Double cachedLon = LocationManager.getInstance().getLongitude();

        if (cachedLat != null && cachedLat != 0) {
            this.latitude = cachedLat;
            this.longitude = cachedLon;
            // 有缓存位置，可以直接开始刷新
            autoRefresh();
        } else {
            LocationManager.getInstance().startSingleLocation(requireContext());
        }
    }

    @Override
    public void onGetCancelOrderSuccess(List<OrderEntity> data) {
        refreshListSuccess(data);
        onLoadMore();
    }

    @Override
    public void onGetCancelOrderFailed(String errorMsg) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(LocationEvent event) {
        this.latitude = event.latitude;
        this.longitude = event.longitude;
        onRefresh();
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }
}
