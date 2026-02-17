package com.leben.merchant.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.fragment.BaseRefreshFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.util.SharedPreferencesUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.common.Constant.CommonConstant;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.model.bean.LoginEntity;
import com.leben.merchant.util.MerchantUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = CommonConstant.Router.WORK_BENCH)
public class WorkbenchFragment extends BaseRefreshFragment {

    private TextView mTvShopName;
    private ImageView mIvShopAvatar;
    private TextView mTvLogout;
    private LinearLayout llAllOrder;
    private LinearLayout llCancelOrder;
    private LinearLayout llDoneOrder;
    private LinearLayout llPendingOrder;

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_frag_workbench;
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

                    dialog.show(getParentFragmentManager(), "logout_tag");
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
    }

    @Override
    public void initData() {

    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.color_theme_yellow;
    }

    @Override
    public void onRefresh() {

    }

    private void loadMerchantInfo(){
        LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(getContext());
        if (merchantInfo != null) {
            mTvShopName.setText(merchantInfo.getShopName());
            Glide.with(requireContext())
                    .load(merchantInfo.getImg())
                    .circleCrop()
                    .into(mIvShopAvatar);
        }
    }
}
