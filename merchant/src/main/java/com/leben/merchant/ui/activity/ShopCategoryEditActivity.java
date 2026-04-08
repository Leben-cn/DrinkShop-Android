package com.leben.merchant.ui.activity;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

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
import com.leben.common.model.event.SelectAddressEvent;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.AddShopCategoryContract;
import com.leben.merchant.contract.DeleteShopCategoryContract;
import com.leben.merchant.contract.GetShopCategoryContract;
import com.leben.merchant.contract.UpdateCategorySortContract;
import com.leben.merchant.model.event.SelectShopCategoryEvent;
import com.leben.merchant.presenter.AddShopCategoryPresenter;
import com.leben.merchant.presenter.DeleteShopCategoryPresenter;
import com.leben.merchant.presenter.GetShopCategoryPresenter;
import com.leben.merchant.presenter.UpdateCategorySortPresenter;
import com.leben.merchant.ui.adapter.ShopCategoryAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = MerchantConstant.Router.CATEGORY_EDIT)
public class ShopCategoryEditActivity extends BaseRecyclerActivity<ShopCategoriesEntity> implements GetShopCategoryContract.View,
        AddShopCategoryContract.View, DeleteShopCategoryContract.View, UpdateCategorySortContract.View {

    private ImageView mIvAddCategory;

    private boolean isOrderChanged = false; // 标记顺序是否发生变化

    private TitleBar titleBar;

    private boolean isSelectMode = false;

    @InjectPresenter
    GetShopCategoryPresenter getShopCategoryPresenter;

    @InjectPresenter
    AddShopCategoryPresenter addShopCategoryPresenter;

    @InjectPresenter
    DeleteShopCategoryPresenter deleteShopCategoryPresenter;

    @InjectPresenter
    UpdateCategorySortPresenter updateCategorySortPresenter;

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
        titleBar = findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle("店铺内分类");
            mIvAddCategory = new ImageView(this);
            mIvAddCategory.setImageResource(R.drawable.ic_add);
            titleBar.addRightView(mIvAddCategory);
        }

        initDragHelper();
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        // 1. 添加分类逻辑
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
                }, throwable -> LogUtils.error("点击错误: " + throwable.getMessage()));

        if (mAdapter instanceof ShopCategoryAdapter) {
            ShopCategoryAdapter shopCategoryAdapter = (ShopCategoryAdapter) mAdapter;

            // 2. 删除逻辑
            shopCategoryAdapter.setOnItemDeleteListener(id -> {
                CommonDialog dialog = new CommonDialog();
                dialog.setContent("确认删除此分类吗？");
                dialog.setOnConfirmListener(result -> {
                    deleteShopCategoryPresenter.deleteShopCategory(id);
                    dialog.dismiss();
                });
                dialog.setOnCancelListener(result -> dialog.dismiss());
                dialog.show(getSupportFragmentManager(), "dialog_delete");
            });

            // 3. 列表项点击（选择模式）
            shopCategoryAdapter.setOnItemClickListener((view, position, entity) -> {
                if (isSelectMode) {
                    EventBus.getDefault().post(new SelectShopCategoryEvent(entity));
                }
                silentSaveAndExit(); // 静默保存并强制退出
            });
        }

        // 4. 标题栏返回键逻辑：显式触发弹窗逻辑
        titleBar.setOnBackListener(v -> finish());
    }

    @Override
    public void initData() {
        isSelectMode = getIntent().getBooleanExtra("isSelectMode", false);
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
        showError(errorMsg);
        LogUtils.error("删除店铺分类失败："+errorMsg);
    }

    private void initDragHelper() {
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                return makeMovementFlags(dragFlags, 0);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                // 交换数据并通知刷新
                mAdapter.getList().add(toPosition, mAdapter.getList().remove(fromPosition));
                mAdapter.notifyItemMoved(fromPosition, toPosition);

                isOrderChanged = true; // 标记已发生变动
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // 不支持滑动删除
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                // 当检测到正在拖拽（ACTION_STATE_DRAG）时，禁用下拉刷新
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    if (mRefreshController != null) {
                        mRefreshController.setEnableRefresh(false);
                    }
                    viewHolder.itemView.setPressed(true);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                // 当手指松开，条目动画结束归位后，重新启用下拉刷新
                if (mRefreshController != null) {
                    mRefreshController.setEnableRefresh(true);
                }
                viewHolder.itemView.setPressed(false);
            }
        });
        helper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onUpdateCategorySortSuccess(String data) {
        ToastUtils.show(this,"顺序更新成功");
    }

    @Override
    public void onUpdateCategorySortFailed(String errorMsg) {
        showError("顺序更新失败");
        LogUtils.error("顺序更新失败："+errorMsg);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void finish() {
        // 如果没有变动，直接退出
        if (!isOrderChanged) {
            super.finish();
            return;
        }
        // 如果有变动且不是通过 silentSaveAndExit 调用的，则弹窗
        showSaveSortDialog();
    }

    /**
     * 提取 ID 并发送请求的通用方法
     */
    private void postSortRequest() {
        List<Long> ids = new ArrayList<>();
        for (ShopCategoriesEntity entity : mAdapter.getList()) {
            ids.add(entity.getId());
        }
        updateCategorySortPresenter.updateCategorySort(ids);
        isOrderChanged = false; // 关键：重置标记，防止 finish() 递归
    }

    /**
     * 场景：点击分类项时的“静默保存并退出”
     */
    private void silentSaveAndExit() {
        if (isOrderChanged) {
            postSortRequest();
        }
        super.finish(); // 绕过重写的 finish() 弹窗逻辑
    }

    /**
     * 场景：返回键触发的“弹窗确认”
     */
    private void showSaveSortDialog() {
        new CommonDialog()
                .setTitle("提示")
                .setContent("分类顺序已更改，是否保存并退出？")
                .setOnConfirmListener(result -> {
                    postSortRequest();
                    super.finish();
                })
                .setOnCancelListener(result -> {
                    isOrderChanged = false;
                    super.finish();
                })
                .show(getSupportFragmentManager(), "dialog_save_sort");
    }
}
