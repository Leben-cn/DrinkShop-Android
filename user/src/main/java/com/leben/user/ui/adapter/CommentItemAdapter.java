package com.leben.user.ui.adapter;

import android.content.Context;
import android.widget.RatingBar;

import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.OrderItemEntity;
import com.leben.user.R;

import java.util.HashMap;
import java.util.Map;

public class CommentItemAdapter extends BaseRecyclerAdapter<OrderItemEntity> {

    // 用来存评分：Key=商品ID, Value=分数(1-5)
    private final Map<Long, Integer> ratingMap = new HashMap<>();

    public CommentItemAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_comment_product;
    }

    @Override
    protected void bindData(BaseViewHolder holder, OrderItemEntity data, int position) {
        holder.setImageUrl(R.id.iv_product_img,data.getProductImg(), com.leben.common.R.drawable.pic_no_drink)
                .setText(R.id.tv_product_name, data.getProductName());

        RatingBar ratingBar = holder.getView(R.id.rating_bar);
        // 1. 防止复用导致评分错乱，先设置当前保存的分数
        if (!ratingMap.containsKey(data.getProductId())) {
            ratingMap.put(data.getProductId(), 5); // 默认5星
        }
        // 注意：RatingBar监听器可能会因为recyclerView滚动被触发，所以先移除监听再设置值
        ratingBar.setOnRatingBarChangeListener(null);
        ratingBar.setRating(ratingMap.get(data.getProductId()));

        // 2. 监听用户打分
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser) {
                if (rating < 1.0f) { // 强制最少1星
                    bar.setRating(1.0f);
                    rating = 1.0f;
                }
                ratingMap.put(data.getProductId(), (int) rating);
            }
        });
    }

    // 给 Activity 调用：获取所有商品的评分
    public Map<Long, Integer> getRatingResults() {
        return ratingMap;
    }
}
