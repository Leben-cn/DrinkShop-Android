package com.leben.common.ui.adapter;

import android.content.Context;
import android.view.View;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.base.util.ConvertUtils;
import com.leben.common.R;
import com.leben.common.model.bean.CommentEntity;

public class CommentAdapter extends BaseRecyclerAdapter<CommentEntity> {

    // 1. 定义页面类型常量
    public static final int TYPE_MY_COMMENT = 1;   // "我的评价"页面 (显示商家信息，可能有删除按钮)
    public static final int TYPE_SHOP_COMMENT = 2; // "商家详情"页面 (显示用户信息)

    private int pageType;

    public CommentAdapter(Context context,int pageType) {
        super(context);
        this.pageType = pageType;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.common_item_comment;
    }

    @Override
    protected void bindData(BaseViewHolder holder, CommentEntity data, int position) {
        Context context = holder.itemView.getContext();
        ShapeableImageView ivAvatar = holder.getView(R.id.iv_avatar);
        if (pageType == TYPE_MY_COMMENT) {
            // === 场景A：在“我的评价”列表 ===
            // 应该显示：我评价了【哪个商家】

            // 1. 显示商家头像和名称
            holder.setImageUrl(R.id.iv_avatar, data.getMerchantAvatar());
            holder.setText(R.id.tv_user_merchant_name, data.getMerchantName()); // 复用 username 位置显示商家名

            // 2. 特殊逻辑：比如我的评价可以删除
            // holder.getView(R.id.btn_delete).setVisibility(View.VISIBLE);

            ShapeAppearanceModel roundedModel = ivAvatar.getShapeAppearanceModel()
                    .toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, ConvertUtils.toPx(context,4)) // 8dp 的圆角
                    .build();
            ivAvatar.setShapeAppearanceModel(roundedModel);

        } else if (pageType == TYPE_SHOP_COMMENT) {
            // === 场景B：在“商家详情”下面的评价列表 ===
            // 应该显示：【哪个用户】评价了本商家

            // 1. 显示用户头像和名称
            holder.setImageUrl(R.id.iv_avatar, data.getUserAvatar());
            holder.setText(R.id.tv_user_merchant_name, data.getUserName());

            // 2. 特殊逻辑：商家页面不能删除别人的评论
            // holder.getView(R.id.btn_delete).setVisibility(View.GONE);
            ShapeAppearanceModel circleModel = ivAvatar.getShapeAppearanceModel()
                    .toBuilder()
                    .setAllCornerSizes(ShapeAppearanceModel.PILL) // 或者 setAllCornerSizes(50%)
                    .build();
            ivAvatar.setShapeAppearanceModel(circleModel);
        }
        if(data.getPicture()!=null){
            holder.getView(R.id.iv_comment_pic).setVisibility(View.VISIBLE);
            holder.setImageUrl(R.id.iv_comment_pic,data.getPicture());
        }else{
            holder.getView(R.id.iv_comment_pic).setVisibility(View.GONE);
        }
        holder.setText(R.id.tv_content,data.getContent())
                .setText(R.id.tv_date,data.getCreateTime())
                .setText(R.id.tv_product_names,data.getProductName())
                .setRating(R.id.rb_score, data.getScore());
    }
}
