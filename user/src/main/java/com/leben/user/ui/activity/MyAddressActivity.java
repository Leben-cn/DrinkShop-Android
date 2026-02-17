package com.leben.user.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.user.R;
import com.leben.user.constant.UserConstant;
import com.leben.user.contract.GetMyAddressContract;
import com.leben.common.model.bean.AddressEntity;
import com.leben.common.model.event.SelectAddressEvent;
import com.leben.user.presenter.GetMyAddressPresenter;
import com.leben.user.ui.adapter.AddressAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = UserConstant.Router.ADDRESS)
public class MyAddressActivity extends BaseRecyclerActivity<AddressEntity> implements GetMyAddressContract.View {

    @InjectPresenter
    GetMyAddressPresenter getMyAddressPresenter;

    private TextView tvAddAddress;
    private boolean isSelectMode = false;

    @Override
    protected BaseRecyclerAdapter<AddressEntity> createAdapter() {
        return new AddressAdapter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_ac_address_list;
    }

    @Override
    public void initView() {
        super.initView();
        TitleBar titleBar =findViewById(R.id.title_bar);

        if (titleBar != null) {
            titleBar.setTitle("我的收货地址");
            tvAddAddress = new TextView(this);
            tvAddAddress.setText("新增地址");
            tvAddAddress.setTextColor(Color.parseColor("#333333")); // 深灰色
            tvAddAddress.setTextSize(14); // 字体大小
            tvAddAddress.setPadding(20, 0, 20, 0); // 加点内边距，增大点击区域
            tvAddAddress.setGravity(Gravity.CENTER);
            titleBar.addRightView(tvAddAddress);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(tvAddAddress)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                    ARouter.getInstance()
                            .build(UserConstant.Router.ADD_ADDRESS)
                            .navigation();
                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        if (mAdapter instanceof AddressAdapter) {
            ((AddressAdapter) mAdapter).getItemClickObservable()
                    .throttleFirst(500, TimeUnit.MILLISECONDS) // 防止连点
                    .subscribe(data -> {
                        if (isSelectMode) {
                            // 选择模式
                            EventBus.getDefault().post(new SelectAddressEvent(data));
                            finish();
                        }
                    }, throwable -> {
                        LogUtils.error("Adapter Click Error: " + throwable.getMessage());
                    });
        }

    }

    @Override
    public void initData() {
        isSelectMode = getIntent().getBooleanExtra("isSelectMode", false);
        autoRefresh();
    }

    @Override
    public void onRefresh() {
        getMyAddressPresenter.getMyAddress();
    }

    @Override
    public void onGetMyAddressSuccess(List<AddressEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetMyAddressFailed(String errorMsg) {
        refreshListFailed("获取地址失败");
        showError("获取地址失败："+errorMsg);
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

}
