package com.leben.merchant.ui.activity;

import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.ShopCategoriesEntity;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.GetShopCategoryContract;
import com.leben.merchant.presenter.GetShopCategoryPresenter;
import com.leben.merchant.ui.adapter.ShopCategoryAdapter;
import java.util.List;

@Route(path = MerchantConstant.Router.CATEGORY_EDIT)
public class ShopCategoryEditActivity extends BaseRecyclerActivity<ShopCategoriesEntity> implements GetShopCategoryContract.View {

    @InjectPresenter
    GetShopCategoryPresenter getShopCategoryPresenter;

    @Override
    protected BaseRecyclerAdapter<ShopCategoriesEntity> createAdapter() {
        return new ShopCategoryAdapter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_ac_edit_categoty;
    }

    @Override
    public void initView() {
        super.initView();
        TitleBar titleBar=findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle("店铺内分类");
        }
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        autoRefresh();
    }

    @Override
    public void onRefresh() {
        getShopCategoryPresenter.getShopCategory();
    }

    @Override
    public void onGetShopCategorySuccess(List<ShopCategoriesEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetShopCategoryFailed(String errorMsg) {
        refreshListFailed("获取店铺分类失败");
        LogUtils.error("获取店铺分类失败："+errorMsg);
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }

}
