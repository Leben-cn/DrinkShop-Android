package com.leben.merchant.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.base.util.LogUtils;
import com.leben.common.model.bean.DrinkSimpleEntity;
import com.leben.common.model.bean.OrderEntity;
import com.leben.common.model.bean.OrderItemEntity;
import com.leben.common.ui.adapter.PreviewOrderDrinksAdapter;
import com.leben.common.util.PriceUtils;
import com.leben.merchant.R;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class OrderAdapter extends BaseRecyclerAdapter<OrderEntity> {

    private OnOrderActionClickListener actionListener;

    public interface OnOrderActionClickListener {
        void onCancelOrder(OrderEntity order);   // 点击取消订单
        void onCompleteOrder(OrderEntity order);   // 点击出餐
        void onContactUser(OrderEntity order);
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
        return R.layout.merchant_item_order;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void bindData(BaseViewHolder holder, OrderEntity data, int position) {
        // 1. 获取 View 引用
        TextView btnAction = holder.getView(R.id.tv_action_btn);
        TextView btnCancel = holder.getView(R.id.tv_cancel_btn);
        TextView tvPrice = holder.getView(R.id.tv_total_price);
        TextView tvCount = holder.getView(R.id.tv_total_count);
        RecyclerView rvProducts = holder.getView(R.id.rv_horizontal_items);
        TextView btnContact = holder.getView(R.id.tv_contact_btn);

        // 2. 基础数据绑定
        holder.setText(R.id.tv_total_price, PriceUtils.formatPrice(data.getPayAmount(), false, false, true))
                .setImageUrl(R.id.iv_user_avatar, data.getReceiverImg())
                .setText(R.id.tv_user_name, data.getReceiverName())
                .setText(R.id.tv_order_time, data.getCreateTime())
                .setText(R.id.tv_total_count, "共" + data.getTotalQuantity() + "件");

        // 3. 状态与按钮控制
        btnCancel.setVisibility(View.GONE);
        btnAction.setVisibility(View.GONE); // 默认隐藏，防止 Item 复用导致错乱

        switch (data.getStatus()) {
            case 0: // 制作中
                holder.setText(R.id.tv_status, "制作中");
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setText("确认出餐");
                btnCancel.setVisibility(View.VISIBLE);
                break;
            case 1: // 已完成
                holder.setText(R.id.tv_status, "已完成");
                break;
            case 2: // 已取消
                holder.setText(R.id.tv_status, "已取消");
                btnAction.setVisibility(View.VISIBLE);
                btnAction.setBackgroundResource(R.drawable.bg_btn_white_square);
                btnAction.setText("查看详情");
                break;
        }

        // 4. 处理内部 RecyclerView (商品图片列表)
        PreviewOrderDrinksAdapter innerAdapter = getPreviewOrderDrinksAdapter(data);
        innerAdapter.setOnItemClickListener((item) -> {
            // 点击内部图片也触发跳转详情
            clickSubject.onNext(new OrderClickEvent(data));
        });
        rvProducts.setAdapter(innerAdapter);

        // 5. 整合 Rx 点击事件流（用于跳转详情）
        List<Observable<Object>> clickObservables = new ArrayList<>();
        clickObservables.add(RxView.clicks(holder.itemView)); // 点击卡片空白处
        clickObservables.add(RxView.clicks(tvPrice));        // 点击价格
        clickObservables.add(RxView.clicks(tvCount));        // 点击数量

        // 重要：当按钮是“查看详情”时，将其点击也加入跳转详情流
        if (data.getStatus() == 2) {
            clickObservables.add(RxView.clicks(btnAction));
        }

        Observable.merge(clickObservables)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .map(unit -> new OrderClickEvent(data))
                .subscribe(clickSubject::onNext, throwable -> LogUtils.error(throwable.getMessage()));

        // 6. 业务按钮逻辑处理（使用普通 Click 监听避免 RxBinding 冲突）
        btnAction.setOnClickListener(v -> {
            if (actionListener == null) return;

            int status = data.getStatus();
            if (status == 0) {
                // 制作中 -> 确认出餐
                actionListener.onCompleteOrder(data);
            } else if (status == 2) {
                // 已取消 -> 查看详情 (手动触发一次跳转流)
                clickSubject.onNext(new OrderClickEvent(data));
            }
        });

        btnCancel.setOnClickListener(v -> {
            if (actionListener != null && data.getStatus() == 0) {
                // 制作中 -> 取消订单
                actionListener.onCancelOrder(data);
            }
        });

        btnContact.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onContactUser(data);
            }
        });
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
