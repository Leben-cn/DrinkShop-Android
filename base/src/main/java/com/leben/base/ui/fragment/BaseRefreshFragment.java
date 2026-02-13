package com.leben.base.ui.fragment;

import android.view.View;
import com.leben.base.listener.IRefreshListener;
import com.leben.base.controller.SwipeRefreshController;

/**
 * 带有下拉刷新功能的 Fragment 基类
 * 镜像于 BaseRefreshActivity
 */
public abstract class BaseRefreshFragment extends BaseFragment implements IRefreshListener {

    protected SwipeRefreshController mRefreshController;

    @Override
    protected void initView(View root) {
        // 这里要在 root (Fragment的根布局) 中查找
        int refreshId = getResources().getIdentifier("swipeRefresh", "id", mActivity.getPackageName());
        if (refreshId != 0) {
            mRefreshController = new SwipeRefreshController(root, refreshId);
            mRefreshController.setOnRefreshListener(this);
        }
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        if (mRefreshController != null) {
            mRefreshController.autoRefresh();
        }
    }

    /**
     * 结束刷新
     */
    public void refreshComplete() {
        if (mRefreshController != null) {
            mRefreshController.finishRefresh();
        }
    }
}