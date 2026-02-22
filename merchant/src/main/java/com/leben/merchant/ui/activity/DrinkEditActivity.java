package com.leben.merchant.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.AddressEntity;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.common.model.bean.GroupEntity;
import com.leben.common.model.bean.ShopCategoriesEntity;
import com.leben.common.model.bean.SpecGroupEntity;
import com.leben.common.model.bean.SpecOptionEntity;
import com.leben.common.model.event.SelectAddressEvent;
import com.leben.common.util.ImagePickerHelper;
import com.leben.common.util.ListGroupUtils;
import com.leben.common.util.PermissionDialogHelper;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.GetAllSpecContract;
import com.leben.merchant.model.event.SelectShopCategoryEvent;
import com.leben.merchant.presenter.GetAllSpecPresenter;
import com.leben.merchant.ui.dialog.DrinkSpecDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = MerchantConstant.Router.DRINK_EDIT)
public class DrinkEditActivity extends BaseActivity implements GetAllSpecContract.View {

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

    private TitleBar titleBar;

    private List<GroupEntity<SpecGroupEntity, SpecOptionEntity>> allSystemGroups = new ArrayList<>();

    @InjectPresenter
    GetAllSpecPresenter getAllSpecPresenter;

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
        mTvShopCategory=findViewById(R.id.tv_shop_category);

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
        // 回显包装费和库存 (请确保 DrinkEntity 有对应的 getter 方法，如果没有请根据实际修改)
         mInputPackingFee.setText(String.valueOf(mDrink.getPackingFee()));
         mInputStock.setText(String.valueOf(mDrink.getStock()));

        // 回显图片
        if (!TextUtils.isEmpty(mDrink.getImg())) {
            currentPhotoPath = mDrink.getImg();
            ivSelectedPhoto.setVisibility(View.VISIBLE);
            ivDelPhoto.setVisibility(View.VISIBLE);
            ivAddBtn.setVisibility(View.GONE);
            Glide.with(this).load(mDrink.getImg()).into(ivSelectedPhoto);
        }

        // 回显已选规格
        if (mDrink.getSpecs() != null) {
            mSelectedSpecs.addAll(mDrink.getSpecs());
            if(!mSelectedSpecs.isEmpty()){
                mTvDrinkSpec.setText("已选 " + mSelectedSpecs.size() + " 项规格");
            }else{
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
                    String[] items = {"拍照", "从相册选择"};
                    new AlertDialog.Builder(this)
                            .setItems(items, (dialog, which) -> {
                                if (which == 0) {
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
                            .show();
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
                    if (allSystemGroups == null || allSystemGroups.isEmpty()) {
                        ToastUtils.show(this, "规格数据尚未加载完成，请稍后再试");
                        return;
                    }

                    DrinkSpecDialog dialog;
                    if (mDrink != null) {
                        dialog = DrinkSpecDialog.newInstance(mDrink, allSystemGroups);
                    } else {
                        dialog = DrinkSpecDialog.newInstance(allSystemGroups);
                    }

                    // 3. 监听商家勾选结果
                    dialog.setListener(selectedOptions -> {
                        this.mSelectedSpecs = selectedOptions;
                        if(!mSelectedSpecs.isEmpty()){
                            mTvDrinkSpec.setText("已选 " + mSelectedSpecs.size() + " 项规格");
                        }else{
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

        // 【核心修复】：组装表单提交数据
        RxView.clicks(mBtnSubmit)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(unit -> {
                    String name = mInputName.getText() != null ? mInputName.getText().toString().trim() : "";
                    String desc = mInputDesc.getText() != null ? mInputDesc.getText().toString().trim() : "";
                    String price = mInputPrice.getText() != null ? mInputPrice.getText().toString().trim() : "";
                    String packingFee = mInputPackingFee.getText() != null ? mInputPackingFee.getText().toString().trim() : "";
                    String stock = mInputStock.getText() != null ? mInputStock.getText().toString().trim() : "";

                    if (TextUtils.isEmpty(currentPhotoPath)) {
                        showError("请设置商品照片");
                        return false;
                    }
                    if (TextUtils.isEmpty(name)) {
                        showError("请输入商品名称");
                        return false;
                    }
                    if (TextUtils.isEmpty(desc)) {
                        showError("请输入商品描述");
                        return false;
                    }
                    if (TextUtils.isEmpty(price)) {
                        showError("请输入商品价格");
                        return false;
                    }
                    if (TextUtils.isEmpty(stock)) {
                        showError("请输入商品库存");
                        return false;
                    }
                    // 根据业务需要，可以增加 packingFee 判空
                    return true;
                })
                .subscribe(result -> {
//                    try {
//                        // 1. 组装要提交的 DrinkEntity 对象
//                        DrinkEntity requestData = new DrinkEntity();
//                        if (mDrink != null) {
//                            requestData.setId(mDrink.getId()); // 编辑模式：必须带上原本的商品 ID
//                        }
//
//                        requestData.setName(mInputName.getText().toString().trim());
//                        requestData.setDesc(mInputDesc.getText().toString().trim());
//                        requestData.setImg(currentPhotoPath);
//                        requestData.setPrice(new BigDecimal(mInputPrice.getText().toString().trim()));
//
//                        // 根据你的 DrinkEntity 实体定义，如果有对应字段，请放开下面注释
//                        // requestData.setPackingFee(new BigDecimal(mInputPackingFee.getText().toString().trim()));
//                        // requestData.setStock(Integer.parseInt(mInputStock.getText().toString().trim()));
//
//                        // 将最终勾选的规格绑定到商品上
//                        requestData.setSpecs(mSelectedSpecs);
//
//                        // 2. 发起网络请求
//                        showLoading("正在保存...");
//                        // TODO: 替换成你实际的 Presenter 方法，例如：
//                        // saveDrinkPresenter.saveOrUpdateDrink(requestData);
//
//                    } catch (NumberFormatException e) {
//                        showError("价格或库存金额格式错误");
//                    }
                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(mTvShopCategory)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    ARouter.getInstance()
                            .build(MerchantConstant.Router.CATEGORY_EDIT)
                            .withBoolean("isSelectMode", true)
                            .navigation();

                },throwable -> {
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
        // 1. 获取当前饮品已经设置的规格（如果有的话）
        List<SpecOptionEntity> drinkSpecs = (mDrink != null) ? mDrink.getSpecs() : null;

        // 2. 数据预处理：用饮品实际的加价和选中状态 覆盖 系统默认字典
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
                    // 【关键点】：用保存的价格覆盖系统默认价格
                    systemItem.setPrice(savedItem.getPrice());
                    // 设置为选中状态，这样弹窗打开时就是勾选上的
                    systemItem.setSelected(true);
                } else {
                    // 如果系统字典里有，但饮品里没存，则确保它是未选中状态
                    systemItem.setSelected(false);
                }
            }
        }

        // 3. 执行分组操作（此时 data 已经是合并后的“完全体”数据了）
        this.allSystemGroups = ListGroupUtils.groupList(data, item -> new SpecGroupEntity(
                item.getGroupId(),
                item.getGroupName(),
                item.getIsMultiple()
        ));

        // 4. (可选) 更新已选列表，确保 Activity 里的 mSelectedSpecs 同步
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
        }
    }
}