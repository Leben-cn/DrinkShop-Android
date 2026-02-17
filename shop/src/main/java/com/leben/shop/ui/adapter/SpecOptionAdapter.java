package com.leben.shop.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.SpecOptionEntity;
import com.leben.shop.R;

import java.util.List;

public class SpecOptionAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<SpecOptionEntity> data;
    private boolean isMultiple; // 是否支持多选
    private Runnable onUpdateListener; // 回调给外部计算价格

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
        tv.setText(item.optionName); // 注意：确保这里取的是 optionName

        // 设置选中状态
        tv.setSelected(item.isSelected);

        holder.itemView.setOnClickListener(v -> {
            if (isMultiple) {
                // --- 多选逻辑 ---
                item.isSelected = !item.isSelected;
            } else {
                // --- 单选逻辑 ---
                if (!item.isSelected) {
                    // 1. 先把同组其他的都取消选中
                    for (SpecOptionEntity entity : data) {
                        entity.isSelected = false;
                    }
                    // 2. 选中当前点击的
                    item.isSelected = true;
                } else {
                    // 如果已经是选中状态，单选模式下通常不允许取消(必选一项)，所以啥也不做
                    // 如果允许反选（变成都不选），可以在这里写 item.isSelected = false;
                    return;
                }
            }

            notifyDataSetChanged();

            // 通知外部 Dialog 重新计算总价
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