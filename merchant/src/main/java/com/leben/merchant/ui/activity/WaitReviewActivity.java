package com.leben.merchant.ui.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
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

/**
 * Created by youjiahui on 2026/4/13.
 */
@Route(path = MerchantConstant.Router.WAIT_REVIEW)
public class WaitReviewActivity extends BaseRecyclerActivity<CommentEntity> implements GetShopCommentContract.View, UpdateCommentStatusContract.View {

    @InjectPresenter
    GetShopCommentPresenter getShopCommentPresenter;

    @InjectPresenter
    UpdateCommentStatusPresenter updateCommentStatusPresenter;

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
        mAdapter.setOnItemLongClickListener((view, position, entity) -> {
            List<PopupEntity> items=new ArrayList<>();
            items.add(new PopupEntity(1,"撤销申请"));
            new DefaultPopup(this,items)
                    .setOnItemClickListener(item -> {
                        CommonDialog dialog=new CommonDialog();
                        dialog.setContent("确定要撤销申请吗？");
                        dialog.setOnConfirmListener(result->{
                            updateCommentStatusPresenter.updateCommentStatus(entity.getOrderId(),1);
                        });
                        dialog.setOnCancelListener(result->dialog.dismiss());
                        dialog.show(getSupportFragmentManager(),"dialog_revoke_comment");
                    }).showAsDropDown(view,view.getWidth()/2,-view.getHeight()/2);
        });
    }

    @Override
    public void initData() {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        getShopCommentPresenter.getShopComment(3);
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
    public void onGetShopCommentSuccess(List<CommentEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetShopCommentFailed(String errorMsg) {
        refreshListFailed("加载评论失败");
        LogUtils.error("加载评论失败："+errorMsg);
    }

    @Override
    public void onUpdateCommentStatusSuccess(String data) {
        ToastUtils.show(this,"申请已撤销");
        onRefresh();
    }

    @Override
    public void onUpdateCommentStatusFailed(String errorMsg) {
        refreshListFailed("撤销失败");
        LogUtils.error("撤销失败："+errorMsg);
    }
}
