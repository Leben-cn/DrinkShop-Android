package com.leben.merchant.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.constant.CommonConstant;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.GetShopDrinkContract;
import com.leben.merchant.model.event.RefreshDrinkListEvent;
import com.leben.merchant.presenter.ShopDrinkPresenter;
import com.leben.merchant.ui.adapter.DrinkAdapter;
import com.leben.common.util.MerchantUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by youjiahui on 2026/2/17.
 */
@Route(path = CommonConstant.Router.GOODS)
public class GoodsFragment extends BaseRecyclerFragment<DrinkEntity> implements GetShopDrinkContract.View {

    private ImageView mIvAddDrink;
    private TitleBar titleBar;
    private Long shopId;

    @InjectPresenter
    ShopDrinkPresenter shopDrinkPresenter;

    @Override
    protected BaseRecyclerAdapter<DrinkEntity> createAdapter() {
        return new DrinkAdapter(getContext());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_frag_drink;
    }

    @Override
    public void onInit() {
        super.onInit();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        shopId=MerchantUtils.getMerchantId(getContext());
    }

    @Override
    protected void initView(View root) {

        super.initView(root);
        titleBar=root.findViewById(R.id.title_bar);
        mIvAddDrink=new ImageView(getContext());
        mIvAddDrink.setImageResource(R.drawable.ic_add_circle);
        titleBar.addRightView(mIvAddDrink,30,30);
        titleBar.setBackVisible(false);
        if (titleBar != null) {
            List<String> hints=new ArrayList<>();
            hints.add("搜索商品名称");
            titleBar.setSearchHints(hints);
        }
    }

    @Override
    public void onRefresh() {
        shopDrinkPresenter.getShopDrinks(shopId);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(mIvAddDrink)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.DRINK_EDIT)
                            .withString("TAG","GOOS_FRAGMENT")
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(titleBar)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.DRINK_SEARCH)
                            .withLong("shopId",shopId)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
    }

    @Override
    public void initData() {
        autoRefresh();
    }

    @Override
    protected View getTitleBarView() {
        return mRootView.findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }

    @Override
    public void onGetShopDrinkSuccess(List<DrinkEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetShopDrinkFailed(String errorMsg) {
        refreshListFailed("获取店铺商品失败");
        LogUtils.error("获取店铺商品失败："+errorMsg);
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshDrinkListEvent(RefreshDrinkListEvent event){
        autoRefresh();
    }
}
