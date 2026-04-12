package com.leben.merchant.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxCompoundButton;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.fragment.BaseRefreshFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.util.SharedPreferencesUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.common.Constant.CommonConstant;
import com.leben.common.ui.activity.MerchantActivity;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.GetShopTodayStatsContract;
import com.leben.merchant.contract.UpdateShopStatusContract;
import com.leben.merchant.model.bean.LoginEntity;
import com.leben.merchant.model.event.RefreshInfoEvent;
import com.leben.merchant.presenter.GetShopTodayStatsPresenter;
import com.leben.merchant.presenter.UpdateShopStatusPresenter;
import com.leben.merchant.util.MerchantUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by youjiahui on 2026/4/6.
 */

@Route(path = CommonConstant.Router.WORK_BENCH)
public class WorkbenchFragment extends BaseRefreshFragment implements UpdateShopStatusContract.View, GetShopTodayStatsContract.View {

    private TextView mTvShopName;
    private ImageView mIvShopAvatar;
    private TextView mTvLogout;
    private TextView mTvManagementDrink;
    private LinearLayout llAllOrder;
    private LinearLayout llCancelOrder;
    private LinearLayout llDoneOrder;
    private LinearLayout llPendingOrder;
    private TextView mTvShopSetting;
    private SwitchMaterial mSwitchStatus;
    private View mVStatusDot;
    private TextView mTvShopStatus;
    private TextView mTvShopTodayTurnover;
    private TextView mTvShopTodayOrder;
    private TextView mTvShopStatistics;

    @InjectPresenter
    UpdateShopStatusPresenter updateShopStatusPresenter;

    @InjectPresenter
    GetShopTodayStatsPresenter getShopTodayStatsPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_frag_workbench;
    }

    @Override
    public void onInit() {
        super.onInit();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void initView(View root) {
        mTvShopName=root.findViewById(R.id.tv_shop_name);
        mIvShopAvatar=root.findViewById(R.id.iv_shop_avatar);
        mTvLogout=root.findViewById(R.id.item_logout);
        llAllOrder=root.findViewById(R.id.iv_all_order);
        llCancelOrder=root.findViewById(R.id.ll_refund);
        llDoneOrder=root.findViewById(R.id.ll_done);
        llPendingOrder=root.findViewById(R.id.iv_pending);
        mTvShopSetting=root.findViewById(R.id.tv_shop_setting);
        mSwitchStatus = root.findViewById(R.id.switch_status);
        mVStatusDot = root.findViewById(R.id.v_status_dot);
        mTvShopStatus = root.findViewById(R.id.tv_shop_status);
        mTvManagementDrink=root.findViewById(R.id.tv_goods_mgr);
        mTvShopTodayTurnover=root.findViewById(R.id.tv_today_turnover);
        mTvShopTodayOrder=root.findViewById(R.id.tv_today_orders);
        mTvShopStatistics=root.findViewById(R.id.tv_statistics);

        loadMerchantInfo();
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(mTvLogout)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .flatMap(unit -> Observable.<Boolean>create(emitter -> {
                    CommonDialog dialog = CommonDialog.newInstance()
                            .setTitle("退出登录")
                            .setContent("确定要退出当前账号吗？");

                    dialog.setOnCancelListener(d -> {
                        emitter.onNext(false); // 发射 false 表示取消
                        emitter.onComplete();
                        d.dismiss();
                    });

                    dialog.setOnConfirmListener(d -> {
                        emitter.onNext(true); // 发射 true 表示确定
                        emitter.onComplete();
                        d.dismiss();
                    });

                    dialog.show(getParentFragmentManager(), "dialog_logout");
                }))
                .filter(result -> result) // 只处理 true（确定）的情况
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    // 1. 清除用户数据
                    SharedPreferencesUtils.removeParam(getContext(), CommonConstant.Key.TOKEN);
                    SharedPreferencesUtils.removeParam(getContext(), CommonConstant.Key.MERCHANT_INFO);
                    SharedPreferencesUtils.removeParam(getContext(), CommonConstant.Key.ROLE);

                    // 2. 跳转到登录页
                    ARouter.getInstance()
                            .build(CommonConstant.Router.USER_LOGIN)
                            .navigation();

                    // 3. 结束当前页面
                    if (getActivity() != null) {
                        getActivity().finish();
                    }

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(llAllOrder)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.ORDER_ALL)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
        RxView.clicks(llCancelOrder)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.ORDER_CANCEL)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
        RxView.clicks(llDoneOrder)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.ORDER_DONE)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
        RxView.clicks(llPendingOrder)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.ORDER_PENDING)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(mTvShopSetting)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.SETTING)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(mTvShopStatistics)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.SHOP_STATISTICS)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(mTvManagementDrink)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        setSwitchListener();
    }

    @Override
    public void initData() {
        getShopTodayStatsPresenter.getShopTodayStats();
    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.color_theme_yellow;
    }

    @Override
    public void onRefresh() {

    }

    private void loadMerchantInfo() {
        LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(getContext());
        if (merchantInfo != null) {
            mTvShopName.setText(merchantInfo.getShopName());
            boolean isOpen = (merchantInfo.getStatus() != null && merchantInfo.getStatus() == 1);

            // 初始化前先解除监听
            mSwitchStatus.setOnCheckedChangeListener(null);
            mSwitchStatus.setChecked(isOpen);
            updateStatusUI(isOpen);
            // 初始化后再绑定监听
            setSwitchListener();

            Glide.with(requireContext())
                    .load(merchantInfo.getImg())
                    .centerCrop()
                    .into(mIvShopAvatar);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshInfoEvent(RefreshInfoEvent event){
        loadMerchantInfo();
    }

    private void updateStatusUI(boolean isOpen) {
        mTvShopStatus.setText(isOpen ? "营业中" : "打烊中");
        mVStatusDot.setBackgroundResource(isOpen ?
                R.drawable.shape_circle_green : R.drawable.shape_circle_grey);

        if (mSwitchStatus.isChecked() != isOpen) {
            mSwitchStatus.setChecked(isOpen);
        }
    }

    @Override
    public void onUpdateShopStatusSuccess(String data) {
        ToastUtils.show(getContext(), "切换成功");

        // 1. 获取当前最新的本地缓存
        LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(getContext());
        if (merchantInfo != null) {
            // 2. 修改内存中的状态 (假设之前请求的是 status)
            // 这里的 status 可以根据业务逻辑获取，或者从 Presenter 传回来
            int currentStatus = mSwitchStatus.isChecked() ? 1 : 0;
            merchantInfo.setStatus(currentStatus);

            // 3. 将修改后的对象重新保存到 SharedPreferences
            SharedPreferencesUtils.setParam(getContext(),
                    CommonConstant.Key.MERCHANT_INFO, merchantInfo);
        }

        // 4. 刷新一次 UI 状态（确保万无一失）
        updateStatusUI(mSwitchStatus.isChecked());
    }

    @Override
    public void onUpdateShopStatusFailed(String errorMsg) {
        showError(errorMsg);
        LogUtils.error("切换店铺状态失败："+errorMsg);
        // 1. 先解绑监听器，防止 setChecked 触发二次请求
        mSwitchStatus.setOnCheckedChangeListener(null);

        // 2. 执行回弹逻辑 (拨回反方向)
        boolean backStatus = !mSwitchStatus.isChecked();
        mSwitchStatus.setChecked(backStatus);

        // 3. 同步 UI 文字和圆点
        updateStatusUI(backStatus);

        // 4. 重新绑定监听器
        setSwitchListener();
    }

    private void setSwitchListener() {
        mSwitchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // 物理点击触发
            int status = isChecked ? 1 : 0;
            updateShopStatusPresenter.updateShopStatus(status);
            // 先即时更新 UI
            updateStatusUI(isChecked);
        });
    }

    @Override
    public void onGetShopTodayStatsSuccess(Map<String, Object> data) {
        if (data != null) {
            // 1. 获取营业额 (处理 todayRevenue)
            Object revenueObj = data.get("todayRevenue");
            // 如果是 0，我们希望显示 "0.00"，如果是数字则保留两位小数
            if (revenueObj != null) {
                try {
                    double revenue = Double.parseDouble(String.valueOf(revenueObj));
                    mTvShopTodayTurnover.setText(String.format("%.2f", revenue));
                } catch (Exception e) {
                    mTvShopTodayTurnover.setText("0.00");
                }
            } else {
                mTvShopTodayTurnover.setText("0.00");
            }

            // 2. 获取订单量 (处理 todayOrderCount)
            Object orderObj = data.get("todayOrderCount");
            if (orderObj != null) {
                try {
                    // 将对象转为字符串再转为整数，防止 Double 类型带小数点
                    double orderCountDouble = Double.parseDouble(String.valueOf(orderObj));
                    int orderCount = (int) orderCountDouble;
                    mTvShopTodayOrder.setText(String.valueOf(orderCount));
                } catch (Exception e) {
                    mTvShopTodayOrder.setText("0");
                }
            } else {
                mTvShopTodayOrder.setText("0");
            }
        }
    }

    @Override
    public void onGetShopTodayStatsFailed(String errorMsg) {
        showError(errorMsg);
        LogUtils.error("获取店铺今日数据失败："+errorMsg);
    }
}
