package com.leben.merchant.ui.fragment;

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
import com.leben.common.Constant.CommonConstant;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.GetShopDrinkContract;
import com.leben.merchant.presenter.ShopDrinkPresenter;
import com.leben.merchant.ui.adapter.DrinkAdapter;
import com.leben.merchant.util.MerchantUtils;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by youjiahui on 2026/2/17.
 */
@Route(path = CommonConstant.Router.GOODS)
public class GoodsFragment extends BaseRecyclerFragment<DrinkEntity> implements GetShopDrinkContract.View {

    private ImageView mIvAddDrink;

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
    protected void initView(View root) {
        super.initView(root);
        TitleBar titleBar=root.findViewById(R.id.title_bar);
        mIvAddDrink=new ImageView(getContext());
        mIvAddDrink.setImageResource(R.drawable.pic_add);
        titleBar.addRightView(mIvAddDrink);
        titleBar.setBackVisible(false);
    }

    @Override
    public void onRefresh() {
        shopDrinkPresenter.getShopDrinks(MerchantUtils.getMerchantId(getContext()));
    }

    @Override
    public void initListener() {
        RxView.clicks(mIvAddDrink)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.ADD_DRINK)
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
}
