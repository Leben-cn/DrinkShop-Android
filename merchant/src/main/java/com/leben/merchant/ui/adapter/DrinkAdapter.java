package com.leben.merchant.ui.adapter;

import android.content.Context;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.merchant.R;
import java.text.DecimalFormat;

public class DrinkAdapter extends BaseRecyclerAdapter<DrinkEntity> {

    public DrinkAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_drink;
    }

    @Override
    protected void bindData(BaseViewHolder holder, DrinkEntity data, int position) {
        DecimalFormat df=new DecimalFormat("#.##");
        holder.setImageUrl(R.id.iv_drink_img,data.getImg())
                .setText(R.id.tv_drink_name,data.getName())
                .setText(R.id.tv_rating,data.getMark()+"分")
                .setText(R.id.tv_distance,data.getDistance());
        if (data.getSpecs().isEmpty()) {
            holder.setText(R.id.tv_drink_price,"￥"+df.format(data.getPrice()));
        }else{
            holder.setText(R.id.tv_drink_price,"￥"+df.format(data.getPrice())+" 起");
        }
    }
}
