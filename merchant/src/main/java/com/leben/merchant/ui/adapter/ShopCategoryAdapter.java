package com.leben.merchant.ui.adapter;

import android.content.Context;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.ShopCategoriesEntity;
import com.leben.merchant.R;

public class ShopCategoryAdapter extends BaseRecyclerAdapter<ShopCategoriesEntity> {

    private OnItemDeleteListener mDeleteListener;

    public interface OnItemDeleteListener {
        void onDeleteClick(Long id);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.mDeleteListener = listener;
    }

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

        // 绑定删除按钮点击事件，传递 ID
        holder.setOnClickListener(R.id.iv_delete, v -> {
            if (mDeleteListener != null && data.getId() != null) {
                mDeleteListener.onDeleteClick(data.getId());
            }
        });
    }
}
