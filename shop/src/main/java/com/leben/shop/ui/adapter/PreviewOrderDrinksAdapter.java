package com.leben.shop.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.shop.R;
import com.leben.common.model.bean.DrinkSimpleEntity;

import java.util.List;

public class PreviewOrderDrinksAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private List<DrinkSimpleEntity> mData;

    private OnItemClickListener mListener;

    public PreviewOrderDrinksAdapter(List<DrinkSimpleEntity> data) {
        this.mData = data;
    }

    public interface OnItemClickListener {
        void onItemClick(DrinkSimpleEntity drink);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_drink_simple, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        DrinkSimpleEntity item=mData.get(position);
        holder.setText(R.id.tv_product_name,item.getName())
                .setImageUrl(R.id.iv_product_img,item.getImg(),R.drawable.pic_no_drink);

        holder.itemView.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }
}
