package com.leben.shop.ui.adapter;

import android.content.Context;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.shop.R;
import com.leben.shop.model.bean.CartEntity;

import java.text.DecimalFormat;

public class PayAdapter extends BaseRecyclerAdapter<CartEntity> {

    public PayAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.shop_item_pay_product;
    }

    @Override
    protected void bindData(BaseViewHolder holder, CartEntity data, int position) {
        DecimalFormat df=new DecimalFormat("#.##");
        holder.setImageUrl(R.id.iv_product_img, data.getDrink().getImg(), R.drawable.pic_no_drink)
                .setText(R.id.tv_product_name, data.getDrink().getName())
                .setText(R.id.tv_count, "x " + data.getQuantity())
                .setText(R.id.tv_price, "¥ " + df.format(data.getItemTotalPrice())); // 这里显示该项的总价

        if (data.getSpecDesc() != null && !data.getSpecDesc().isEmpty()) {
            holder.setText(R.id.tv_product_spec, data.getSpecDesc());
        } else {
            holder.setText(R.id.tv_product_spec,"默认规格");
        }
    }
}