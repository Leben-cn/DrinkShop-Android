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

        holder.setImageUrl(R.id.iv_shop_logo, data.getShopLogo(), R.drawable.pic_no_drink)
                .setText(R.id.tv_shop_name, data.getShopName())
                .setText(R.id.tv_total_price,"￥"+df.format(data.getPayAmount()));


        switch (data.getStatus()){
            case 0:
                holder.setText(R.id.tv_status,"待制作");
                holder.getView(R.id.tv_action_btn).setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_action_btn,"取消订单");
                break;
            case 1:
                holder.setText(R.id.tv_status,"已完成");
                if(!data.getComment()){
                    holder.getView(R.id.tv_action_btn).setVisibility(View.VISIBLE);
                    holder.setText(R.id.tv_action_btn,"去评价");
                }else{
                    holder.getView(R.id.tv_action_btn).setVisibility(View.GONE);
                }
                break;
            case 2:
                holder.setText(R.id.tv_status,"已取消");
                holder.getView(R.id.tv_action_btn).setVisibility(View.GONE);
                break;
            case 404:holder.setText(R.id.tv_order_info,"已下架");break;
            case 500:holder.setText(R.id.tv_order_info,"暂停营业");break;
            case 1000:holder.setText(R.id.tv_order_info,"超出配送范围");break;
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

        RxView.clicks(holder.getView(R.id.tv_right_arrow))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit->{
                    ARouter.getInstance()
                            .build(ShopConstant.Router.SHOP)
                            .withLong("shopId", data.getShopId())
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件流出错: " + throwable.getMessage());
                });
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
