package com.leben.base.ui.activity;

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
 * 列表专用基类
 * 1. 自动初始化 RecyclerView
 * 2. 自动管理 Adapter
 * 3. 自动管理 缺省页(StateController)
 * Created by youjiahui on 2026/1/28.
 */

public abstract class BaseRecyclerActivity<T> extends BaseRefreshActivity {

    protected RecyclerView mRecyclerView;
    protected BaseRecyclerAdapter<T> mAdapter;
    protected StateController mStateController;
    protected LoadMoreController mLoadMoreController;

    // 默认间距值
    private int mDefaultSpace = 16;

    @Override
    public void initView() {
        super.initView();//先初始化父类的 SwipeRefreshLayout

        //1.查找 RecyclerView
        int rvId=getResources().getIdentifier("contentView","id",getPackageName());
        if(rvId!=0){
            mRecyclerView=findViewById(rvId);
        }
        if(mRecyclerView!=null){
            //2.初始化 LayoutManager
            mRecyclerView.setLayoutManager(getLayoutManager());
            // 只在需要时添加默认间距
            if (shouldAddDefaultSpaceDecoration()) {
                mRecyclerView.addItemDecoration(new SpaceItemDecoration(mDefaultSpace));
            }

            //3.创建并绑定 Adapter
            mAdapter=createAdapter();
            mRecyclerView.setAdapter(mAdapter);

            //4.初始化状态控制器，查找空布局和错误布局
            mStateController=new StateController(this,mRecyclerView);
            int emptyStubId=getResources().getIdentifier("emptyStub","id",getPackageName());
            if(emptyStubId!=0){
                ViewStub stub=findViewById(emptyStubId);
                mStateController.setEmptyViewStub(stub);
            }

            int errorStubId=getResources().getIdentifier("errorStub","id",getPackageName());
            if(errorStubId!=0){
                ViewStub stub=findViewById(errorStubId);
                mStateController.setErrorViewStub(stub);
            }

            if (isSupportLoadMore()) {
                mLoadMoreController = new LoadMoreController(mRecyclerView, mAdapter);
                mLoadMoreController.setOnLoadMoreListener(this::onLoadMore);
                // 注册控制器
                registerController("loadMore_controller", mLoadMoreController);
            }

            //注册生命周期
            registerController("state_controller",mStateController);
        }
    }

    /**
     * 数据请求成功后调用此方法
     * 停止刷新 + 填充数据 + 切换空布局状态
     */
    public void refreshListSuccess(List<T> list){
        refreshComplete();
        if (mAdapter != null) {
            mAdapter.setList(list);
        }
        if(mStateController!=null){
            mStateController.handleData(list);
        }
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

    /**
     * 默认线性布局，子类可覆盖
     */
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    /**
     * 子类提供 Adapter
     */
    protected abstract BaseRecyclerAdapter<T> createAdapter();

    public void onLoadMore() {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ly_base_recycler;
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

    protected boolean shouldAddDefaultSpaceDecoration() {
        return true;
    }

    /**
     * 设置默认间距值
     */
    protected void setDefaultSpace(int space) {
        mDefaultSpace = space;
    }

    protected boolean isSupportLoadMore() {
        return true;
    }
}
