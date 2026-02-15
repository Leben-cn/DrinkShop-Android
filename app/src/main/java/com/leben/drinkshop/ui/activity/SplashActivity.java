package com.leben.drinkshop.ui.activity;

import android.text.TextUtils;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.SharedPreferencesUtils;
import com.leben.common.Constant.CommonConstant;
import com.leben.drinkshop.App;
import com.leben.drinkshop.R;
import com.leben.drinkshop.constant.AppConstant;
import com.leben.user.constant.UserConstant;

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
        // 2. 判断 Token 是否为空
        if (TextUtils.isEmpty(token)) {
            // ---> 没 Token，说明没登录，去登录页
            LogUtils.info("无Token");
            ARouter.getInstance()
                    .build(CommonConstant.Router.USER_LOGIN)
                    .navigation();
        } else {
            LogUtils.info("有Token："+token);
            // 3. 有 Token，说明登录过。接下来判断身份去不同的首页
            // 注意：这里需要你在登录成功时，额外存一个字段标识身份
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
