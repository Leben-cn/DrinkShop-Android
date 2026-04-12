package com.leben.merchant.ui.adapter;

import android.content.Context;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.OrderEntity;
import com.leben.merchant.R;

/**
 * Created by youjiahui on 2026/4/12.
 */

public class BillAdapter extends BaseRecyclerAdapter<OrderEntity> {

    public BillAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.merchant_item_bill;
    }

    @Override
    protected void bindData(BaseViewHolder holder, OrderEntity data, int position) {
        holder.setImageUrl(R.id.iv_bill_avatar,data.getReceiverImg(), com.leben.common.R.drawable.pic_no_drink)
                .setText(R.id.tv_bill_merchant,data.getReceiverName())
                .setText(R.id.tv_bill_time,data.getCreateTime())
                .setText(R.id.tv_bill_amount,"+"+data.getPayAmount());
    }
}
