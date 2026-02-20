package com.leben.shop.ui.adapter;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.SpecOptionEntity;
import com.leben.shop.R;

import java.math.BigDecimal;
import java.util.List;

public class SpecOptionAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<SpecOptionEntity> data;
    private boolean isMultiple;
    private Runnable onUpdateListener;

    public SpecOptionAdapter(List<SpecOptionEntity> data, boolean isMultiple, Runnable onUpdateListener) {
        this.data = data;
        this.isMultiple = isMultiple;
        this.onUpdateListener = onUpdateListener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item_spec_option, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        SpecOptionEntity item = data.get(position);
        TextView tv = holder.getView(R.id.tv_name);

        String name = item.getOptionName();

        if (item.getPrice() != null && item.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            String priceStr = "¥" + item.getPrice().stripTrailingZeros().toPlainString();
            String separator = " | ";
            String fullText = name + separator + priceStr;

            SpannableString spannableString = new SpannableString(fullText);

            // 使用更浅、更柔和的红色
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF4D4F"));

            // 核心修改：变色起始位置跳过 " | "
            int startColorIndex = name.length() + separator.length();
            spannableString.setSpan(colorSpan, startColorIndex, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv.setText(spannableString);
        } else {
            tv.setText(name);
        }

        tv.setSelected(item.isSelected());

        holder.itemView.setOnClickListener(v -> {
            if (isMultiple) {
                item.setSelected(!item.isSelected());
            } else {
                if (!item.isSelected()) {
                    for (SpecOptionEntity entity : data) {
                        entity.setSelected(false);
                    }
                    item.setSelected(true);
                } else {
                    return;
                }
            }

            notifyDataSetChanged();

            if (onUpdateListener != null) {
                onUpdateListener.run();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}