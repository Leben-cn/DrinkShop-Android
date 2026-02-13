package com.leben.base.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gyf.immersionbar.ImmersionBar;
import com.leben.base.Lifecycle;
import com.leben.base.LifecycleManage;
import com.leben.base.R;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.config.AppConfig;
import com.leben.base.contract.IBasePresenter;
import com.leben.base.contract.IBaseView;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.CompositeDisposable;

/**
 * 管理 Presenter 和 Controller 生命周期的 Activity 基类
 * 1. 自动注入 View (@BindView)--已废弃
 * 2. 自动注入 Presenter (@InjectPresenter)
 * 3. 自动分发生命周期给 Controller (LifecycleManage)
 * Created by youjiahui on 2026/1/28.
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseView, Lifecycle {

    // 存放所有自动注入的 Presenter
    private final List<IBasePresenter> mPresenterList = new ArrayList<>();

    // Controller 生命周期管理器
    protected LifecycleManage<Lifecycle> mLifecycleManage = new LifecycleManage<>();
    private AlertDialog mLoadingDialog;
    private TextView mTvLoadingMsg;

    // 【新增】管理 Activity 自身的订阅（如 RxView 点击事件）
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(getLayoutId());

        injectPresenters();

        // 2.Activity 自身的初始化
        onInit();
        initView();
        initListener();
        initData();

        //1.初始化沉浸式
        initImmersionBar();

        // 3.分发生命周期给注册好的 Controller
        mLifecycleManage.onInit();
        mLifecycleManage.initView();
        mLifecycleManage.initListener();
        mLifecycleManage.initData();
    }

    /**
     * 子类调用此方法注册 Controller
     */
    protected void registerController(String key, Lifecycle controller) {
        mLifecycleManage.register(key, controller);
    }

    @Override
    public Activity getActivityContext() {
        return this;
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

        // 3.Activity 销毁
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 分发给 Controller
        mLifecycleManage.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onInit() {
    }

    @Override
    public void onRetry() {
    }

    public void showLoading(String msg) {
        if (mLoadingDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.view_dialog_loading, null);
            mTvLoadingMsg = view.findViewById(R.id.tv_dialogMsg);

            builder.setView(view);
            builder.setCancelable(false);

            mLoadingDialog = builder.create();
            //让背景变成透明，否则可能有个白色方框
            Objects.requireNonNull(mLoadingDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        }

        if (mTvLoadingMsg != null) {
            mTvLoadingMsg.setText(msg);
        }

        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    public void hideLoading() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    public void showError(String msg) {
        ToastUtils.show(this,msg);
    }

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

}