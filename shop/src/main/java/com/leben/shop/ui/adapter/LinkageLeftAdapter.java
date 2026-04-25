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

    // 预定义颜色，避免频繁解析字符串
    private static final int COLOR_SELECTED = Color.parseColor("#333333");
    private static final int COLOR_NORMAL = Color.parseColor("#666666");
    private static final int BG_SELECTED = Color.WHITE;
    private static final int BG_NORMAL = Color.parseColor("#F5F5F5");

    public LinkageLeftAdapter(List<ShopCategoriesEntity> data) {
        super(data);
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item_linkage_left, parent, false));
    }

    @Override
    protected void onBind(BaseViewHolder holder, ShopCategoriesEntity item, boolean isSelected) {
        // 1. 设置文字内容
        holder.setText(R.id.tv_shop_category, item.getCategoryName());

        // 2. 样式切换优化
        TextView tvName = holder.getView(R.id.tv_shop_category);

        // 背景切换
        holder.itemView.setBackgroundColor(isSelected ? BG_SELECTED : BG_NORMAL);

        // 文字样式切换
        tvName.setTextColor(isSelected ? COLOR_SELECTED : COLOR_NORMAL);
        tvName.setTypeface(isSelected ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);

        // 指示器切换 (使用 setGone 减少 View 层级绘制压力)
        holder.setGone(R.id.v_indicator, !isSelected);
    }
}
