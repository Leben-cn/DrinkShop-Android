package com.leben.shop.ui.adapter;

import android.content.Context;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.shop.controller.CartController;
import com.leben.shop.R;
import com.leben.shop.model.bean.CartEntity;

import java.text.DecimalFormat;

public class CartAdapter extends BaseRecyclerAdapter<CartEntity> {

    public CartAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_cart_product;
    }

    @Override
    protected void bindData(BaseViewHolder holder, CartEntity data, int position) {
        DecimalFormat df=new DecimalFormat("#.##");
        holder.setImageUrl(R.id.iv_product_img,data.getDrink().getImg(),R.drawable.pic_no_drink)
                .setText(R.id.tv_product_name,data.getDrink().getName())
                .setText(R.id.tv_product_price, df.format(data.getCurrentPrice()))
                .setText(R.id.tv_count,String.valueOf(data.getQuantity()));

        if (data.getSpecDesc() != null && !data.getSpecDesc().isEmpty()) {
            holder.setText(R.id.tv_product_spec, data.getSpecDesc());
        } else {
            holder.setText(R.id.tv_product_spec,"默认规格");
        }

        holder.getView(R.id.iv_add).setOnClickListener(v -> {
            // 注意：带规格的商品添加，需要把规格参数传进去
            CartController.getInstance().add(
                    data.getDrink(),
                    data.getSpecs(),
                    data.getCurrentPrice(),
                    data.getSpecDesc()
            );
            notifyItemChanged(holder.getAdapterPosition());
        });
        // 点击事件：减号
        holder.getView(R.id.iv_sub).setOnClickListener(v -> {
            // 这里需要 CartController 实现一个 remove(CartEntity item) 的方法
            // 或者 remove(key)
            CartController.getInstance().remove(data);
            // 如果数量归零，可能需要刷新整个列表
            if (data.getQuantity() <= 0) {
                notifyDataSetChanged(); // 移除整行
            } else {
                notifyItemChanged(holder.getAdapterPosition());
            }
        });


    }
}
