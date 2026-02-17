package com.leben.user.ui.activity;

import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.LocationManager;
import com.leben.common.model.bean.ShopEntity;
import com.leben.user.R;
import com.leben.user.constant.UserConstant;
import com.leben.user.contract.GetMyFavoriteContract;
import com.leben.user.presenter.GetMyFavoritePresenter;
import com.leben.user.ui.adapter.FavoriteAdapter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Route(path = UserConstant.Router.FAVORITE)
public class MyFavoriteActivity extends BaseRecyclerActivity<ShopEntity> implements GetMyFavoriteContract.View {

    @InjectPresenter
    GetMyFavoritePresenter getMyFavoritePresenter;

    @Override
    protected BaseRecyclerAdapter<ShopEntity> createAdapter() {
        return new FavoriteAdapter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.user_ac_favorite_list;
    }

    @Override
    public void initView() {
        super.initView();
        TitleBar titleBar=findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle("收藏中心");
        }
    }

    @Override
    public void initListener() {
        if (mAdapter instanceof FavoriteAdapter) {
            ((FavoriteAdapter) mAdapter).observeClicks()
                    .throttleFirst(500, TimeUnit.MILLISECONDS)
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(event -> {
                        Long shopId = event.shop.getId();
                        long drinkId = (event.drink != null) ? event.drink.getId() : -1;
                        ARouter.getInstance()
                                .build(UserConstant.Router.SHOP)
                                .withSerializable("shopId", shopId)
                                .withLong("drinkId", drinkId)
                                .navigation();
                    }, throwable -> {
                        LogUtils.error("点击事件流出错: " + throwable.getMessage());
                    });
        }
    }

    @Override
    public void initData() {
        autoRefresh();
    }

    @Override
    public void onRefresh() {
        Double latitude= LocationManager.getInstance().getLatitude();
        Double longitude=LocationManager.getInstance().getLongitude();
        getMyFavoritePresenter.getMyFavorite(latitude,longitude);
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    public void onGetMyFavoriteSuccess(List<ShopEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetMyFavoriteFailed(String errorMsg) {
        refreshListFailed("获取收藏店铺失败");
        LogUtils.error("获取收藏店铺失败："+errorMsg);
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        autoRefresh();
    }
}
