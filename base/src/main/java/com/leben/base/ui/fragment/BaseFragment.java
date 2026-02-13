package com.leben.base.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.gyf.immersionbar.ImmersionBar;
import com.leben.base.Lifecycle;
import com.leben.base.LifecycleManage;
import com.leben.base.R;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.contract.IBasePresenter;
import com.leben.base.contract.IBaseView;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 自动管理 Presenter 和 Controller 生命周期的 Fragment 基类
 * 已与 BaseActivity 逻辑同步：
 * 1. 自动注入 View (@BindView)
 * 2. 自动注入 Presenter (@InjectPresenter)
 * 3. 自动分发生命周期给 Controller (LifecycleManage)
 * Created by youjiahui on 2026/1/28.
 */
public abstract class BaseFragment extends Fragment implements IBaseView, Lifecycle {

    protected Activity mActivity;
    protected View mRootView;

    // 存放所有自动注入的 Presenter
    private final List<IBasePresenter> mPresenterList = new ArrayList<>();

    // Controller 生命周期管理器
    protected LifecycleManage<Lifecycle> mLifecycleManage = new LifecycleManage<>();

    private AlertDialog mLoadingDialog;
    private TextView mTvLoadingMsg;

    //=标志位：是否是第一次初始化
    private boolean mIsFirstInit = true;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            this.mActivity = (Activity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getLayoutId(), container, false);
            mIsFirstInit = true;
        }else{
            mIsFirstInit = false;
        }
        // 缓存 View，防止 Fragment 切换时重复加载（配合 ViewPager 等使用时很重要）
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 只有第一次加载视图时才进行注入和初始化
        // 如果 mRootView 是复用的，说明已经初始化过了，避免重复注入
        // (注意：这里通过 tag 或者简单判断是否已经注入过会更严谨，但基于 MVP 简单场景，
        //  如果是复用的 View，通常不需要再次绑定事件，除非你的业务逻辑要求每次 visible 都刷新)
        //  为了简单起见，这里每次 onViewCreated 都走一遍流程，
        //  但在 onCreateView 复用 View 的情况下，onViewCreated 依然会被调用，需要根据实际业务调整。
        //  **修正建议**：配合 onCreateView 的缓存机制，这里建议加一个标志位 isInit

        if (mIsFirstInit) {
            // 1.注入 Presenter
            injectPresenters();

            // 3.Fragment 自身的初始化
            onInit();
            initView(view);
            initListener();
            initData();

            // onViewCreated 和 onResume 双重初始化是多余且有害的,沉浸式状态栏不应该在视图创建初期（onViewCreated）设置
            //initImmersionBar();

            // 4.分发生命周期给注册好的 Controller
            mLifecycleManage.onInit();
            mLifecycleManage.initView();
            mLifecycleManage.initListener();
            mLifecycleManage.initData();

            // 初始化完成，标记改为 false
            mIsFirstInit = false;
        }
    }

    /**
     * 子类调用此方法注册 Controller
     */
    protected void registerController(String key, Lifecycle controller) {
        mLifecycleManage.register(key, controller);
    }

    @Override
    public Activity getActivityContext() {
        return mActivity;
    }

    @Override
    public void onStart() {
        super.onStart();
        mLifecycleManage.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLifecycleManage.onResume();
        initImmersionBar();
    }

    @Override
    public void onPause() {
        super.onPause();
        mLifecycleManage.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLifecycleManage.onStop();
    }

    @Override
    public void onDestroy() {
        // 1.Controller 销毁
        mLifecycleManage.onDestroy();

        // 2.Presenter 解绑
        for (IBasePresenter presenter : mPresenterList) {
            presenter.detachView();
        }
        mPresenterList.clear();

        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mLifecycleManage.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showLoading(String msg) {
        if (mActivity == null || mActivity.isFinishing()) return;

        if (mLoadingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            View view = LayoutInflater.from(mActivity).inflate(R.layout.view_dialog_loading, null);
            mTvLoadingMsg = view.findViewById(R.id.tv_dialogMsg);

            builder.setView(view);
            builder.setCancelable(false);

            mLoadingDialog = builder.create();
            Objects.requireNonNull(mLoadingDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }

        if (mTvLoadingMsg != null) {
            mTvLoadingMsg.setText(msg);
        }

        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void showError(String msg) {
        ToastUtils.show(mActivity, msg);
    }

    @Override
    public void onInit() {
    }

    @Override
    public void onRetry() {
    }

    // 修改参数：initView 通常需要 View root 来 findViewById
    protected abstract void initView(View root);

    @Override
    public void initView() {
        // 为了兼容 Lifecycle 接口，这里留空，实际 Fragment 用 initView(View root)
    }

    @Override
    public abstract void initListener();

    @Override
    public abstract void initData();

    protected abstract int getLayoutId();

    private void injectPresenters() {
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(InjectPresenter.class)) {
                try {
                    Class<?> type = field.getType();
                    if (IBasePresenter.class.isAssignableFrom(type)) {
                        field.setAccessible(true);
                        // 使用原始类型绕过泛型检查
                        IBasePresenter presenter = (IBasePresenter) type.newInstance();
                        // 绑定 View
                        presenter.attachView(this);
                        // 赋值
                        field.set(this, presenter);
                        // 加入集合
                        mPresenterList.add(presenter);
                    }
                } catch (Exception e) {
                    LogUtils.error("@InjectPresenter 失败：", e);
                }
            }
        }
    }

    /**
     * 初始化沉浸式状态栏
     * 子类如果需要不同的颜色，直接重写这个方法即可
     */
    protected void initImmersionBar() {
        // 默认配置：白色背景，深色字体（因为背景是白的，字体得是黑的才看清）
        ImmersionBar.with(this)
                .statusBarColor(getStatusBarColor())
                .statusBarDarkFont(isStatusBarDarkFont())
                .titleBar(getTitleBarView())
                .init();
    }

    protected View getTitleBarView() {
        return null;
    }

    /**
     * 钩子方法：子类可以重写这个方法来改变状态栏颜色
     * 默认返回灰色
     */
    protected int getStatusBarColor() {
        return R.color.grey_backGround;
    }

    /**
     * 钩子方法：子类可以重写这个方法来改变状态栏字体颜色
     * return true: 黑色字体 (适合浅色背景)
     * return false: 白色字体 (适合深色背景)
     */
    protected boolean isStatusBarDarkFont() {
        return true; // 默认黑色字体
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // hidden = false 代表当前 Fragment 显示出来了
        if (!hidden) {
            initImmersionBar();
        }
    }

}