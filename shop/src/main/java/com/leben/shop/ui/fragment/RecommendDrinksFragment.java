package com.leben.shop.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.constant.CommonConstant;
import com.leben.common.LocationManager;
import com.leben.common.model.event.LocationEvent;
import com.leben.shop.R;
import com.leben.shop.constant.ShopConstant;
import com.leben.shop.contract.RecommendShopsContract;
import com.leben.shop.model.bean.PageEntity;
import com.leben.common.model.bean.ShopEntity;
import com.leben.shop.presenter.RecommendShopsPresenter;
import com.leben.shop.ui.adapter.RecommendShopsAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.CommonDataSource;

import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = CommonConstant.Router.RECOMMEND_DRINKS)
public class RecommendDrinksFragment extends BaseRecyclerFragment<ShopEntity> implements RecommendShopsContract.View {

    @InjectPresenter
    RecommendShopsPresenter mRecommendShopsPresenter;

    private TitleBar titleBar;
    private int page = 0;
    private final int SIZE = 10;//分页大小
    private long currentSeed = 0; // 记录当前的随机种子,用于每次刷新后展示不同的数据

    private Double latitude = 30.29;
    private Double longitude = 120.15;

    private LinearLayout llMilkTea;
    private LinearLayout llFruitTea;
    private LinearLayout llJuice;
    private LinearLayout llCoffee;
    private TextView tvLocationName;

    @Override
    protected BaseRecyclerAdapter<ShopEntity> createAdapter() {
        return new RecommendShopsAdapter(mActivity);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.shop_frag_recommend_drinks;
    }

    @Override
    public void onInit() {
        super.onInit();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void initView(View root) {
        super.initView(root);
        titleBar = root.findViewById(R.id.title_bar);
        llMilkTea=root.findViewById(R.id.ll_cat_milk_tea);
        llFruitTea=root.findViewById(R.id.ll_cat_fruit_tea);
        llJuice=root.findViewById(R.id.ll_cat_juice);
        llCoffee=root.findViewById(R.id.ll_cat_coffee);
        tvLocationName=root.findViewById(R.id.tv_location_name);

        if (titleBar != null) {
            List<String> hints = new ArrayList<>();
            hints.add("搜索好喝的奶茶");
            hints.add("低卡果茶推荐");
            titleBar.setSearchHints(hints);
            titleBar.setBackgroundResource(R.color.color_theme_yellow);
            // 开启轮播
            titleBar.startRoll();
            titleBar.setBackVisible(false);
        }
    }

    @Override
    public void onRefresh() {
        page = 0;
        currentSeed = System.currentTimeMillis();
        mRecommendShopsPresenter.getRecommendShops(page, SIZE, currentSeed, latitude, longitude);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        if (mAdapter instanceof RecommendShopsAdapter) {
            ((RecommendShopsAdapter) mAdapter).observeClicks()
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(event -> {
                        Long shopId = event.shop.getId();
                        long drinkId = (event.drink != null) ? event.drink.getId() : -1;
                        ARouter.getInstance()
                                .build(ShopConstant.Router.SHOP)
                                .withLong("shopId", shopId)
                                .withLong("drinkId", drinkId)
                                .navigation();
                    }, throwable -> {
                        LogUtils.error("点击事件流出错: " + throwable.getMessage());
                    });
        }

        RxView.clicks(titleBar)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit->{
                    ARouter.getInstance()
                            .build(ShopConstant.Router.SEARCH_DRINK)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(tvLocationName)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit->{
                    ARouter.getInstance()
                            .build(CommonConstant.Router.ADD_ADDRESS)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(llMilkTea)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit->{
                    ARouter.getInstance()
                            .build(ShopConstant.Router.PLATFORM_CLASSIFY)
                            .withLong("CLASSIFY",1)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(llFruitTea)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit->{
                    ARouter.getInstance()
                            .build(ShopConstant.Router.PLATFORM_CLASSIFY)
                            .withLong("CLASSIFY",2)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(llJuice)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit->{
                    ARouter.getInstance()
                            .build(ShopConstant.Router.PLATFORM_CLASSIFY)
                            .withLong("CLASSIFY",3)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(llCoffee)
                .throttleFirst(500,TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit->{
                    ARouter.getInstance()
                            .build(ShopConstant.Router.PLATFORM_CLASSIFY)
                            .withLong("CLASSIFY",4)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
    }

    @Override
    public void initData() {
        // 1. 检查是否有缓存位置，如果有，直接用缓存位置请求一次
        Double cachedLat = LocationManager.getInstance().getLatitude();
        Double cachedLon = LocationManager.getInstance().getLongitude();

        if (cachedLat != null && cachedLat != 0) {
            this.latitude = cachedLat;
            this.longitude = cachedLon;
            // 有缓存位置，可以直接开始刷新
            autoRefresh();
        } else {
            LocationManager.getInstance().startSingleLocation(requireContext());
        }
    }

    @Override
    public void onLoadMore() {
        page++;
        Long seed = currentSeed;
        mRecommendShopsPresenter.getRecommendShops(page, SIZE, seed, latitude, longitude);
    }

    @Override
    public void onRecommendShopsSuccess(PageEntity<ShopEntity> pageData) {
        List<ShopEntity> list = pageData.getContent();
        if(pageData.getContent().isEmpty()){
            ToastUtils.show(requireContext(),"附近10公里没有店铺！");
        }
        // 区分 2：滑到底了
        boolean isLast = pageData.isLast(); // 后端 Page 对象自带 last 属性
        if (page == 0) {
            refreshListSuccess(list);
        } else {
            loadMoreSuccess(list, !isLast); // 如果是最后一页，!isLast 为 false，前端停止上拉
        }

        if (isLast && page > 0) {
            // 可以在这里提示“没有更多数据了”
        }
    }

    @Override
    public void onRecommendShopsFailed(String errorMsg) {
        if (page == 0) {
            refreshListFailed(errorMsg);
        } else {
            page--;
            loadMoreFailed();
            showError(errorMsg);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 如果 TitleBar 有轮播，记得在这里 startRoll
        if (getView() != null) {
            TitleBar titleBar = getView().findViewById(R.id.title_bar);
            if (titleBar != null) titleBar.startRoll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // 记得停止轮播
        if (getView() != null) {
            TitleBar titleBar = getView().findViewById(R.id.title_bar);
            if (titleBar != null) titleBar.stopRoll();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mRecommendShopsPresenter != null) {
            mRecommendShopsPresenter.detachView();
        }
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    protected View getTitleBarView() {
        return mRootView.findViewById(R.id.cl_location_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.color_theme_yellow;
    }

    //处理地址更新
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationChanged(LocationEvent event) {
        // 只有当位置真的发生变化时才请求
        this.latitude = event.latitude;
        this.longitude = event.longitude;
        this.tvLocationName.setText(event.address);

        // 定位成功后，如果是第一次加载数据（page=0），触发刷新
        onRefresh();
    }

}
