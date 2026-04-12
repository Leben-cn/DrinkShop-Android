package com.leben.merchant.ui.activity;

import android.view.View;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRefreshActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.GetShopRevenueContract;
import com.leben.merchant.presenter.GetShopRevenuePresenter;
import java.math.BigDecimal;

/**
 * Created by youjiahui on 2026/4/12.
 */

@Route(path = MerchantConstant.Router.SHOP_WALLET)
public class WalletActivity extends BaseRefreshActivity implements GetShopRevenueContract.View {

    private TextView tvCharge;

    @InjectPresenter
    GetShopRevenuePresenter getShopRevenuePresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_ac_wallet;
    }

    @Override
    public void initView() {
        super.initView();
        TitleBar titleBar = findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle("");
        }
        tvCharge=findViewById(R.id.tv_charge);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        onRefresh();
    }

    @Override
    public void onRefresh() {
        getShopRevenuePresenter.getShopRevenue();
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.white;
    }

    @Override
    public void onGetShopRevenueSuccess(BigDecimal data) {
        tvCharge.setText(String.valueOf(data));
        refreshComplete();
    }

    @Override
    public void onGetShopRevenueFailed(String errorMsg) {
        ToastUtils.show(this,"余额加载失败");
        LogUtils.error("余额加载失败："+errorMsg);
    }
}
