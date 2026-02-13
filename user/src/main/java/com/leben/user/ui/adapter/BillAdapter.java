package com.leben.user.ui.adapter;

import android.content.Context;

import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.user.R;
import com.leben.user.model.bean.BillEntity;

public class BillAdapter extends BaseRecyclerAdapter<BillEntity> {

    public BillAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_bill;
    }

    @Override
    protected void bindData(BaseViewHolder holder, BillEntity data, int position) {
        holder.setImageUrl(R.id.iv_bill_avatar,data.getMerchantAvatar(), com.leben.common.R.drawable.pic_no_drink)
                .setText(R.id.tv_bill_merchant,data.getMerchantName())
                .setText(R.id.tv_bill_time,data.getCreateTime())
                .setText(R.id.tv_bill_amount,"-"+data.getTotalPrice());
    }
}
