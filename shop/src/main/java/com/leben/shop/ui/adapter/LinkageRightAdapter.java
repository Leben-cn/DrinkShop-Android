package com.leben.shop.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.base.util.LogUtils;
import com.leben.shop.controller.CartController; // 1. 引入控制器
import com.leben.shop.R;
import com.leben.shop.model.bean.DrinkEntity;
import com.leben.shop.model.bean.LinkageRightEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        if (viewType == 0) {
            return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_header, parent, false));
        } else {
            return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_drink, parent, false));
        }
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
        DecimalFormat df = new DecimalFormat("#.##");
        holder.setText(R.id.tv_food_name, drink.getName())
                .setText(R.id.tv_food_price, "¥" + df.format(drink.getPrice()))
                .setText(R.id.tv_food_sales, "销量: " + drink.getSalesVolume())
                .setImageUrl(R.id.iv_food_img, drink.getImg(), R.drawable.pic_no_drink);

        boolean hasSpecs = !item.getDrink().getSpecs().isEmpty();

        if (hasSpecs) {
            // 有规格 -> 显示选规格按钮
            holder.getView(R.id.tv_choose_spec).setVisibility(View.VISIBLE);
            holder.getView(R.id.ll_counter_container).setVisibility(View.GONE);

            // 选规格点击事件
            RxView.clicks(holder.itemView.findViewById(R.id.tv_choose_spec))
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .map(obj -> item.getDrink())
                    .filter(Objects::nonNull)
                    .subscribe(entity -> {
                        if (mListener != null) {
                            mListener.onAddClick(entity); // 还是回调弹窗，因为选规格不能直接加购物车
                        }
                    }, throwable -> {
                        LogUtils.error("点击事件错误: " + throwable.getMessage());
                    });
        } else {
            // 无规格 -> 显示加减计数器
            holder.getView(R.id.tv_choose_spec).setVisibility(View.GONE);
            holder.getView(R.id.ll_counter_container).setVisibility(View.VISIBLE);

            // 2. 获取当前商品在购物车的数量 (数据源来自 CartController)
            int quantity = CartController.getInstance().getProductQuantity(drink.getId());

            // 3. 根据数量控制 "减号" 和 "数字" 的显示
            // 假设你的计数文本ID是 tv_count，如果不是请修改 ID
            holder.setText(R.id.tv_count, String.valueOf(quantity));

            if (quantity > 0) {
                holder.getView(R.id.iv_sub).setVisibility(View.VISIBLE);
                holder.getView(R.id.tv_count).setVisibility(View.VISIBLE);
            } else {
                holder.getView(R.id.iv_sub).setVisibility(View.GONE);
                holder.getView(R.id.tv_count).setVisibility(View.GONE);
            }

            // 4. 处理【加号】点击
            RxView.clicks(holder.itemView.findViewById(R.id.iv_add))
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> {
                        // A. 通知控制器添加 (控制器内部会发 EventBus 给 Activity)
                        CartController.getInstance().add(drink);

                        // B. 刷新当前 Item UI (必须刷新，否则列表上的数字不会变)
                        // notifyItemChanged 比 notifyDataSetChanged 性能更好，只刷新这一行
                        notifyItemChanged(holder.getAdapterPosition());
                    }, throwable -> {
                        LogUtils.error("点击事件错误: " + throwable.getMessage());
                    });
            // 5. 处理【减号】点击
            RxView.clicks(holder.itemView.findViewById(R.id.iv_sub))
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .subscribe(obj -> {
                        // A. 通知控制器减少
                        CartController.getInstance().remove(drink);

                        // B. 刷新当前 Item UI
                        notifyItemChanged(holder.getAdapterPosition());
                    }, throwable -> {
                        LogUtils.error("点击事件错误: " + throwable.getMessage());
                    });

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}