package com.leben.common.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.R;
import com.leben.common.model.bean.DrinkSimpleEntity;
import com.leben.common.util.PriceUtils;

import java.text.DecimalFormat;
import java.util.List;

public class PreviewDrinksAdapter extends RecyclerView.Adapter<BaseViewHolder>{

    private List<DrinkSimpleEntity> mData;

    private OnItemClickListener mListener;

    public PreviewDrinksAdapter(List<DrinkSimpleEntity> data) {
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
                .inflate(R.layout.common_item_drink_simple, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        DrinkSimpleEntity item=mData.get(position);
        holder.setText(R.id.tv_product_name,item.getName())
                .setImageUrl(R.id.iv_product_img,item.getImg(),R.drawable.pic_no_drink);

        TextView tvPrice = holder.getView(R.id.tv_product_price);
        tvPrice.setText(PriceUtils.formatPrice(item.getPrice(), false));

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
