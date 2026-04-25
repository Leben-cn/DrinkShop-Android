package com.leben.merchant.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.base.widget.dialog.ListSelectDialog;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.common.model.bean.GroupEntity;
import com.leben.common.model.bean.ShopCategoriesEntity;
import com.leben.common.model.bean.SpecGroupEntity;
import com.leben.common.model.bean.SpecOptionEntity;
import com.leben.common.util.ImagePickerHelper;
import com.leben.common.util.ListGroupUtils;
import com.leben.common.util.PermissionDialogHelper;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.DelectDrinkContract;
import com.leben.merchant.contract.GetAllSpecContract;
import com.leben.merchant.contract.SaveDrinkContract;
import com.leben.merchant.model.bean.DrinkRequestEntity;
import com.leben.merchant.model.event.RefreshDrinkListEvent;
import com.leben.merchant.model.event.SelectShopCategoryEvent;
import com.leben.merchant.presenter.DelectDrinkPresenter;
import com.leben.merchant.presenter.GetAllSpecPresenter;
import com.leben.merchant.presenter.SaveDrinkPresenter;
import com.leben.merchant.ui.dialog.DrinkSpecDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = MerchantConstant.Router.DRINK_EDIT)
public class DrinkEditActivity extends BaseActivity implements GetAllSpecContract.View, SaveDrinkContract.View , DelectDrinkContract.View {

    private String TAG;

    private DrinkEntity mDrink;

    private List<SpecOptionEntity> mSelectedSpecs = new ArrayList<>();

    private ImageView ivAddBtn;
    private ImageView ivDelPhoto;
    private ImageView ivSelectedPhoto;
    private ImagePickerHelper imagePickerHelper;
    private String currentPhotoPath;

    private Button mBtnSubmit;
    private TextInputEditText mInputName;
    private TextInputEditText mInputDesc;
    private TextInputEditText mInputPrice;
    private TextInputEditText mInputPackingFee;
    private TextInputEditText mInputStock;
    private TextView mTvDrinkSpec;
    private TextView mTvShopCategory;
    private TextView mTvCategory;

    private SwitchMaterial mSwitchStatus;

    private TitleBar titleBar;

    private ImageView mIvDelectDrink;

    private long categoryId;
    private long shopCategoryId;

    private List<GroupEntity<SpecGroupEntity, SpecOptionEntity>> allSystemGroups = new ArrayList<>();

    @InjectPresenter
    GetAllSpecPresenter getAllSpecPresenter;

    @InjectPresenter
    SaveDrinkPresenter saveDrinkPresenter;

    @InjectPresenter
    DelectDrinkPresenter delectDrinkPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_ac_drink_edit;
    }

    @Override
    public void onInit() {
        super.onInit();
        TAG = getIntent().getStringExtra("TAG");
        mDrink = (DrinkEntity) getIntent().getSerializableExtra("drink");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void initView() {
        mBtnSubmit = findViewById(R.id.btn_save);
        mInputName = findViewById(R.id.et_name);
        mInputDesc = findViewById(R.id.et_desc);
        mInputPrice = findViewById(R.id.et_price);
        mInputPackingFee = findViewById(R.id.et_packing_fee);
        mInputStock = findViewById(R.id.et_stock);
        mTvDrinkSpec = findViewById(R.id.tv_drink_spec);
        titleBar = findViewById(R.id.title_bar);

        ivAddBtn = findViewById(R.id.iv_add_btn);
        ivDelPhoto = findViewById(R.id.iv_del_photo);
        ivSelectedPhoto = findViewById(R.id.iv_selected_photo);
        mTvShopCategory = findViewById(R.id.tv_shop_category);
        mTvCategory = findViewById(R.id.tv_category);
        mSwitchStatus = findViewById(R.id.switch_status);

        mIvDelectDrink=new ImageView(this);
        mIvDelectDrink.setImageResource(R.drawable.pic_delect_drink);
        titleBar.addRightView(mIvDelectDrink, 24, 24);

        if ("DRINK_ADAPTER".equals(TAG)) {
            titleBar.setTitle("编辑商品");
            initEchoData();
        } else if ("GOOS_FRAGMENT".equals(TAG)) {
            titleBar.setTitle("添加商品");
        }

        imagePickerHelper = new ImagePickerHelper(this, path -> {
            this.currentPhotoPath = path;
            ivSelectedPhoto.setVisibility(View.VISIBLE);
            ivDelPhoto.setVisibility(View.VISIBLE);
            ivAddBtn.setVisibility(View.GONE);
            Glide.with(this).load(path).into(ivSelectedPhoto);
        });
    }

    /**
     * 回显编辑状态的数据
     */
    private void initEchoData() {
        if (mDrink == null) {
            return;
        }
        mInputName.setText(mDrink.getName());
        mInputDesc.setText(mDrink.getDescription());
        if (mDrink.getPrice() != null) {
            mInputPrice.setText(String.valueOf(mDrink.getPrice()));
        }
        mInputPackingFee.setText(String.valueOf(mDrink.getPackingFee()));
        mInputStock.setText(String.valueOf(mDrink.getStock()));
        mTvShopCategory.setText(mDrink.getShopCategories().getCategoryName());
        shopCategoryId=mDrink.getShopCategories().getId();
        mTvCategory.setText(mDrink.getCategories().getName());
        categoryId=mDrink.getCategories().getId();
        mSwitchStatus.setChecked(mDrink.getStatus() == 1);

        if (!TextUtils.isEmpty(mDrink.getImg())) {
            currentPhotoPath = mDrink.getImg();
            ivSelectedPhoto.setVisibility(View.VISIBLE);
            ivDelPhoto.setVisibility(View.VISIBLE);
            ivAddBtn.setVisibility(View.GONE);
            Glide.with(this).load(mDrink.getImg()).into(ivSelectedPhoto);
        }

        if (mDrink.getSpecs() != null) {
            mSelectedSpecs.addAll(mDrink.getSpecs());
            if (!mSelectedSpecs.isEmpty()) {
                mTvDrinkSpec.setText("已选 " + mSelectedSpecs.size() + " 项规格");
            } else {
                mTvDrinkSpec.setText("");
            }

        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(ivAddBtn)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                    List<String> options = Arrays.asList("拍照", "从相册选择");
                    ListSelectDialog.<String>newInstance()
                            .setData(options, item -> item) // 直接返回字符串本身
                            .setOnItemClickListener((item, position) -> {
                                if (position == 0) {
                                    new RxPermissions(this)
                                            .request(Manifest.permission.CAMERA)
                                            .subscribe(granted -> {
                                                if (granted) {
                                                    imagePickerHelper.openCamera();
                                                } else {
                                                    PermissionDialogHelper.showCameraPermissionDialog(this);
                                                }
                                            });
                                } else {
                                    imagePickerHelper.openGallery();
                                }
                            })
                            .show(getSupportFragmentManager(), "select_dialog");
                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(ivDelPhoto)
                .subscribe(unit -> {
                    currentPhotoPath = null;
                    ivSelectedPhoto.setVisibility(View.GONE);
                    ivDelPhoto.setVisibility(View.GONE);
                    ivAddBtn.setVisibility(View.VISIBLE);
                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(mTvDrinkSpec)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(unit -> {
//                    if (allSystemGroups == null || allSystemGroups.isEmpty()) {
//                        ToastUtils.show(this, "规格数据尚未加载完成，请稍后再试");
//                        return;
//                    }

                    DrinkSpecDialog dialog;
                    if (mDrink != null) {
                        dialog = DrinkSpecDialog.newInstance(mDrink, allSystemGroups);
                    } else {
                        dialog = DrinkSpecDialog.newInstance(allSystemGroups);
                    }

                    // 3. 监听商家勾选结果
                    dialog.setListener(selectedOptions -> {
                        this.mSelectedSpecs = selectedOptions;
                        if (!mSelectedSpecs.isEmpty()) {
                            mTvDrinkSpec.setText("已选 " + mSelectedSpecs.size() + " 项规格");
                        } else {
                            mTvDrinkSpec.setText("");
                        }

//                        for (SpecOptionEntity spec : mSelectedSpecs) {
//                            LogUtils.info("规格名称: " + spec.getOptionName() + ", 规格ID: " + spec.getOptionId());
//                        }

                        // 同步更新本地实体，防止关掉重开丢状态
                        if (mDrink != null) {
                            mDrink.setSpecs(selectedOptions);
                        }
                    });

                    // 4. 显示弹窗
                    dialog.show(getSupportFragmentManager(), "dialog_drinkSpec");

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(mBtnSubmit)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                    // 1. 在 subscribe 内部直接进行表单校验
                    String name = getText(mInputName);
                    String desc = getText(mInputDesc);
                    String price = getText(mInputPrice);
                    String packingFee = getText(mInputPackingFee);
                    String stock = getText(mInputStock);

                    if (TextUtils.isEmpty(currentPhotoPath)) { showError("请设置商品照片"); return; }
                    if (TextUtils.isEmpty(name)) { showError("请输入商品名称"); return; }
                    if (TextUtils.isEmpty(desc)) { showError("请输入商品描述"); return; }
                    if (TextUtils.isEmpty(price)) { showError("请输入商品价格"); return; }
                    if (TextUtils.isEmpty(packingFee)) { showError("请输入包装费"); return; }
                    if (TextUtils.isEmpty(stock)) { showError("请输入商品库存"); return; }
                    if (categoryId <= 0) { showError("请选择商品分类"); return; }

                    // 2. 逻辑转换（BigDecimal 等）
                    try {
                        BigDecimal priceValue = new BigDecimal(price);
                        BigDecimal packingFeeValue = new BigDecimal(packingFee);
                        int stockValue = Integer.parseInt(stock);

                        // 3. 构建 RequestEntity
                        List<com.leben.merchant.model.bean.SpecOptionEntity> merchantSpecs = new ArrayList<>();
                        for (SpecOptionEntity commonSpec : mSelectedSpecs) {
                            com.leben.merchant.model.bean.SpecOptionEntity merchantSpec = new com.leben.merchant.model.bean.SpecOptionEntity();
                            merchantSpec.setSpecOptionId(commonSpec.getOptionId());
                            merchantSpec.setPriceAdjust(commonSpec.getPrice());
                            merchantSpec.setIsDefault(0);
                            merchantSpecs.add(merchantSpec);
                        }

                        DrinkRequestEntity requestData = new DrinkRequestEntity();
                        if(mDrink != null) requestData.setId(mDrink.getId());
                        requestData.setName(name);
                        requestData.setDescription(desc);
                        requestData.setPrice(priceValue);
                        requestData.setPackingFee(packingFeeValue);
                        requestData.setStock(stockValue);
                        requestData.setImg(currentPhotoPath);
                        requestData.setCategoryId(categoryId);
                        requestData.setShopCategoryId(shopCategoryId);
                        requestData.setStatus(mSwitchStatus.isChecked() ? 1 : 2);
                        requestData.setSpecs(merchantSpecs);

                        // 4. 执行保存
                        showLoading("正在保存...");
                        saveDrinkPresenter.saveDrink(requestData);

                    } catch (Exception e) {
                        showError("输入格式错误");
                    }
                }, throwable -> {
                    // 这里的 throwable 只有在 View 销毁等极端情况下才会触发
                    LogUtils.error("点击流发生异常: " + throwable.getMessage());
                });

        RxView.clicks(mTvShopCategory)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {

                    ARouter.getInstance()
                            .build(MerchantConstant.Router.CATEGORY_EDIT)
                            .withBoolean("isSelectMode", true)
                            .navigation();
                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(mTvCategory)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {

                    List<String> options = Arrays.asList("奶茶", "果茶", "果汁", "咖啡");
                    ListSelectDialog.<String>newInstance()
                            .setData(options, item -> item) // 直接返回字符串本身
                            .setOnItemClickListener((item, position) -> {
                                categoryId = (long) position+1L;
                                mTvCategory.setText(item);
                            })
                            .show(getSupportFragmentManager(), "select_dialog");

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(mIvDelectDrink)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    CommonDialog dialog = new CommonDialog();
                    dialog.setContent("确认删除此商品吗？");
                    dialog.setOnConfirmListener(data -> {
                        delectDrinkPresenter.delectDrink(mDrink.getId());
                        dialog.dismiss();
                    });
                    dialog.setOnCancelListener(data -> dialog.dismiss());
                    dialog.show(getSupportFragmentManager(), "dialog_delect_drink");
                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

    }

    @Override
    public void initData() {
        getAllSpecPresenter.getAllSpec();
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    public void onGetAllSpecSuccess(List<SpecOptionEntity> data) {
        // 获取当前饮品已经设置的规格（如果有的话）
        List<SpecOptionEntity> drinkSpecs = (mDrink != null) ? mDrink.getSpecs() : null;

        // 数据预处理：用饮品实际的加价和选中状态 覆盖系统默认字典
        if (drinkSpecs != null && !drinkSpecs.isEmpty()) {
            // 为了提高效率，先把饮品规格转成 Map
            Map<Long, SpecOptionEntity> drinkSpecMap = new HashMap<>();
            for (SpecOptionEntity spec : drinkSpecs) {
                drinkSpecMap.put(spec.getOptionId(), spec);
            }

            // 遍历全量系统字典进行覆盖
            for (SpecOptionEntity systemItem : data) {
                SpecOptionEntity savedItem = drinkSpecMap.get(systemItem.getOptionId());
                if (savedItem != null) {
                    // 用保存的价格覆盖系统默认价格
                    systemItem.setPrice(savedItem.getPrice());
                    // 设置为选中状态，这样弹窗打开时就是勾选上的
                    systemItem.setSelected(true);
                } else {
                    // 如果系统字典里有，但饮品里没存，则确保它是未选中状态
                    systemItem.setSelected(false);
                }
            }
        }

        // 执行分组操作（此时 data 已经是合并后的“完全体”数据了）
        this.allSystemGroups = ListGroupUtils.groupList(data, item -> new SpecGroupEntity(
                item.getGroupId(),
                item.getGroupName(),
                item.getIsMultiple()
        ));

        // 更新已选列表，确保 Activity 里的 mSelectedSpecs 同步
        if (drinkSpecs != null) {
            this.mSelectedSpecs.clear();
            this.mSelectedSpecs.addAll(drinkSpecs);
        }
    }

    @Override
    public void onGetAllSpecFailed(String errorMsg) {
        showError("获取商品规格字典失败");
        LogUtils.error("获取商品规格字典失败: " + errorMsg);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 店铺分类回填
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectShopCategoryEvent(SelectShopCategoryEvent event) {
        if (event != null && event.getCategory() != null) {
            ShopCategoriesEntity categoryEvent = event.getCategory();
            mTvShopCategory.setText(categoryEvent.getCategoryName());
            shopCategoryId=categoryEvent.getId();
        }
    }

    private String getText(EditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    @Override
    public void onSaveDrinkSuccess(String data) {
        hideLoading();
        ToastUtils.show(this,"修改成功");
        EventBus.getDefault().post(new RefreshDrinkListEvent());
        finish();
    }

    @Override
    public void onSaveDrinkFailed(String errorMsg) {
        hideLoading();
        showError("修改失败");
        LogUtils.error("修改失败"+errorMsg);
    }

    @Override
    public void onDelectDrinkSuccess(String data) {
        ToastUtils.show(this,"删除成功");
        EventBus.getDefault().post(new RefreshDrinkListEvent());
        finish();
    }

    @Override
    public void onDelectDrinkFailed(String errorMsg) {
        showError(errorMsg);
        LogUtils.error("删除商品失败："+errorMsg);
    }
}