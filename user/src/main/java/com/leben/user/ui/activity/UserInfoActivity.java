package com.leben.user.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.solver.state.State;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.LoginEntity;
import com.leben.common.util.ImagePickerHelper;
import com.leben.common.util.PermissionDialogHelper;
import com.leben.common.util.UserUtils;
import com.leben.user.R;
import com.leben.user.constant.UserConstant;
import com.leben.user.contract.SubmitUserInfoContract;
import com.leben.user.model.bean.UserInfoEntity;
import com.leben.user.presenter.SubmitUserInfoPresenter;
import com.leben.user.widget.EditInfoDialog;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = UserConstant.Router.USER_INFO)
public class UserInfoActivity extends BaseActivity implements SubmitUserInfoContract.View {

    private ImageView ivAvatar;
    private TextView tvName;
    private TextView tvPhone;
    private ConstraintLayout clAvatar;
    private ConstraintLayout clUserName;
    private ConstraintLayout clPassword;
    private ConstraintLayout clPhone;
    private ConstraintLayout clLogoutAccount;

    private int currentEditType; // 0:头像, 1:昵称, 2:密码, 3:手机号
    private String pendingContent;

    private ImagePickerHelper imagePickerHelper;

    @InjectPresenter
    SubmitUserInfoPresenter submitUserInfoPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_user_info;
    }

    @Override
    public void initView() {
        TitleBar titleBar=findViewById(R.id.title_bar);
        ivAvatar=findViewById(R.id.iv_user_avatar);
        tvName=findViewById(R.id.tv_username);
        tvPhone=findViewById(R.id.tv_phone);
        clAvatar=findViewById(R.id.cl_avatar);
        clUserName=findViewById(R.id.cl_username);
        clPassword=findViewById(R.id.cl_password);
        clPhone=findViewById(R.id.cl_phone);
        clLogoutAccount=findViewById(R.id.cl_logout_account);

        if (titleBar != null) {
            titleBar.setTitle("我的账号");
        }

        LoginEntity.UserInfo myInfo = UserUtils.getUserInfo(this);
        if (myInfo != null) {
            tvName.setText(myInfo.getNickname());
            tvPhone.setText(myInfo.getPhone());
            Glide.with(this)
                    .load(myInfo.getAvatar())
                    .error(R.drawable.pic_default_avatar)
                    .circleCrop()
                    .into(ivAvatar);
        }

        imagePickerHelper=new ImagePickerHelper(this,path -> {

            Glide.with(this).load(path).into(ivAvatar);
            this.currentEditType = 0;
            this.pendingContent = path; // 把路径暂存，为了 success 回调里更新 SP

            // 4. 构建实体并提交
            UserInfoEntity entity = new UserInfoEntity();
            entity.setImg(path); // 设置图片路径
            submitUserInfoPresenter.submitUserInfo(entity);
        });

    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(clAvatar)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

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

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(clUserName)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    LoginEntity.UserInfo myInfo = UserUtils.getUserInfo(this);
                    if (myInfo != null) {
                        EditInfoDialog dialog=EditInfoDialog.newInstance("修改昵称",myInfo.getNickname());
                        dialog.setHintText("支持中文、英文、数字");
                        dialog.setOnConfirmListener(newContent->{
                            UserInfoEntity entity=new UserInfoEntity();
                            this.pendingContent = newContent;
                            this.currentEditType=1;
                            entity.setNickName(newContent);
                            submitUserInfoPresenter.submitUserInfo(entity);
                        });
                        dialog.show(getSupportFragmentManager(), "EditNickName");
                    }

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });


        RxView.clicks(clPassword)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    LoginEntity.UserInfo myInfo = UserUtils.getUserInfo(this);
                    if (myInfo != null) {
                        EditInfoDialog dialog=EditInfoDialog.newInstance("修改密码",myInfo.getPassword());
                        dialog.setHintText("支持英文、数字、特殊符号");
                        dialog.setOnConfirmListener(newContent->{
                            UserInfoEntity entity=new UserInfoEntity();
                            this.pendingContent = newContent;
                            this.currentEditType=2;
                            entity.setPassword(newContent);
                            submitUserInfoPresenter.submitUserInfo(entity);
                        });
                        dialog.show(getSupportFragmentManager(), "EditPassword");
                    }

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(clPhone)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    LoginEntity.UserInfo myInfo = UserUtils.getUserInfo(this);
                    if (myInfo != null) {
                        EditInfoDialog dialog=EditInfoDialog.newInstance("修改手机号",myInfo.getPhone());
                        dialog.setHintText("仅支持数字");
                        dialog.setOnConfirmListener(newContent->{
                            UserInfoEntity entity=new UserInfoEntity();
                            this.pendingContent = newContent;
                            this.currentEditType=3;
                            entity.setPhone(newContent);
                            submitUserInfoPresenter.submitUserInfo(entity);
                        });
                        dialog.show(getSupportFragmentManager(), "EditPhone");
                    }

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(clLogoutAccount)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{



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
    public void onSubmitUserInfoSuccess(String data) {

        LoginEntity.UserInfo userInfo = UserUtils.getUserInfo(this);

        if (userInfo != null && pendingContent != null) {

            if (currentEditType == 0) {
                userInfo.setAvatar(pendingContent);
            }else if(currentEditType==1){
                userInfo.setNickname(pendingContent);
                tvName.setText(pendingContent);
            }else if(currentEditType==2){
                userInfo.setPassword(pendingContent);
            }else if(currentEditType==3){
                userInfo.setPhone(pendingContent);
                tvPhone.setText(pendingContent);
            }

            UserUtils.saveUserInfo(this, userInfo);
        }
        ToastUtils.show(this,"修改成功");
    }

    @Override
    public void onSubmitUserInfoFailed(String errorMsg) {
        ToastUtils.show(this,"修改信息失败");
        LogUtils.error("修改信息失败："+errorMsg);
    }

}
