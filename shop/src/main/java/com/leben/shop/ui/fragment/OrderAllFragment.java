package com.leben.shop.ui.fragment;

import android.annotation.SuppressLint;

import androidx.fragment.app.DialogFragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.common.LocationManager;
import com.leben.shop.R;
import com.leben.shop.constant.ShopConstant;
import com.leben.shop.contract.CancelOrderContract;
import com.leben.shop.contract.GetAllOrderContract;
import com.leben.common.model.bean.OrderEntity;
import com.leben.shop.presenter.CancelOrderPresenter;
import com.leben.shop.presenter.GetAllOrderPresenter;
import com.leben.shop.ui.adapter.OrderAdapter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class OrderAllFragment extends BaseRecyclerFragment<OrderEntity> implements GetAllOrderContract.View,
        CancelOrderContract.View {

    @InjectPresenter
    GetAllOrderPresenter getAllOrderPresenter;

    @InjectPresenter
    CancelOrderPresenter cancelOrderPresenter;

    private Double latitude;
    private Double longitude;

    @Override
    protected BaseRecyclerAdapter<OrderEntity> createAdapter() {
        return new OrderAdapter(getContext());
    }

    @Override
    public void onRefresh() {
        getAllOrderPresenter.getAllOrder(latitude,longitude);
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

                    dialog.show(getParentFragmentManager(), "cancelOrder_tag");
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
    public void onGetAllOrderSuccess(List<OrderEntity> data) {
        refreshListSuccess(data);
        onLoadMore();
    }

    @Override
    public void onGetAllOrderFailed(String errorMsg) {
        refreshListFailed(errorMsg);
        showError("获取订单失败");
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
        autoRefresh();
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
}
