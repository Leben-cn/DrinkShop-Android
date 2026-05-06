package com.leben.shop.ui.adapter;

import android.content.Context;

import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.util.PriceUtils;
import com.leben.shop.R;
import com.leben.common.model.bean.DrinkEntity;

import java.text.DecimalFormat;

public class DrinkAdapter extends BaseRecyclerAdapter<DrinkEntity> {

    public DrinkAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.shop_item_display_drink;
    }

    @Override
    protected void bindData(BaseViewHolder holder, DrinkEntity data, int position) {

        holder.setImageUrl(R.id.iv_display_drink_img, data.getImg())
                .setText(R.id.tv_display_drink_name, data.getName())
                .setText(R.id.tv_display_rating, data.getMark() + "分")
                .setText(R.id.tv_display_distance, data.getDistance())
                .setText(R.id.tv_display_sales,"月售 "+data. getSalesVolume())
                .setText(R.id.tv_display_shop_name,data.getShopName());

        if (data.getMark() == null) {
            holder.setText(R.id.tv_display_rating, "5.0分");
        }

        holder.setText(R.id.tv_display_drink_price, PriceUtils.formatPrice(data.getPrice(), data.getSpecs().isEmpty(), true, false));

    }
}
