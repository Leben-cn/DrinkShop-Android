package com.leben.merchant.ui.activity;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.CommentEntity;
import com.leben.common.ui.adapter.CommentAdapter;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.GetShopCommentContract;
import com.leben.merchant.presenter.GetShopCommentPresenter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by youjiahui on 2026/4/13.
 */

@Route(path = MerchantConstant.Router.MANAGEMENT_COMMENT)
public class ManagementCommentActivity extends BaseRecyclerActivity<CommentEntity> implements GetShopCommentContract.View {

    private Long shopId;
    private TextView tvWaitReview;

    @InjectPresenter
    GetShopCommentPresenter getShopCommentPresenter;

    @Override
    protected BaseRecyclerAdapter<CommentEntity> createAdapter() {
        return new CommentAdapter(this,CommentAdapter.TYPE_MANAGEMENT_COMMENT);
    }

    @Override
    public void onInit() {
        super.onInit();
        shopId= getIntent().getLongExtra("shopId",-1);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_ac_management_comment;
    }

    @Override
    public void initView() {
        setDefaultSpace(10);
        super.initView();
        TitleBar titleBar=findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle("评价管理");
            tvWaitReview = new TextView(this);
            tvWaitReview.setText("待审核");
            tvWaitReview.setTextColor(Color.parseColor("#333333")); // 深灰色
            tvWaitReview.setTextSize(14); // 字体大小
            tvWaitReview.setPadding(20, 0, 20, 0); // 加点内边距，增大点击区域
            tvWaitReview.setGravity(Gravity.CENTER);
            titleBar.addRightView(tvWaitReview);
        }
    }

    @Override
    public void initListener() {
        RxView.clicks(tvWaitReview)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.WAIT_REVIEW)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
    }

    @Override
    public void initData() {
        if (shopId != 0) {
            getShopCommentPresenter.getShopComment(shopId);
        }
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onGetShopCommentSuccess(List<CommentEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetShopCommentFailed(String errorMsg) {
        refreshListFailed("加载评论失败");
        LogUtils.error("加载评论失败："+errorMsg);
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
