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
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.util.ImagePickerHelper;
import com.leben.common.util.PermissionDialogHelper;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.model.bean.MerchantRegisterEntity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = MerchantConstant.Router.DRINK_EDIT)
public class DrinkEditActivity extends BaseActivity {

    private String TAG;

    private ImageView ivAddBtn;
    private ImageView ivDelPhoto;
    private ImageView ivSelectedPhoto;
    private ImagePickerHelper imagePickerHelper;
    private String currentPhotoPath; // 当前选中的图片本地路径

    private Button mBtnSubmit;
    private TextInputEditText mInputName;
    private TextInputEditText mInputDesc;
    private TextInputEditText mInputPrice;
    private TextInputEditText mInputPackingFee;
    private TextInputEditText mInputStock;

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_ac_drink_edit;
    }

    @Override
    public void onInit() {
        super.onInit();
        TAG= getIntent().getStringExtra("TAG");
    }

    @Override
    public void initView() {

        mBtnSubmit=findViewById(R.id.btn_save);
        mInputName=findViewById(R.id.et_name);
        mInputDesc=findViewById(R.id.et_desc);
        mInputPrice=findViewById(R.id.et_price);
        mInputPackingFee=findViewById(R.id.et_packing_fee);
        mInputStock=findViewById(R.id.et_stock);

        TitleBar titleBar=findViewById(R.id.title_bar);
        if("DRINK_ADAPTER".equals(TAG)){
            titleBar.setTitle("编辑商品");
        }else if("GOOS_FRAGMENT".equals(TAG)){
            titleBar.setTitle("添加商品");
        }

        ivAddBtn=findViewById(R.id.iv_add_btn);
        ivDelPhoto=findViewById(R.id.iv_del_photo);
        ivSelectedPhoto=findViewById(R.id.iv_selected_photo);
        
        imagePickerHelper=new ImagePickerHelper(this,path -> {

            this.currentPhotoPath = path;
            ivSelectedPhoto.setVisibility(View.VISIBLE);
            ivDelPhoto.setVisibility(View.VISIBLE);
            ivAddBtn.setVisibility(View.GONE);

            Glide.with(this).load(path).into(ivSelectedPhoto);
        });
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
                                    // 直接启动相册选择器
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
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(mBtnSubmit)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(unit->{
                    String name = mInputName.getText() != null ? mInputName.getText().toString().trim() : "";
                    String desc = mInputDesc.getText() != null ? mInputDesc.getText().toString().trim() : "";
                    String price=mInputPrice.getText() != null ? mInputPrice.getText().toString().trim() : "";
                    String packingFee=mInputPackingFee.getText() != null ? mInputPackingFee.getText().toString().trim() : "";
                    String stock=mInputStock.getText() != null ? mInputStock.getText().toString().trim() : "";
                    if(TextUtils.isEmpty(currentPhotoPath)){
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
                    if(TextUtils.isEmpty(price)){
                        showError("请输入商品价格");
                        return false;
                    }
                    if(TextUtils.isEmpty(stock)){
                        showError("请输入商品库存");
                        return false;
                    }
                    return true;
                })
                .subscribe(result->{

//                    try {
//                        request.minOrder = Double.parseDouble(mInputPrice.getText().toString().trim());
//                        request.deliveryFee = Double.parseDouble(mEdDeliveryFee.getText().toString().trim());
//                    } catch (NumberFormatException e) {
//                        showError("金额格式错误");
//                    }
//                    registerPresenter.register(request);
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
    }

    @Override
    public void initData() {

    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }
}
