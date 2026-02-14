package com.leben.merchant.ui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = MerchantConstant.Router.MERCHANT_LOGIN)
public class LoginActivity extends BaseActivity {

    private EditText mEtUsername;
    private EditText mEtPassword;
    private Button mBtnLogin;
    private Button mBtnRegister;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_merchant_login;
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
}
