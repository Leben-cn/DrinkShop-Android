package com.leben.merchant.ui.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.CommentEntity;
import com.leben.common.ui.adapter.CommentAdapter;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;

/**
 * Created by youjiahui on 2026/4/13.
 */
@Route(path = MerchantConstant.Router.WAIT_REVIEW)
public class WaitReviewActivity extends BaseRecyclerActivity<CommentEntity> {

    @Override
    protected BaseRecyclerAdapter<CommentEntity> createAdapter() {
        return new CommentAdapter(this,CommentAdapter.TYPE_WAIT_REVIEW);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_ac_wait_review;
    }

    @Override
    public void initView() {
        setDefaultSpace(10);
        super.initView();
        TitleBar titleBar=findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle("评论审核进度");
        }
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }
}
