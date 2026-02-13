package com.leben.user.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.SharedPreferencesUtils;
import com.leben.base.util.ToastUtils;
import com.leben.user.R;
import com.leben.user.constant.UserConstant;
import com.leben.user.contract.LoginContract;
import com.leben.common.model.bean.LoginEntity;
import com.leben.user.presenter.LoginPresenter;
import com.tbruyelle.rxpermissions2.RxPermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = UserConstant.Router.USER_LOGIN)
public class LoginActivity extends BaseActivity implements LoginContract.View {

    @InjectPresenter
    LoginPresenter loginPresenter;

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private TextView mTvRegister;
    private TextView mTvMerchantLogin;

    private RxPermissions rxPermissions;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_login;
    }

    @Override
    public void initView() {
        mEtUsername = findViewById(R.id.et_username);
        mEtPassword = findViewById(R.id.et_password);
        mBtnLogin = findViewById(R.id.btn_login);
        mTvRegister = findViewById(R.id.tv_register);
        mTvMerchantLogin = findViewById(R.id.tv_merchant_login);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(mBtnLogin)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(unit->{
                    String username = mEtUsername.getText().toString().trim();
                    String password = mEtPassword.getText().toString().trim();
                    if (TextUtils.isEmpty(username)) {
                        showError("请输入账号");
                        return false;
                    }
                    if (TextUtils.isEmpty(password)) {
                        showError("请输入密码");
                        return false;
                    }
                    return true;
                })
                .subscribe(unit -> {
                    String username = mEtUsername.getText().toString().trim();
                    String password = mEtPassword.getText().toString().trim();
                    showLoading("正在登录...");

                    loginPresenter.login(username,password);

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
    }

    @SuppressLint("CheckResult")
    @Override
    public void initData() {
        // 初始化 RxPermissions
        rxPermissions = new RxPermissions(this);

        // 直接请求权限
        // 系统会自动处理：弹出一个，用户点允许/拒绝后，再自动弹下一个
        rxPermissions.request(getNeedPermissions())
                .subscribe(granted -> {
                    if (granted) {
                        // 用户全部同意了
                        LogUtils.debug("所有权限已授予");
                    } else {
                        // 用户至少拒绝了一个
                        ToastUtils.show(this, "未获取全部权限，部分功能可能受限");
                    }
                });
    }

    /**
     * 获取需要申请的权限列表（兼容 Android 13）
     */
    private String[] getNeedPermissions() {
        List<String> permissions = new ArrayList<>();

        permissions.add(Manifest.permission.CAMERA);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        // 3. 存储/相册权限 (根据版本区分)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13 (API 33) 及以上
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
            permissions.add(Manifest.permission.READ_MEDIA_VIDEO);

            if (Build.VERSION.SDK_INT >= 34) {
                permissions.add(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED);
            }
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        return permissions.toArray(new String[0]);
    }

    @Override
    public void onLoginSuccess(LoginEntity data) {
        // 1. 保存 Token (给 NetworkManager 用)
        SharedPreferencesUtils.setParam(this, UserConstant.Key.TOKEN, data.getToken());

        SharedPreferencesUtils.setParam(this, UserConstant.Key.ROLE, "USER");
        //存角色，给我的页面用
        String userInfoJson = new Gson().toJson(data.getUserInfo());
        SharedPreferencesUtils.setParam(this, UserConstant.Key.USER_INFO, userInfoJson);
        hideLoading();
        ToastUtils.show(this,"登录成功");
        ARouter.getInstance()
                .build(UserConstant.Router.CUSTOMER)
                .navigation();
        finish();
    }

    @Override
    public void onLoginFailed(String errorMsg) {
        hideLoading();
        ToastUtils.show(this,"登录失败");
    }
}
