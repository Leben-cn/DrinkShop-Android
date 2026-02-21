package com.leben.merchant.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.base.widget.dialog.InputDialog;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.ShopCategoriesEntity;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.AddShopCategoryContract;
import com.leben.merchant.contract.DeleteShopCategoryContract;
import com.leben.merchant.contract.GetShopCategoryContract;
import com.leben.merchant.presenter.AddShopCategoryPresenter;
import com.leben.merchant.presenter.DeleteShopCategoryPresenter;
import com.leben.merchant.presenter.GetShopCategoryPresenter;
import com.leben.merchant.ui.adapter.ShopCategoryAdapter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = MerchantConstant.Router.CATEGORY_EDIT)
public class ShopCategoryEditActivity extends BaseRecyclerActivity<ShopCategoriesEntity> implements GetShopCategoryContract.View,
        AddShopCategoryContract.View, DeleteShopCategoryContract.View {

    private ImageView mIvAddCategory;

    @InjectPresenter
    GetShopCategoryPresenter getShopCategoryPresenter;

    @InjectPresenter
    AddShopCategoryPresenter addShopCategoryPresenter;

    @InjectPresenter
    DeleteShopCategoryPresenter deleteShopCategoryPresenter;

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
        // 必须在 super.initView() 之前调用 setDefaultSpace
        setDefaultSpace(6);
        super.initView();
        TitleBar titleBar = findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle("店铺内分类");
            mIvAddCategory = new ImageView(this);
            mIvAddCategory.setImageResource(R.drawable.ic_add);
            titleBar.addRightView(mIvAddCategory);
        }
    }

    @Override
    public void initListener() {
        RxView.clicks(mIvAddCategory)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(entity -> {
                    InputDialog.newInstance()
                            .setTitle("添加店铺内分类")
                            .setHint("输入新分类名称")
                            .setOnConfirmListener((dialog, inputText) -> {
                                if (!TextUtils.isEmpty(inputText)) {
                                    addShopCategoryPresenter.addShopCategory(inputText);
                                }
                            })
                            .show(getSupportFragmentManager(), "dialog_InputShopCategory");
                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
        if (mAdapter instanceof ShopCategoryAdapter) {
            ShopCategoryAdapter shopCategoryAdapter=(ShopCategoryAdapter) mAdapter;
            shopCategoryAdapter.setOnItemDeleteListener(new ShopCategoryAdapter.OnItemDeleteListener() {
                @Override
                public void onDeleteClick(Long id) {
                    CommonDialog dialog = new CommonDialog();
                    dialog.setContent("确认删除此分类吗？");
                    dialog.setOnConfirmListener(result -> {
                        deleteShopCategoryPresenter.deleteShopCategory(id);
                        dialog.dismiss();
                    });
                    dialog.setOnCancelListener(result -> dialog.dismiss());
                    dialog.show(getSupportFragmentManager(), "dialog_history");
                }
            });
        }
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
        LogUtils.error("获取店铺分类失败：" + errorMsg);
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }

    @Override
    public void onAddShopCategorySuccess(String data) {
        onRefresh();
        ToastUtils.show(this,"添加成功");
    }

    @Override
    public void onAddShopCategoryFailed(String errorMsg) {
        showError("添加店铺分类失败");
        LogUtils.error("添加店铺分类失败："+errorMsg);
    }

    @Override
    public void onDeleteShopCategorySuccess(String data) {
        onRefresh();
        ToastUtils.show(this,"删除成功");
    }

    @Override
    public void onDeleteShopCategoryFailed(String errorMsg) {
        showError("删除店铺分类失败");
        LogUtils.error("删除店铺分类失败："+errorMsg);
    }
}
