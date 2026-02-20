package com.leben.merchant.ui.adapter;

import android.content.Context;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.ShopCategoriesEntity;
import com.leben.merchant.R;

public class ShopCategoryAdapter extends BaseRecyclerAdapter<ShopCategoriesEntity> {

    public ShopCategoryAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.merchant_item_shop_category;
    }

    @Override
    protected void bindData(BaseViewHolder holder, ShopCategoriesEntity data, int position) {

        holder.setText(R.id.tv_category_name, data.getName())
                .setText(R.id.tv_drink_num, String.valueOf(data.getDrinkNum()));

    }
}
