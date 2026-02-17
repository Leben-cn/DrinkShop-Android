package com.leben.merchant.ui.activity;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.google.gson.Gson;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.SharedPreferencesUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.Constant.CommonConstant;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.LoginContract;
import com.leben.merchant.model.bean.LoginEntity;
import com.leben.merchant.presenter.LoginPresenter;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = MerchantConstant.Router.MERCHANT_LOGIN)
public class LoginActivity extends BaseActivity implements LoginContract.View {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnRegister;

    @InjectPresenter
    LoginPresenter loginPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_ac_login;
    }

    @Override
    public void initView() {
        TitleBar titleBar=findViewById(R.id.title_bar);
        mEtUsername=findViewById(R.id.et_username);
        mEtPassword=findViewById(R.id.et_password);
        mBtnLogin=findViewById(R.id.btn_login);
        mBtnRegister=findViewById(R.id.btn_register);

        titleBar.setTitle(" ");
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(mBtnRegister)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.REGISTER)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
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

    @Override
    public void initData() {

    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }

    @Override
    public void onLoginSuccess(LoginEntity data) {

        SharedPreferencesUtils.setParam(this, CommonConstant.Key.TOKEN, data.getToken());
        SharedPreferencesUtils.setParam(this, CommonConstant.Key.ROLE, "MERCHANT");
        //存角色，给商家我的页面用
        String merchantInfoJson = new Gson().toJson(data.getShopInfo());
        SharedPreferencesUtils.setParam(this, CommonConstant.Key.MERCHANT_INFO, merchantInfoJson);
        hideLoading();
        ToastUtils.show(this,"登录成功");
        ARouter.getInstance()
                .build(CommonConstant.Router.MERCHANT_TAB)
                .navigation();
        finish();
    }

    @Override
    public void onLoginFailed(String errorMsg) {
        hideLoading();
        ToastUtils.show(this,errorMsg);
    }
}
