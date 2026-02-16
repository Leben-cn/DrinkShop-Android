package com.leben.merchant.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.DrinkSimpleEntity;
import com.leben.common.model.bean.OrderEntity;
import com.leben.common.model.bean.OrderItemEntity;
import com.leben.common.ui.adapter.PreviewOrderDrinksAdapter;
import com.leben.merchant.R;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class OrderAdapter extends BaseRecyclerAdapter<OrderEntity> {

    private OnOrderActionClickListener actionListener;

    // 1. 定义接口
    public interface OnOrderActionClickListener {
        void onCancelOrder(OrderEntity order);   // 点击取消订单
        void onGoToComment(OrderEntity order);   // 点击去评价
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
        return R.layout.item_order;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void bindData(BaseViewHolder holder, OrderEntity data, int position) {

        DecimalFormat df=new DecimalFormat("#.##");
        TextView btnAction = holder.getView(R.id.tv_action_btn);
        holder.setText(R.id.tv_total_price,"￥"+df.format(data.getPayAmount()));


        switch (data.getStatus()){
            case 0:
                holder.getView(R.id.tv_action_btn).setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_action_btn,"已出餐");
                break;
            case 1:
                holder.setText(R.id.tv_status,"已完成");
                break;
            case 2:
                holder.getView(R.id.tv_action_btn).setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_action_btn,"查看详情");
                break;
        }

        RecyclerView rvProducts = holder.getView(R.id.rv_horizontal_items);

        // 2. 【核心修改】先获取 Adapter 实例，不要直接 setAdapter
        PreviewOrderDrinksAdapter innerAdapter = getPreviewOrderDrinksAdapter(data);

        innerAdapter.setOnItemClickListener((item) -> {
            // 当点击内部的小商品图时，触发外部的订单点击事件流
            clickSubject.onNext(new OrderClickEvent(data));
        });

        rvProducts.setAdapter(innerAdapter);


        // 4. 【建议】同时给整个 CardView (holder.itemView) 也加上点击
        // 这样点击商品列表旁边的空白处也能跳转
        RxView.clicks(holder.itemView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .map(o -> new OrderClickEvent(data))
                .subscribe(clickSubject);

        btnAction.setOnClickListener(v -> {
            if (actionListener == null) {
                return;
            }
            // 根据状态判断执行哪个回调
            int status = data.getStatus();

            if (status == 0) {
                // 状态 0: 待制作 -> 执行取消逻辑
                actionListener.onCancelOrder(data);

            } else if (status == 1) {
                // 状态 1: 已完成 -> 执行评价逻辑
                actionListener.onGoToComment(data);
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
