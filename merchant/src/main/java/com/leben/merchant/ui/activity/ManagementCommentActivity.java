package com.leben.merchant.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.model.bean.PopupEntity;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.base.widget.popup.DefaultPopup;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.contract.UpdateCommentStatusContract;
import com.leben.common.model.bean.CommentEntity;
import com.leben.common.presenter.UpdateCommentStatusPresenter;
import com.leben.common.ui.adapter.CommentAdapter;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.GetShopCommentContract;
import com.leben.merchant.presenter.GetShopCommentPresenter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by youjiahui on 2026/4/13.
 */

@Route(path = MerchantConstant.Router.MANAGEMENT_COMMENT)
public class ManagementCommentActivity extends BaseRecyclerActivity<CommentEntity> implements GetShopCommentContract.View , UpdateCommentStatusContract.View {

    private Long shopId;
    private TextView tvWaitReview;

    @InjectPresenter
    GetShopCommentPresenter getShopCommentPresenter;

    @InjectPresenter
    UpdateCommentStatusPresenter updateCommentStatusPresenter;

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

    @SuppressLint("CheckResult")
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
        mAdapter.setOnItemLongClickListener((view, position, entity) -> {
            List<PopupEntity> items=new ArrayList<>();
            items.add(new PopupEntity(1,"删除评论"));
            new DefaultPopup(this,items)
                    .setOnItemClickListener(item -> {
                        CommonDialog dialog=new CommonDialog();
                        dialog.setContent("确定要删除此评论吗？将由管理员介入审核");
                        dialog.setOnConfirmListener(result->{
                            updateCommentStatusPresenter.updateCommentStatus(entity.getOrderId(),3);
                        });
                        dialog.setOnCancelListener(result->dialog.dismiss());
                        dialog.show(getSupportFragmentManager(),"dialog_delete_comment");
                    }).showAsDropDown(view,view.getWidth()/2,-view.getHeight()/2);
        });
    }

    @Override
    public void initData() {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        getShopCommentPresenter.getShopComment(1);
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

    @Override
    public void onUpdateCommentStatusSuccess(String data) {
        ToastUtils.show(this,"管理员已介入，评论审核中");
        onRefresh();
    }

    @Override
    public void onUpdateCommentStatusFailed(String errorMsg) {
        refreshListFailed("删除评价失败");
        LogUtils.error("删除评价失败："+errorMsg);
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }
}
