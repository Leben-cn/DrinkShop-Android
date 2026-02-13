package com.leben.shop.ui.adapter;

import android.view.View;
import android.widget.TextView;
import com.leben.shop.R;
import com.leben.shop.model.bean.SpecOptionEntity;
import java.util.List;

// 这里用原生 Adapter 写法演示，方便复制
public class SpecOptionAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.leben.base.ui.adapter.holder.BaseViewHolder> {

    private List<SpecOptionEntity> data;
    private boolean isMultiple; // 是否多选
    private Runnable onUpdateListener; // 通知外部刷新价格

    public SpecOptionAdapter(List<SpecOptionEntity> data, boolean isMultiple, Runnable onUpdateListener) {
        this.data = data;
        this.isMultiple = isMultiple;
        this.onUpdateListener = onUpdateListener;
    }

    @Override
    public com.leben.base.ui.adapter.holder.BaseViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        View view = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spec_option, parent, false);
        return new com.leben.base.ui.adapter.holder.BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(com.leben.base.ui.adapter.holder.BaseViewHolder holder, int position) {
        SpecOptionEntity item = data.get(position);
        TextView tv = holder.getView(R.id.tv_name);
        tv.setText(item.name);

        // 【核心】设置 View 的选中状态，触发 XML selector 变色
        tv.setSelected(item.isSelected);
        if(item.isSelected) {
            tv.setTextColor(0xFFFF0000); // 选中红字 (可用资源色代替)
        } else {
            tv.setTextColor(0xFF333333); // 未选中黑字
        }

        holder.itemView.setOnClickListener(v -> {
            if (isMultiple) {
                // 多选：取反
                item.isSelected = !item.isSelected;
            } else {
                // 单选：其他的全灭，我亮
                if (!item.isSelected) { // 如果已经选中了就不重复操作
                    for (SpecOptionEntity entity : data) {
                        entity.isSelected = false;
                    }
                    item.isSelected = true;
                }
            }
            notifyDataSetChanged();
            if (onUpdateListener != null) onUpdateListener.run();
        });
    }

    @Override
    public int getItemCount() { return data == null ? 0 : data.size(); }
}