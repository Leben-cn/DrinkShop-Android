package com.leben.shop.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.base.util.LogUtils;
import com.leben.common.util.PriceUtils;
import com.leben.shop.controller.CartController;
import com.leben.shop.R;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.shop.model.bean.LinkageRightEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LinkageRightAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private OnGoodsAddListener mListener;
    private List<LinkageRightEntity> mData;

    public LinkageRightAdapter(List<LinkageRightEntity> data) {
        this.mData = data == null ? new ArrayList<>() : data;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setNewData(List<LinkageRightEntity> newData) {
        this.mData = newData;
        notifyDataSetChanged();
    }

    public interface OnGoodsAddListener {
        void onAddClick(DrinkEntity drink);
    }

    public void setOnGoodsAddListener(OnGoodsAddListener listener) {
        this.mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).isHeader() ? 0 : 1;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (viewType == 0) ? R.layout.shop_item_menu_header : R.layout.shop_item_menu_drink;
        return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false));
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        LinkageRightEntity item = mData.get(position);

        if (item.isHeader()) {
            holder.setText(R.id.tv_user_header, item.getHeaderName());
            return;
        }

        DrinkEntity drink = item.getDrink();

        // 1. 基本信息展示
        holder.setText(R.id.tv_food_name, drink.getName())
                .setImageUrl(R.id.iv_food_img, drink.getImg(), R.drawable.pic_no_drink);

        String sales = (drink.getSalesVolume() == null) ? "0" : String.valueOf(drink.getSalesVolume());
        holder.setText(R.id.tv_food_sales, "月售 " + sales);

        // 2. 价格处理：直接一行调用 Util
        boolean hasSpecs = drink.getSpecs() != null && !drink.getSpecs().isEmpty();
        TextView tvPrice = holder.getView(R.id.tv_food_price);
        tvPrice.setText(PriceUtils.formatPrice(drink.getPrice(), hasSpecs));

        // 3. UI 状态切换
        if (hasSpecs) {
            holder.setGone(R.id.ll_counter_container, true);
            holder.setVisible(R.id.tv_choose_spec, true);

            RxView.clicks(holder.getView(R.id.tv_choose_spec))
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> {
                        if (mListener != null) mListener.onAddClick(drink);
                    }, throwable -> LogUtils.error(throwable.getMessage()));
        } else {
            holder.setGone(R.id.tv_choose_spec, true);
            holder.setVisible(R.id.ll_counter_container, true);

            // 计数逻辑
            int quantity = CartController.getInstance().getProductQuantity(drink.getId());
            holder.setText(R.id.tv_count, String.valueOf(quantity));
            holder.setVisible(R.id.iv_sub, quantity > 0);
            holder.setVisible(R.id.tv_count, quantity > 0);

            // 加/减 点击事件
            RxView.clicks(holder.getView(R.id.iv_add))
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> {
                        CartController.getInstance().add(drink);
                        notifyItemChanged(holder.getAdapterPosition());
                    }, throwable -> LogUtils.error(throwable.getMessage()));

            RxView.clicks(holder.getView(R.id.iv_sub))
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> {
                        CartController.getInstance().remove(drink);
                        notifyItemChanged(holder.getAdapterPosition());
                    }, throwable -> LogUtils.error(throwable.getMessage()));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}