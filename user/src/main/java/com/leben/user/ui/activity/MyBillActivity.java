package com.leben.user.ui.activity;

import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.user.R;
import com.leben.user.constant.UserConstant;
import com.leben.user.contract.GetMyBillContract;
import com.leben.user.model.bean.BillEntity;
import com.leben.user.presenter.GetMyBillPresenter;
import com.leben.user.ui.adapter.BillAdapter;
import java.util.List;

@Route(path = UserConstant.Router.BILL)
public class MyBillActivity extends BaseRecyclerActivity<BillEntity> implements GetMyBillContract.View {

    private TitleBar titleBar;

    @InjectPresenter
    GetMyBillPresenter getMyBillPresenter;

    @Override
    protected BaseRecyclerAdapter<BillEntity> createAdapter() {
        return new BillAdapter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_ac_bill_list;
    }

    @Override
    public void initView() {
        super.initView();
        titleBar=findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle("账单中心");
        }
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
        getMyBillPresenter.getMyBill();
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    public void onGetMyBillSuccess(List<BillEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetMyBillFailed(String errorMsg) {
        refreshListFailed("获取账单数据失败");
        LogUtils.error("获取账单数据失败："+errorMsg);
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }

}
