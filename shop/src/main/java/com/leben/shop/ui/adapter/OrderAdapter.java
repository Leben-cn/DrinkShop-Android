package com.leben.shop.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.base.util.LogUtils;
import com.leben.common.ui.adapter.PreviewOrderDrinksAdapter;
import com.leben.shop.R;
import com.leben.shop.constant.ShopConstant;
import com.leben.common.model.bean.DrinkSimpleEntity;
import com.leben.common.model.bean.OrderEntity;
import com.leben.common.model.bean.OrderItemEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.PublishSubject;

public class OrderAdapter extends BaseRecyclerAdapter<OrderEntity> {

    private OnOrderActionClickListener actionListener;

    // 1. 定义接口
    public interface OnOrderActionClickListener {
        void onCancelOrder(OrderEntity order);     // 取消订单
        void onGoToComment(OrderEntity order);     // 去评价
        void onConfirmReceipt(OrderEntity order);  // 确认收货
        void onContactShop(OrderEntity order);     // 联系商家
    }

    // 2. 暴露设置监听的方法
    public void setOnOrderActionClickListener(OnOrderActionClickListener listener) {
        this.actionListener = listener;
    }

    private final PublishSubject<OrderClickEvent> clickSubject=PublishSubject.create();

    public static class OrderClickEvent{
        public OrderEntity order;
        public OrderClickEvent(OrderEntity order){
            this.order=order;
        }
    }

    public Observable<OrderClickEvent> observableClicks(){
        return clickSubject;
    }

    public OrderAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.shop_item_order;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void bindData(BaseViewHolder holder, OrderEntity data, int position) {
        DecimalFormat df = new DecimalFormat("#.##");
        TextView btnAction = holder.getView(R.id.tv_action_btn); // 主操作按钮
        TextView btnContact = holder.getView(R.id.tv_contact_btn); // 联系商家按钮
        TextView tvPrice = holder.getView(R.id.tv_total_price);
        TextView tvCount = holder.getView(R.id.tv_total_count);
        TextView tvShopState = holder.getView(R.id.tv_order_info);

        // 店铺状态控制
        tvShopState.setVisibility(View.GONE);
        if ("暂停营业".equals(data.getShopState())) {
            tvShopState.setVisibility(View.VISIBLE);
            tvShopState.setText("暂停营业");
        }

        // 设置基础数据
        holder.setImageUrl(R.id.iv_shop_logo, data.getShopLogo(), R.drawable.pic_no_shop)
                .setText(R.id.tv_shop_name, data.getShopName())
                .setText(R.id.tv_total_price, "￥" + df.format(data.getPayAmount()))
                .setText(R.id.tv_total_count, "共" + data.getTotalQuantity() + "件");

        // 状态机处理
        btnAction.setVisibility(View.GONE);
        switch (data.getStatus()) {
            case 0: // 待制作
                holder.setText(R.id.tv_status, "待制作");
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setText("取消订单");
                break;
            case 1: // 待收货 (根据你的 case 1 修改)
                holder.setText(R.id.tv_status, "待收货");
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setText("确认收货");
                break;
            case 3: // 已完成
                holder.setText(R.id.tv_status, "已完成");
                if (!data.getComment()) {
                    btnAction.setVisibility(View.VISIBLE);
                    btnAction.setText("去评价");
                }
                break;
            case 2: // 已取消
                holder.setText(R.id.tv_status, "已取消");
                break;
        }

        // 1. 进店点击 (Logo, 名字, 箭头)
        Observable.merge(
                        RxView.clicks(holder.getView(R.id.iv_shop_logo)),
                        RxView.clicks(holder.getView(R.id.tv_shop_name)),
                        RxView.clicks(holder.getView(R.id.tv_right_arrow))
                ).throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(unit -> ARouter.getInstance().build(ShopConstant.Router.SHOP)
                        .withLong("shopId", data.getShopId()).navigation());

        // 2. 联系商家点击
        RxView.clicks(btnContact)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(unit -> {
                    if (actionListener != null) actionListener.onContactShop(data);
                });

        // 3. 主操作按钮点击逻辑切换
        RxView.clicks(btnAction)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(unit -> {
                    if (actionListener == null) return;
                    switch (data.getStatus()) {
                        case 0:
                            actionListener.onCancelOrder(data);
                            break;
                        case 1:
                            actionListener.onConfirmReceipt(data);
                            break;
                        case 3:
                            actionListener.onGoToComment(data);
                            break;
                    }
                });

        // 4. 跳转详情流 (Item整体及内部列表)
        Observable.merge(RxView.clicks(holder.itemView), RxView.clicks(tvPrice), RxView.clicks(tvCount))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(unit -> clickSubject.onNext(new OrderClickEvent(data)));

        RecyclerView rvProducts = holder.getView(R.id.rv_horizontal_items);
        PreviewOrderDrinksAdapter innerAdapter = getPreviewOrderDrinksAdapter(data);
        innerAdapter.setOnItemClickListener((item) -> clickSubject.onNext(new OrderClickEvent(data)));
        rvProducts.setAdapter(innerAdapter);
    }

    private static PreviewOrderDrinksAdapter getPreviewOrderDrinksAdapter(OrderEntity data) {
        List<DrinkSimpleEntity> previewList = new ArrayList<>();
        for (OrderItemEntity orderItem : data.getItems()) {
            DrinkSimpleEntity drinkSimpleEntity = new DrinkSimpleEntity(
                    orderItem.getProductId(),
                    orderItem.getProductName(),
                    orderItem.getPrice(),
                    orderItem.getProductImg());
            previewList.add(drinkSimpleEntity);
        }
        return new PreviewOrderDrinksAdapter(previewList);
    }
}
