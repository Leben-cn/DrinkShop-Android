package com.leben.shop.ui.activity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseSearchActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.common.LocationManager;
import com.leben.shop.constant.ShopConstant;
import com.leben.shop.contract.QueryDrinkContract;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.shop.model.bean.DrinkQueryEntity;
import com.leben.shop.model.bean.PageEntity;
import com.leben.shop.presenter.QueryDrinkPresenter;
import com.leben.shop.ui.adapter.DrinkAdapter;
import java.util.List;

@Route(path = ShopConstant.Router.SEARCH_DRINK)
public class DrinksSearchActivity extends BaseSearchActivity<DrinkEntity> implements QueryDrinkContract.View {

    private int page=0;
    private int size=10;

    Double userLat;
    Double userLon;

    @InjectPresenter
    QueryDrinkPresenter queryDrinkPresenter;

    @Override
    protected BaseRecyclerAdapter<DrinkEntity> createAdapter() {
        return new DrinkAdapter(this);
    }

    @Override
    public void onInit() {
        super.onInit();
        userLat= LocationManager.getInstance().getLatitude();
        userLon=LocationManager.getInstance().getLongitude();
    }

    @Override
    protected void doSearch(String keyword) {
        page = 0;

        DrinkQueryEntity entity = new DrinkQueryEntity();
        entity.setName(keyword);
        entity.setUserLat(userLat);
        entity.setUserLon(userLon);
        // 发起请求
        queryDrinkPresenter.queryDrink(entity,page,size);
    }
    @Override
    protected void onSearchResultClick(DrinkEntity item, int position) {
        ARouter.getInstance()
                .build(ShopConstant.Router.SHOP)
                .withLong("shopId", item.getShopCategories().getShopId())
                .withLong("drinkId", item.getId())
                .navigation();
    }

    @Override
    public void onQueryDrinkSuccess(PageEntity<DrinkEntity> data) {
        if (data == null) {
            refreshListSuccess(null);
            return;
        }
        List<DrinkEntity> list = data.getContent();
        boolean hasMore = !data.isLast();

        // 根据当前页码判断是刷新还是加载更多
        if (page == 0) {
            refreshListSuccess(list);
        } else {
            loadMoreSuccess(list, hasMore);
        }
    }

    @Override
    public void onQueryDrinkFailed(String errorMsg) {
        hideLoading();
        // 错误处理逻辑
        if (page == 0) {
            refreshListFailed(errorMsg);
        } else {
            page--; // 加载失败，回退页码
            loadMoreFailed();
            showError(errorMsg);
        }
        showError("查询饮品出错");
        LogUtils.error("查询饮品出错："+errorMsg);
    }

    @Override
    protected void doLoadMore(String keyword) {
        page++;
        DrinkQueryEntity entity = new DrinkQueryEntity();
        entity.setName(keyword);
        entity.setUserLat(userLat);
        entity.setUserLon(userLon);
        queryDrinkPresenter.queryDrink(entity,page,size);
    }

    @Override
    protected String getHistoryKey() {
        return "drink_search_history";
    }
}
