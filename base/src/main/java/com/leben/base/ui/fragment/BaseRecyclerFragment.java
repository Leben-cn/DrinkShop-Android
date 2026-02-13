package com.leben.base.ui.fragment;

import android.view.View;
import android.view.ViewStub;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.leben.base.R;
import com.leben.base.decoration.SpaceItemDecoration;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.controller.LoadMoreController;
import com.leben.base.controller.StateController;
import java.util.List;

/**
 * 列表专用 Fragment 基类
 * 1. 自动初始化 RecyclerView
 * 2. 自动管理 Adapter
 * 3. 自动管理 缺省页(StateController)
 * 4. 自动管理 加载更多
 */
public abstract class BaseRecyclerFragment<T> extends BaseRefreshFragment {

    protected RecyclerView mRecyclerView;
    protected BaseRecyclerAdapter<T> mAdapter;
    protected StateController mStateController;
    protected LoadMoreController mLoadMoreController;

    // 默认间距值
    private int mDefaultSpace = 20;

    @Override
    protected void initView(View root) {
        super.initView(root); // 先初始化父类的 RefreshController

        // 1.查找 RecyclerView (通过 getIdentifier 动态查找，要求 xml id 叫 contentView)
        int rvId = getResources().getIdentifier("contentView", "id", mActivity.getPackageName());
        if (rvId != 0) {
            mRecyclerView = root.findViewById(rvId);
        }

        if (mRecyclerView != null) {
            // 2.初始化 LayoutManager
            mRecyclerView.setLayoutManager(getLayoutManager());
            // 只在需要时添加默认间距
            if (shouldAddDefaultSpaceDecoration()) {
                mRecyclerView.addItemDecoration(new SpaceItemDecoration(mDefaultSpace));
            }

            // 3.创建并绑定 Adapter
            mAdapter = createAdapter();
            mRecyclerView.setAdapter(mAdapter);

            // 4.初始化状态控制器，查找空布局和错误布局
            mStateController = new StateController(root, mRecyclerView);
            int emptyStubId = getResources().getIdentifier("emptyStub", "id", mActivity.getPackageName());
            if (emptyStubId != 0) {
                ViewStub stub = root.findViewById(emptyStubId);
                mStateController.setEmptyViewStub(stub);
            }

            int errorStubId = getResources().getIdentifier("errorStub", "id", mActivity.getPackageName());
            if (errorStubId != 0) {
                ViewStub stub = root.findViewById(errorStubId);
                mStateController.setErrorViewStub(stub);
            }

            if (isSupportLoadMore()) {
                mLoadMoreController = new LoadMoreController(mRecyclerView, mAdapter);
                mLoadMoreController.setOnLoadMoreListener(this::onLoadMore);
                // 注册控制器
                registerController("loadMore_controller", mLoadMoreController);
            }

            // 6.注册生命周期 (BaseFragment 已支持 LifecycleManage)
            registerController("state_controller", mStateController);
        }
    }

    /**
     * 默认返回 base 模块里定义好的通用布局（swipRefresh + RecyclerView）。
     * 如果子类需要自定义布局，就覆写这个方法，在布局文件里<include>引用这个布局
     */
    @Override
    protected int getLayoutId() {
        return R.layout.ly_base_recycler;
    }

    /**
     * 数据请求成功后调用此方法
     */
    public void refreshListSuccess(List<T> list) {
        refreshComplete();
        //1.填充数据
        if (mAdapter != null) {
            mAdapter.setList(list);
        }
        //2.交给 StateController 判断显示内容还是空页面
        if (mStateController != null) {
            mStateController.handleData(list);
        }
        //3.重置加载更多状态
        if (mLoadMoreController != null) {
            mLoadMoreController.reset();
        }
    }

    public void refreshListFailed(String msg) {
        refreshComplete();
        //如果当前列表是空的，就显示全屏错误页
        if(mAdapter==null||mAdapter.getItemCount()==0){
            if(mStateController!=null){
                mStateController.showError();
            }
        }else{
            //如果列表里已经有数据了，就不全屏显示，简单弹个吐司就行
            showError(msg);
        }
    }

    public void loadMoreSuccess(List<T> list, boolean hasMore) {
        if (mAdapter != null) {
            mAdapter.addList(list);
        }
        if (mLoadMoreController != null) {
            mLoadMoreController.loadMoreSuccess(hasMore);
        }
    }

    public void loadMoreFailed() {
        if (mLoadMoreController != null) {
            mLoadMoreController.loadMoreFail();
        }
    }

    /**
     * 默认线性布局，子类可覆盖
     */
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(mActivity);
    }

    /**
     * 子类提供 Adapter
     */
    protected abstract BaseRecyclerAdapter<T> createAdapter();

    /**
     * 子类覆写此方法处理加载更多
     */
    public void onLoadMore() {
    }

    /**
     * 【新增方法】是否支持加载更多
     * 默认为 true (支持)，子类不需要分页时重写返回 false
     */
    protected boolean isSupportLoadMore() {
        return true;
    }

    protected boolean shouldAddDefaultSpaceDecoration() {
        return true;
    }

    /**
     * 【新增方法】设置默认间距值
     */
    protected void setDefaultSpace(int space) {
        mDefaultSpace = space;
    }

}