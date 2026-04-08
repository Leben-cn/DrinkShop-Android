package com.leben.shop.ui.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.shop.R;
import com.leben.common.model.bean.ShopCategoriesEntity;
import java.util.List;

public class LinkageLeftAdapter extends com.leben.base.widget.linkage.LinkageLeftAdapter<ShopCategoriesEntity, BaseViewHolder> {

    public LinkageLeftAdapter(List<ShopCategoriesEntity> data) {
        super(data);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_item_linkage_left,parent,false));
    }

    @Override
    protected void onBind(BaseViewHolder holder, ShopCategoriesEntity item, boolean isSelected) {
        TextView tvName = holder.getView(R.id.tv_shop_category);
        View vIndicator = holder.getView(R.id.v_indicator);

        tvName.setText(item.getCategoryName());

        if (isSelected) {
            holder.itemView.setBackgroundColor(Color.WHITE);
            tvName.setTextColor(Color.parseColor("#333333"));
            tvName.setTypeface(Typeface.DEFAULT_BOLD);
            if(vIndicator != null) vIndicator.setVisibility(View.VISIBLE);
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#F5F5F5"));
            tvName.setTextColor(Color.parseColor("#666666"));
            tvName.setTypeface(Typeface.DEFAULT);
            if(vIndicator != null) vIndicator.setVisibility(View.GONE);
        }
    }

}
