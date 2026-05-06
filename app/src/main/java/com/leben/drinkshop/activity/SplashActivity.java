package com.leben.drinkshop.activity;

import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.SharedPreferencesUtils;
import com.leben.common.constant.CommonConstant;
import com.leben.drinkshop.R;
import com.leben.drinkshop.constant.AppConstant;

/**
 * 启动页
 * Created by youjiahui on 2026/2/1.
 */
@Route(path = AppConstant.Router.SPLASH)
public class SplashActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.ac_splash;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        checkLogin();
    }

    private void checkLogin() {
        String token = SharedPreferencesUtils.getParam(this,AppConstant.Key.TOKEN, "");
        LogUtils.info(token);
        if (TextUtils.isEmpty(token)) {
            LogUtils.info("无Token");
            ARouter.getInstance()
                    .build(CommonConstant.Router.USER_LOGIN)
                    .navigation();
        } else {
            LogUtils.info("有Token："+token);
            // 这里需要在登录成功时，额外存一个字段标识身份
            String role = SharedPreferencesUtils.getParam(this,AppConstant.Key.ROLE, "");

            if ("MERCHANT".equals(role)) {
                ARouter.getInstance()
                        .build(CommonConstant.Router.MERCHANT_TAB)
                        .navigation();
            } else if("USER".equals(role)){
                ARouter.getInstance()
                        .build(CommonConstant.Router.CUSTOMER_TAB)
                        .navigation();
            }else{
                showError("Token 解析出错");
            }
        }

        finish();
    }


}
