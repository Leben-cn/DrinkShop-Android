package com.leben.shop.ui.adapter;

import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.shop.R;
import com.leben.shop.model.bean.SpecGroupEntity;
import java.util.List;

public class SpecGroupAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<SpecGroupEntity> data;
    private Runnable onPriceChangeListener;

    public SpecGroupAdapter(List<SpecGroupEntity> data, Runnable onPriceChangeListener) {
        this.data = data;
        this.onPriceChangeListener = onPriceChangeListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spec_group, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        SpecGroupEntity group = data.get(position);
        holder.setText(R.id.tv_group_name, group.groupName);

        RecyclerView rvInner = holder.getView(R.id.rv_inner_specs);

        // 【核心】使用 Flexbox 实现自动换行
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(holder.itemView.getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        rvInner.setLayoutManager(layoutManager);

        // 初始化内部 Adapter
        SpecOptionAdapter innerAdapter = new SpecOptionAdapter(
                group.options,
                group.isMultiple,
                onPriceChangeListener // 将刷新事件透传给内部
        );
        rvInner.setAdapter(innerAdapter);
    }

    @Override
    public int getItemCount() { return data == null ? 0 : data.size(); }
}