package com.leben.base.ui.adapter;

import android.content.Context;
import com.leben.base.R;
import com.leben.base.ui.adapter.holder.BaseViewHolder;

public class HistoryAdapter extends BaseRecyclerAdapter<String> {


    public HistoryAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_history;
    }

    @Override
    protected void bindData(BaseViewHolder holder, String data, int position) {
        holder.setText(R.id.tv_name,data);
    }
}
