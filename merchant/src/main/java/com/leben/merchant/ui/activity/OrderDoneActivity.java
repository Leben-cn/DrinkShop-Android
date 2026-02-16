package com.leben.merchant.ui.activity;

import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.OrderEntity;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.GetDoneOrderContract;
import com.leben.merchant.presenter.GetDoneOrderPresenter;
import com.leben.merchant.ui.adapter.OrderAdapter;
import java.util.List;

@Route(path = MerchantConstant.Router.ORDER_DONE)
public class OrderDoneActivity extends BaseRecyclerActivity<OrderEntity> implements GetDoneOrderContract.View {

    @InjectPresenter
    GetDoneOrderPresenter getDoneOrderPresenter;

    @Override
    protected BaseRecyclerAdapter<OrderEntity> createAdapter() {
        return new OrderAdapter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ac_order;
    }

    @Override
    public void initView() {
        super.initView();
        TitleBar titleBar=findViewById(R.id.title_bar);
        titleBar.setTitle("已完成订单");
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        autoRefresh();
    }

    @Override
    public void onRefresh() {
        getDoneOrderPresenter.getDoneOrder();
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }

    @Override
    protected boolean shouldAddDefaultSpaceDecoration() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        autoRefresh();
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    public void onGetDoneOrderSuccess(List<OrderEntity> data) {
        refreshListSuccess(data);
        onLoadMore();
    }

    @Override
    public void onGetDoneOrderFailed(String errorMsg) {
        refreshListFailed(errorMsg);
        showError("获取订单失败");
        LogUtils.error("获取订单失败："+errorMsg);
    }
}
