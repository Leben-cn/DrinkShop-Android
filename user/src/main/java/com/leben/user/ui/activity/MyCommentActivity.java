package com.leben.user.ui.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.CommentEntity;
import com.leben.common.ui.adapter.CommentAdapter;
import com.leben.user.R;
import com.leben.user.constant.UserConstant;
import com.leben.user.contract.GetMyCommentContract;
import com.leben.user.presenter.GetMyCommentPresenter;

import java.util.List;

@Route(path = UserConstant.Router.MY_COMMENT)
public class MyCommentActivity extends BaseRecyclerActivity<CommentEntity> implements GetMyCommentContract.View {

    private TitleBar titleBar;

    @InjectPresenter
    GetMyCommentPresenter getMyCommentPresenter;

    @Override
    protected BaseRecyclerAdapter<CommentEntity> createAdapter() {
        return new CommentAdapter(this,CommentAdapter.TYPE_MY_COMMENT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_ac_comment_list;
    }

    @Override
    public void initView() {
        super.initView();
        titleBar=findViewById(R.id.title_bar);

        if (titleBar != null) {
            titleBar.setTitle("评价中心");
        }
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        autoRefresh();
    }

    @Override
    public void onRefresh() {
        getMyCommentPresenter.getMyComment();
    }

    @Override
    public void onGetMyCommentSuccess(List<CommentEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetMyCommentFailed(String errorMsg) {
        refreshListFailed("获取我的评价失败");
        LogUtils.error("获取我的评价失败："+errorMsg);
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }
}
