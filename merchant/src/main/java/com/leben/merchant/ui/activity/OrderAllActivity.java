package com.leben.merchant.ui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import androidx.fragment.app.DialogFragment;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.model.event.RefreshEvent;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.constant.CommonConstant;
import com.leben.common.contract.UpdateOrderStatusContract;
import com.leben.common.model.bean.OrderEntity;
import com.leben.common.presenter.UpdateOrderStatusPresenter;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.GetAllOrderContract;
import com.leben.merchant.presenter.GetAllOrderPresenter;
import com.leben.merchant.ui.adapter.OrderAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = MerchantConstant.Router.ORDER_ALL)
public class OrderAllActivity extends BaseRecyclerActivity<OrderEntity> implements GetAllOrderContract.View , UpdateOrderStatusContract.View {

    @InjectPresenter
    GetAllOrderPresenter getAllOrderPresenter;

    @InjectPresenter
    UpdateOrderStatusPresenter updateOrderStatePresenter;

    @Override
    protected BaseRecyclerAdapter<OrderEntity> createAdapter() {
        return new OrderAdapter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_ac_order;
    }

    @Override
    public void initView() {
        super.initView();
        TitleBar titleBar=findViewById(R.id.title_bar);
        titleBar.setTitle("全部订单");
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
                        updateOrderStatePresenter.updateOrderStatus(order.getId(),2);
                    });

                    dialog.show(getSupportFragmentManager(), "dialog_cancelOrder");
                }

                @Override
                public void onCompleteOrder(OrderEntity order) {
                    CommonDialog dialog = CommonDialog.newInstance()
                            .setTitle("确认出餐")
                            .setContent("确认已完成用户所有商品？");
                    dialog.setOnCancelListener(DialogFragment::dismiss);

                    dialog.setOnConfirmListener(d -> {
                        updateOrderStatePresenter.updateOrderStatus(order.getId(),1);
                    });

                    dialog.show(getSupportFragmentManager(), "dialog_completeOrder");
                }
                @Override
                public void onContactUser(OrderEntity order) {
                    String roleStr = "USER";
                    ARouter.getInstance()
                            .build(CommonConstant.Router.CHAT_DETAIL)
                            .withLong("targetId", order.getUserId()) // 注意：这里传的是对方真实的 ID
                            .withString("targetName", order.getReceiverName())
                            .withString("targetRoleStr", roleStr) // 传字符串
                            .withString("targetIcon", order.getReceiverImg())
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
        autoRefresh();
    }

    @Override
    public void onRefresh() {
        getAllOrderPresenter.getAllOrder();
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
        autoRefresh();
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    public void onUpdateOrderStatusSuccess(String data) {
        ToastUtils.show(this,data);
        EventBus.getDefault().post(new RefreshEvent());
        onRefresh();
    }

    @Override
    public void onUpdateOrderStatusFailed(String errorMsg) {
        ToastUtils.show(this,errorMsg);
        LogUtils.error(errorMsg);
    }
}
