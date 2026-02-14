package com.leben.merchant.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.ShopEntity;
import com.leben.common.util.ImagePickerHelper;
import com.leben.common.util.PermissionDialogHelper;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.RegisterContract;
import com.leben.merchant.model.bean.MerchantRegisterEntity;
import com.leben.merchant.presenter.RegisterPresenter;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = MerchantConstant.Router.REGISTER)
public class RegisterActivity extends BaseActivity implements RegisterContract.View {

    private EditText mEdAccount;
    private EditText mEdPassword;
    private EditText mEdPhone;
    private EditText mEdShopName;
    private EditText mEdDesc;
    private EditText mEdMinOrder;
    private EditText mEdDeliveryFee;
    private Button mBtnRegister;

    private ImageView ivAddBtn;
    private ImageView ivDelPhoto;
    private ImageView ivSelectedPhoto;
    private ImagePickerHelper imagePickerHelper;
    private String currentPhotoPath; // 当前选中的图片本地路径

    private Double latitude=-10000.0;
    private Double longitude=-10000.0;
    private TextView mTvAddress;

    private static final int REQUEST_CODE_ADDRESS = 100;

    @InjectPresenter
    RegisterPresenter registerPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_merchant_register;
    }

    @Override
    public void initView() {
        TitleBar titleBar=findViewById(R.id.title_bar);
        mEdAccount=findViewById(R.id.et_account);
        mEdPassword=findViewById(R.id.et_password);
        mEdPhone=findViewById(R.id.et_phone);
        mEdShopName=findViewById(R.id.et_shop_name);
        mEdDesc=findViewById(R.id.et_description);
        mEdMinOrder=findViewById(R.id.et_min_order);
        mEdDeliveryFee=findViewById(R.id.et_delivery_fee);
        mBtnRegister=findViewById(R.id.btn_register);
        mTvAddress=findViewById(R.id.tv_address_selector);

        ivAddBtn=findViewById(R.id.iv_add_btn);
        ivDelPhoto=findViewById(R.id.iv_del_photo);
        ivSelectedPhoto=findViewById(R.id.iv_selected_photo);

        titleBar.setTitle("商家入驻");

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
        RxView.clicks(mBtnRegister)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(unit->{
                    String account = mEdAccount.getText().toString().trim();
                    String password = mEdPassword.getText().toString().trim();
                    String phone=mEdPhone.getText().toString().trim();
                    String shopName=mEdShopName.getText().toString().trim();
                    String desc=mEdDesc.getText().toString().trim();
                    String minOrder=mEdMinOrder.getText().toString().trim();
                    String deliveryFee=mEdDeliveryFee.getText().toString().trim();
                    if (TextUtils.isEmpty(account)) {
                        showError("请输入账号");
                        return false;
                    }
                    if (TextUtils.isEmpty(password)) {
                        showError("请输入密码");
                        return false;
                    }
                    if(TextUtils.isEmpty(phone)){
                        showError("请输入联系方式");
                        return false;
                    }
                    if(TextUtils.isEmpty(currentPhotoPath)){
                        showError("请设置店铺照片");
                        return false;
                    }
                    if (TextUtils.isEmpty(shopName)) {
                        showError("请输入店铺名称");
                        return false;
                    }
                    if (latitude == -10000.00 || longitude == -10000.00) {
                        ToastUtils.show(this, "请设置店铺地址");
                        return false;
                    }
                    if (latitude == 0.00 || longitude == 0.00) {
                        ToastUtils.show(this, "定位信息获取失败，请重新定位");
                        return false;
                    }
                    if (TextUtils.isEmpty(desc)) {
                        showError("请输入店铺简介");
                        return false;
                    }
                    if (TextUtils.isEmpty(minOrder)) {
                        showError("请输入起送价");
                        return false;
                    }
                    if (TextUtils.isEmpty(deliveryFee)) {
                        showError("请输入配送费");
                        return false;
                    }
                    return true;
                })
                .subscribe(result->{
                    MerchantRegisterEntity request = new MerchantRegisterEntity();
                    request.account = mEdAccount.getText().toString().trim();
                    request.password = mEdPassword.getText().toString().trim();
                    request.phone = mEdPhone.getText().toString().trim();
                    request.img=currentPhotoPath;
                    request.name = mEdShopName.getText().toString().trim();
                    request.latitude = Double.parseDouble(String.format("%.2f", latitude));
                    request.longitude = Double.parseDouble(String.format("%.2f", longitude));
                    request.description = mEdDesc.getText().toString().trim();
                    try {
                        request.minOrder = Double.parseDouble(mEdMinOrder.getText().toString().trim());
                        request.deliveryFee = Double.parseDouble(mEdDeliveryFee.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        showError("金额格式错误");
                    }
                    registerPresenter.register(request);
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
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

        RxView.clicks(mTvAddress)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.ADD_ADDRESS)
                            .navigation(this,REQUEST_CODE_ADDRESS);
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

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_ADDRESS && data != null) {

            double lat = data.getDoubleExtra("latitude", 0.0);
            double lng = data.getDoubleExtra("longitude", 0.0);
            String address = data.getStringExtra("address");
            String detailAddress = data.getStringExtra("detailAddress");

            this.latitude = lat;
            this.longitude = lng;

            mTvAddress.setText(address + detailAddress);
        }
    }

    @Override
    public void onRegisterSuccess(String data) {
        ToastUtils.show(this,"注册成功");
        finish();
    }

    @Override
    public void onRegisterFailed(String errorMsg) {
        showError("注册失败");
        LogUtils.error("注册失败"+errorMsg);
    }
}
