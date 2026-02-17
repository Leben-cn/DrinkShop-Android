package com.leben.shop.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.GroupEntity;
import com.leben.common.model.bean.SpecGroupEntity; // 刚才定义的 Key
import com.leben.common.model.bean.SpecOptionEntity;
import com.leben.shop.R;

import java.util.List;

public class SpecGroupAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    // 数据源泛型修改：Key 是 SpecGroupEntity
    private List<GroupEntity<SpecGroupEntity, SpecOptionEntity>> data;
    private Runnable onPriceChangeListener;

    public SpecGroupAdapter(List<GroupEntity<SpecGroupEntity, SpecOptionEntity>> data, Runnable onPriceChangeListener) {
        this.data = data;
        this.onPriceChangeListener = onPriceChangeListener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_spec_group, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        GroupEntity<SpecGroupEntity, SpecOptionEntity> groupEntity = data.get(position);

        // 1. 获取 Key (包含了组名、多选状态)
        SpecGroupEntity key = groupEntity.getHeader(); // 注意你的 GroupEntity 方法名是 getHeader 还是 getKey
        List<SpecOptionEntity> options = groupEntity.getChildren();

        // 2. 设置 UI
        holder.setText(R.id.tv_group_name, key.groupName);

        // 3. 设置内部列表
        RecyclerView rvInner = holder.getView(R.id.rv_inner_specs);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(holder.itemView.getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        rvInner.setLayoutManager(layoutManager);

        // 4. 初始化内部 Adapter，传入多选状态
        SpecOptionAdapter innerAdapter = new SpecOptionAdapter(
                options,
                key.isMultiple, // 从 Key 中获取是否多选
                onPriceChangeListener
        );
        rvInner.setAdapter(innerAdapter);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}