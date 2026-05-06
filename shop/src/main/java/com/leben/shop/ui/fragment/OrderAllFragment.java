package com.leben.shop.ui.fragment;

import android.annotation.SuppressLint;
import androidx.fragment.app.DialogFragment;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.model.event.RefreshEvent;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.common.LocationManager;
import com.leben.common.constant.CommonConstant;
import com.leben.common.contract.UpdateOrderStatusContract;
import com.leben.common.model.event.LocationEvent;
import com.leben.common.presenter.UpdateOrderStatusPresenter;
import com.leben.shop.contract.CancelOrderContract;
import com.leben.shop.contract.GetAllOrderContract;
import com.leben.common.model.bean.OrderEntity;
import com.leben.shop.presenter.CancelOrderPresenter;
import com.leben.shop.presenter.GetAllOrderPresenter;
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

public class OrderAllFragment extends BaseRecyclerFragment<OrderEntity> implements GetAllOrderContract.View,
        CancelOrderContract.View , UpdateOrderStatusContract.View {

    @InjectPresenter
    GetAllOrderPresenter getAllOrderPresenter;

    @InjectPresenter
    CancelOrderPresenter cancelOrderPresenter;

    @InjectPresenter
    UpdateOrderStatusPresenter updateOrderStatePresenter;

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
        getAllOrderPresenter.getAllOrder(latitude, longitude);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        if (mAdapter instanceof OrderAdapter) {
            OrderAdapter orderAdapter = (OrderAdapter) mAdapter;

            orderAdapter.setOnOrderActionClickListener(new OrderAdapter.OnOrderActionClickListener() {
                @Override
                public void onCancelOrder(OrderEntity order) {
                    CommonDialog dialog = CommonDialog.newInstance()
                            .setTitle("取消订单")
                            .setContent("确定要取消当前订单吗？");
                    dialog.setOnCancelListener(DialogFragment::dismiss);

                    dialog.setOnConfirmListener(d -> {
                        cancelOrderPresenter.cancelOrder(order.getId());
                    });

                    dialog.show(getParentFragmentManager(), "dialog_cancelOrder");
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
                    CommonDialog dialog = CommonDialog.newInstance()
                            .setTitle("确认收货")
                            .setContent("确定商品完整无破损？");
                    dialog.setOnCancelListener(DialogFragment::dismiss);

                    dialog.setOnConfirmListener(d -> {
                        updateOrderStatePresenter.updateOrderStatus(order.getId(),3);
                    });

                    dialog.show(getParentFragmentManager(), "dialog_confirmOrder");
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
    public void onGetAllOrderSuccess(List<OrderEntity> data) {
        refreshListSuccess(data);
        onLoadMore();
    }

    @Override
    public void onGetAllOrderFailed(String errorMsg) {
        refreshListFailed("获取订单失败");
        LogUtils.error("获取订单失败："+errorMsg);
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }

    @Override
    protected boolean shouldAddDefaultSpaceDecoration() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (latitude != null) {
            autoRefresh();
        }
    }

    @Override
    public void onCancelOrderSuccess(String data) {
        ToastUtils.show(getContext(),"订单已取消");
        onRefresh();
    }

    @Override
    public void onCancelOrderFailed(String errorMsg) {
        showError("取消订单失败");
        LogUtils.error("取消订单失败："+errorMsg);
    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
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

    @Override
    public void onUpdateOrderStatusSuccess(String data) {
        ToastUtils.show(requireContext(),data);
        EventBus.getDefault().post(new RefreshEvent());
        onRefresh();
    }

    @Override
    public void onUpdateOrderStatusFailed(String errorMsg) {
        ToastUtils.show(requireContext(),errorMsg);
        LogUtils.error(errorMsg);
    }
}
