package com.leben.shop.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.LocationManager;
import com.leben.shop.R;
import com.leben.shop.constant.ShopConstant;
import com.leben.shop.contract.RecommendShopsContract;
import com.leben.shop.model.bean.PageEntity;
import com.leben.common.model.bean.ShopEntity;
import com.leben.shop.presenter.RecommendShopsPresenter;
import com.leben.shop.ui.adapter.RecommendShopsAdapter;
import com.leben.shop.ui.dialog.RemarkDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = ShopConstant.Router.RECOMMEND_DRINKS)
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

    @Override
    protected BaseRecyclerAdapter<ShopEntity> createAdapter() {
        return new RecommendShopsAdapter(mActivity);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.frag_recommend_drinks;
    }

    @Override
    public void initView(View root) {
        super.initView(root);
        titleBar = root.findViewById(R.id.title_bar);
        llMilkTea=root.findViewById(R.id.ll_cat_milk_tea);
        llFruitTea=root.findViewById(R.id.ll_cat_fruit_tea);
        llJuice=root.findViewById(R.id.ll_cat_juice);
        llCoffee=root.findViewById(R.id.ll_cat_coffee);

        if (titleBar != null) {
            // 这里根据你的 TitleBar API 进行设置
            // 例如设置搜索热词：
            List<String> hints = new ArrayList<>();
            hints.add("搜索好喝的奶茶");
            hints.add("低卡果茶推荐");
            titleBar.setSearchHints(hints);

            // 开启轮播
            titleBar.startRoll();

            // 如果这个页面不需要返回键（比如是首页 Tab），可以隐藏
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
        LocationManager.getInstance().updateLocation(latitude, longitude);
        autoRefresh();
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
        boolean hasMore = !pageData.isLast();
        if (page == 0) {
            // 下拉刷新成功时，记得重置 LoadMore 状态
            mLoadMoreController.reset();
            //直接调用父类 BaseRecyclerActivity 的方法
            //它内部会自动调用 refreshComplete()和 setList()
            refreshListSuccess(list);
        } else {
            loadMoreSuccess(list, hasMore);
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
    }

    @Override
    protected View getTitleBarView() {
        return mRootView.findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }

}
