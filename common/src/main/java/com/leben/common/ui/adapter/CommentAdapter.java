package com.leben.common.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

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
    public static final int TYPE_MANAGEMENT_COMMENT=3;//商家管理评价页面
    public static final int TYPE_WAIT_REVIEW=4;//商家待审核评论

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
            holder.setText(R.id.tv_user_merchant_name, data.getMerchantName());

            TextView tvStatus=holder.getView(R.id.tv_status);
            tvStatus.setVisibility(View.GONE);

            if (data.getStatus() == 0) {
                holder.getView(R.id.tv_status).setVisibility(View.VISIBLE);
                holder.getView(R.id.tv_status).setBackgroundResource(R.drawable.bg_status_warning);
            }

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

            TextView tvStatus=holder.getView(R.id.tv_status);
            tvStatus.setVisibility(View.GONE);

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

        }else if(pageType== TYPE_MANAGEMENT_COMMENT){
            TextView tvStatus=holder.getView(R.id.tv_status);
            tvStatus.setVisibility(View.GONE);
            holder.setImageUrl(R.id.iv_avatar, data.getUserAvatar());
            holder.setText(R.id.tv_user_merchant_name, data.getUserName());

        } else if (pageType==TYPE_WAIT_REVIEW) {
            holder.setImageUrl(R.id.iv_avatar, data.getUserAvatar());
            holder.setText(R.id.tv_user_merchant_name, data.getUserName());
            if (data.getStatus() == 3) {
                TextView tvStatus=holder.getView(R.id.tv_status);
                tvStatus.setVisibility(View.VISIBLE);
                tvStatus.setText("审核中");
                tvStatus.setBackgroundResource(R.drawable.bg_status_warning);
            }
        }


        if(data.getPicture()!=null){
            holder.getView(R.id.iv_comment_pic).setVisibility(View.VISIBLE);
            holder.setImageUrl(R.id.iv_comment_pic,data.getPicture());
        }else{
            holder.getView(R.id.iv_comment_pic).setVisibility(View.GONE);
        }
        String fullDate = data.getCreateTime();
        String shortDate = "";
        if (fullDate != null && fullDate.length() >= 10) {
            shortDate = fullDate.substring(0, 10);
        }
        holder.setText(R.id.tv_content,data.getContent())
                .setText(R.id.tv_date,shortDate)
                .setText(R.id.tv_product_names,data.getProductName())
                .setRating(R.id.rb_score, data.getScore());
    }
}
