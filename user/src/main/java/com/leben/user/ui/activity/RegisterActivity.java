package com.leben.user.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.util.ImagePickerHelper;
import com.leben.common.util.PermissionDialogHelper;
import com.leben.user.R;
import com.leben.user.constant.UserConstant;
import com.leben.user.contract.RegisterContract;
import com.leben.user.model.bean.RegisterEntity;
import com.leben.user.presenter.RegisterPresenter;
import com.tbruyelle.rxpermissions2.RxPermissions;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = UserConstant.Router.USER_REGISTER)
public class RegisterActivity extends BaseActivity implements RegisterContract.View {

    private EditText mEdAccount, mEdPassword, mEdPhone, mEdNickName;
    private TextView mTvGender;
    private Button mBtnRegister;
    private ImageView ivAddBtn, ivDelPhoto, ivSelectedPhoto;

    private ImagePickerHelper imagePickerHelper;
    private String currentPhotoPath;
    private String selectedGender = "未知"; // 默认性别

    @InjectPresenter
    RegisterPresenter registerPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.user_ac_register;
    }

    @Override
    public void initView() {
        TitleBar titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("用户注册");

        mEdAccount = findViewById(R.id.et_account);
        mEdPassword = findViewById(R.id.et_password);
        mEdPhone = findViewById(R.id.et_phone);
        mEdNickName = findViewById(R.id.et_nick_name);
        mTvGender = findViewById(R.id.tv_gender_selector);
        mBtnRegister = findViewById(R.id.btn_register);

        ivAddBtn = findViewById(R.id.iv_add_btn);
        ivDelPhoto = findViewById(R.id.iv_del_photo);
        ivSelectedPhoto = findViewById(R.id.iv_selected_photo);

        imagePickerHelper = new ImagePickerHelper(this, path -> {
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
        // 选择性别
        RxView.clicks(mTvGender).subscribe(unit -> {
            String[] items = {"男", "女", "保密"};
            new AlertDialog.Builder(this)
                    .setItems(items, (dialog, which) -> {
                        selectedGender = items[which];
                        mTvGender.setText(selectedGender);
                    }).show();
        });

        // 注册提交
        RxView.clicks(mBtnRegister)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(unit -> {
                    if (TextUtils.isEmpty(mEdAccount.getText().toString().trim())) {
                        showError("请输入账号");
                        return false;
                    }
                    if (TextUtils.isEmpty(mEdPassword.getText().toString().trim())) {
                        showError("请输入密码");
                        return false;
                    }
                    if (TextUtils.isEmpty(mEdNickName.getText().toString().trim())) {
                        showError("请输入昵称");
                        return false;
                    }
                    return true;
                })
                .subscribe(result -> {
                    RegisterEntity entity = new RegisterEntity();
                    entity.account = mEdAccount.getText().toString().trim();
                    entity.password = mEdPassword.getText().toString().trim();
                    entity.phone = mEdPhone.getText().toString().trim();
                    entity.nickName = mEdNickName.getText().toString().trim();
                    entity.gender = selectedGender;
                    entity.img = currentPhotoPath;

                    registerPresenter.register(entity);
                }, throwable -> LogUtils.error("用户注册点击异常: " + throwable.getMessage()));

        // 图片上传逻辑
        RxView.clicks(ivAddBtn).subscribe(unit -> {
            new RxPermissions(this).request(Manifest.permission.CAMERA)
                    .subscribe(granted -> {
                        if (granted) imagePickerHelper.openGallery();
                        else PermissionDialogHelper.showCameraPermissionDialog(this);
                    });
        });

        RxView.clicks(ivDelPhoto).subscribe(unit -> {
            currentPhotoPath = null;
            ivSelectedPhoto.setVisibility(View.GONE);
            ivDelPhoto.setVisibility(View.GONE);
            ivAddBtn.setVisibility(View.VISIBLE);
        });
    }

    @Override public void onRegisterSuccess(String data) {
        ToastUtils.show(this, "注册成功");
        finish();
    }

    @Override public void onRegisterFailed(String errorMsg) {
        showError("注册失败: " + errorMsg);
    }

    @Override public void initData() {}
    @Override protected View getTitleBarView() { return findViewById(R.id.title_bar); }
    @Override protected int getStatusBarColor() { return com.leben.base.R.color.white; }
}