package com.leben.merchant.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.GroupEntity;
import com.leben.common.model.bean.SpecGroupEntity;
import com.leben.common.model.bean.SpecOptionEntity;
import com.leben.merchant.R;
import java.util.List;

public class SpecGroupAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<GroupEntity<SpecGroupEntity, SpecOptionEntity>> data;

    public SpecGroupAdapter(List<GroupEntity<SpecGroupEntity, SpecOptionEntity>> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = android.view.LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_item_spec_group, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        GroupEntity<SpecGroupEntity, SpecOptionEntity> groupEntity = data.get(position);

        SpecGroupEntity key = groupEntity.getHeader();
        List<SpecOptionEntity> options = groupEntity.getChildren();

        holder.setText(R.id.tv_group_name, key.groupName);

        RecyclerView rvInner = holder.getView(R.id.rv_inner_specs);
        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(holder.itemView.getContext());
        layoutManager.setFlexDirection(FlexDirection.ROW);
        layoutManager.setFlexWrap(FlexWrap.WRAP);
        rvInner.setLayoutManager(layoutManager);

        SpecOptionAdapter innerAdapter = new SpecOptionAdapter(options);
        rvInner.setAdapter(innerAdapter);
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }
}