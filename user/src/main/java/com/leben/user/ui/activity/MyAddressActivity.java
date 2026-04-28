package com.leben.user.ui.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.model.bean.PopupEntity;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.base.widget.popup.DefaultPopup;
import com.leben.base.widget.popup.IconPopup;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.user.R;
import com.leben.user.constant.UserConstant;
import com.leben.user.contract.DeleteAddressContract;
import com.leben.user.contract.GetMyAddressContract;
import com.leben.common.model.bean.AddressEntity;
import com.leben.common.model.event.SelectAddressEvent;
import com.leben.user.presenter.DeleteAddressPresenter;
import com.leben.user.presenter.GetMyAddressPresenter;
import com.leben.user.ui.adapter.AddressAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = UserConstant.Router.ADDRESS)
public class MyAddressActivity extends BaseRecyclerActivity<AddressEntity> implements GetMyAddressContract.View, DeleteAddressContract.View {

    @InjectPresenter
    GetMyAddressPresenter getMyAddressPresenter;

    @InjectPresenter
    DeleteAddressPresenter deleteAddressPresenter;

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
            titleBar.setBackgroundResource(R.color.white);
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

        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<AddressEntity>() {
            @Override
            public void onItemClick(View view, int viewId, int position, AddressEntity data) {
                if(viewId==R.id.iv_edit){
                    ARouter.getInstance()
                            .build(UserConstant.Router.ADD_ADDRESS)
                            .withSerializable("address", data)
                            .navigation();
                }else{
                    //点击了整行
                    if (isSelectMode) {
                        EventBus.getDefault().post(new SelectAddressEvent(data));
                        finish();
                    }
                }
            }
        });

        mAdapter.setOnItemLongClickListener((view, position, data) -> {
            List<PopupEntity> items = new ArrayList<>();
            items.add(new PopupEntity(1, "删除地址"));

            new DefaultPopup(this, items)
                    .setOnItemClickListener(item -> {
                        if (item.id == 1){
                            CommonDialog dialog=new CommonDialog();
                            dialog.setContent("确认删除该地址吗？");
                            dialog.setOnConfirmListener(result->{
                                deleteAddressPresenter.deleteAddress(data.getId());
                                dialog.dismiss();
                            });
                            dialog.setOnCancelListener(result->dialog.dismiss());
                            dialog.show(getSupportFragmentManager(),"dialog_delete_address");
                        } ;
                    })
                    .showAsDropDown(view, view.getWidth() / 2, -view.getHeight() / 2);
        });

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

    @Override
    protected int getStatusBarColor() {
        return R.color.white;
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }

    @Override
    public void onDeleteAddressSuccess(String data) {
        ToastUtils.show(this,"地址已删除");
        onRefresh();
    }

    @Override
    public void onDeleteAddressFailed(String errorMsg) {
        ToastUtils.show(this,errorMsg);
        LogUtils.error("删除地址失败："+errorMsg);
    }
}
