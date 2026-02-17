package com.leben.shop.ui.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.LocationManager;
import com.leben.shop.R;
import com.leben.shop.constant.ShopConstant;
import com.leben.shop.contract.QueryDrinkContract;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.shop.model.bean.DrinkQueryEntity;
import com.leben.shop.model.bean.PageEntity;
import com.leben.shop.presenter.QueryDrinkPresenter;
import com.leben.shop.ui.adapter.DrinkAdapter;

import java.util.List;

@Route(path = ShopConstant.Router.PLATFORM_CLASSIFY)
public class PlatFormClassifyActivity extends BaseRecyclerActivity<DrinkEntity> implements QueryDrinkContract.View {

    @InjectPresenter
    QueryDrinkPresenter queryDrinkPresenter;

    private Long classifyId;
    private int page = 0;
    private int size=10;
    private DrinkQueryEntity entity;

    @Override
    protected BaseRecyclerAdapter<DrinkEntity> createAdapter() {
        return new DrinkAdapter(this);
    }

    @Override
    public void onInit() {
        super.onInit();
        classifyId= getIntent().getLongExtra("CLASSIFY",-1);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.shop_ac_platform_classify;
    }

    @Override
    public void initView() {
        super.initView();
        TitleBar titleBar=findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle("热销推荐");
        }
    }

    @Override
    public void initListener() {
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<DrinkEntity>() {
            @Override
            public void onItemClick(View view, int position, DrinkEntity entity) {
                ARouter.getInstance()
                        .build(ShopConstant.Router.SHOP)
                        .withLong("shopId", entity.getShopCategories().getShopId())
                        .withLong("drinkId", entity.getId())
                        .navigation();
            }
        });
    }

    @Override
    public void initData() {
        autoRefresh();
    }

    @Override
    public void onRefresh() {
        page=0;
        Double userLat= LocationManager.getInstance().getLatitude();
        Double userLon=LocationManager.getInstance().getLongitude();

        entity = new DrinkQueryEntity();
        if(classifyId!=-1){
            entity.setCategoryId(classifyId);
        }
        entity.setUserLat(userLat);
        entity.setUserLon(userLon);
        // 发起请求
        queryDrinkPresenter.queryDrink(entity,page,size);
    }

    @Override
    public void onQueryDrinkSuccess(PageEntity<DrinkEntity> data) {
        List<DrinkEntity> list=data.getContent();
        boolean hasMore=!data.isLast();
        if (page == 0) {
            mLoadMoreController.reset();
            refreshListSuccess(list);
        }else{
            loadMoreSuccess(list,hasMore);
        }
    }

    @Override
    public void onQueryDrinkFailed(String errorMsg) {
        if (page == 0) {
            refreshListFailed(errorMsg);
        } else {
            page--;
            loadMoreFailed();
            showError(errorMsg);
        }
    }

    @Override
    public void onLoadMore() {
        page++;
        queryDrinkPresenter.queryDrink(entity,page,size);
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }
}
