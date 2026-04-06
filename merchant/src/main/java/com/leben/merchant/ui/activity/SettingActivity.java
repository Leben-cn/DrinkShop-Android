package com.leben.merchant.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.EditInfoDialog;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.util.ImagePickerHelper;
import com.leben.common.util.PermissionDialogHelper;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.contract.SubmitShopInfoContract;
import com.leben.merchant.model.bean.LoginEntity;
import com.leben.merchant.model.bean.MerchantInfoEntity;
import com.leben.merchant.model.event.RefreshInfoEvent;
import com.leben.merchant.presenter.SubmitShopInfoPresenter;
import com.leben.merchant.util.MerchantUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
import org.greenrobot.eventbus.EventBus;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by youjiahui on 2026/4/6.
 */

@Route(path = MerchantConstant.Router.SETTING)
public class SettingActivity extends BaseActivity implements SubmitShopInfoContract.View {

    private ImageView ivShopImg;
    private TextView tvShopName;
    private TextView tvShopAccount;
    private TextView tvPhone;
    private TextView tvDeliveryFee;
    private TextView tvMinOrder;
    private ConstraintLayout clShopImg;
    private ConstraintLayout clShopName;
    private ConstraintLayout clShopAccount;
    private ConstraintLayout clPassword;
    private ConstraintLayout clPhone;
    private ConstraintLayout clDeliveryFee;
    private ConstraintLayout clMinOrder;
    private ConstraintLayout clDescription;

    private int currentEditType; // 0:店铺头像, 1:店铺名, 2:账号, 3:密码, 4:手机号, 5:运费, 6:起送价, 7:店铺描述
    private String pendingContent; //为了 success 回调里更新 SP

    private ImagePickerHelper imagePickerHelper;

    @InjectPresenter
    SubmitShopInfoPresenter submitShopInfoPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_ac_info;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView() {
        TitleBar titleBar = findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setTitle("店铺设置");
        }

        ivShopImg = findViewById(R.id.iv_shop_avatar);
        tvShopName = findViewById(R.id.tv_shopName);
        tvShopAccount = findViewById(R.id.tv_account);
        tvPhone = findViewById(R.id.tv_phone);
        tvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        tvMinOrder = findViewById(R.id.tv_MinOrder);

        clShopImg = findViewById(R.id.cl_avatar);
        clShopName = findViewById(R.id.cl_shopName);
        clShopAccount = findViewById(R.id.cl_account);
        clPassword = findViewById(R.id.cl_password);
        clPhone = findViewById(R.id.cl_phone);
        clDeliveryFee = findViewById(R.id.cl_delivery_fee);
        clMinOrder = findViewById(R.id.cl_MinOrder);
        clDescription = findViewById(R.id.cl_description);

        LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(this);
        if (merchantInfo != null) {
            tvShopName.setText(merchantInfo.getShopName());
            tvShopName.setText(merchantInfo.getShopName());
            tvShopAccount.setText(merchantInfo.getAccount());
            tvPhone.setText(merchantInfo.getPhone());
            DecimalFormat df = new DecimalFormat("#.##");
            tvDeliveryFee.setText(df.format(merchantInfo.getDeliveryFee()) + "￥");
            tvMinOrder.setText(df.format(merchantInfo.getMinOrder()) + "￥");
            Glide.with(this)
                    .load(merchantInfo.getImg())
                    .circleCrop()
                    .into(ivShopImg);
        }

        imagePickerHelper=new ImagePickerHelper(this,path -> {

            Glide.with(this).load(path).into(ivShopImg);
            this.currentEditType = 0;
            this.pendingContent = path; // 把路径暂存，为了 success 回调里更新 SP

            // 4. 构建实体并提交
            MerchantInfoEntity entity = new MerchantInfoEntity();
            entity.setImg(path); // 设置图片路径
            submitShopInfoPresenter.submitShopInfo(entity);
        });

    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(clShopImg)
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

        RxView.clicks(clShopName)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(this);
                    if (merchantInfo != null) {
                        EditInfoDialog dialog = EditInfoDialog.newInstance("修改店铺名", merchantInfo.getShopName());
                        dialog.setHintText("支持中文、英文");
                        dialog.setOnConfirmListener(newContent -> {
                            MerchantInfoEntity entity = new MerchantInfoEntity();
                            this.pendingContent = newContent;
                            this.currentEditType = 1;
                            entity.setName(newContent);
                            submitShopInfoPresenter.submitShopInfo(entity);
                        });
                        dialog.show(getSupportFragmentManager(), "dialog_editShopName");
                    }

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(clShopAccount)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(this);
                    if (merchantInfo != null) {
                        EditInfoDialog dialog = EditInfoDialog.newInstance("修改账号", merchantInfo.getAccount());
                        dialog.setHintText("支持数字、英文");
                        dialog.setOnConfirmListener( newContent -> {
                            MerchantInfoEntity entity = new MerchantInfoEntity();
                            this.pendingContent = newContent;
                            this.currentEditType = 2;
                            entity.setAccount(newContent);
                            submitShopInfoPresenter.submitShopInfo(entity);
                        });
                        dialog.show(getSupportFragmentManager(), "dialog_editAccount");
                    }

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(clPassword)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(this);
                    if (merchantInfo != null) {
                        EditInfoDialog dialog = EditInfoDialog.newInstance("修改密码", merchantInfo.getPassword());
                        dialog.setHintText("支持英文、数字、特殊符号");
                        dialog.setOnConfirmListener(newContent -> {
                            MerchantInfoEntity entity = new MerchantInfoEntity();
                            this.pendingContent = newContent;
                            this.currentEditType = 3;
                            entity.setPassword(newContent);
                            submitShopInfoPresenter.submitShopInfo(entity);
                        });
                        dialog.show(getSupportFragmentManager(), "dialog_editPassword");
                    }

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(clPhone)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(this);
                    if (merchantInfo != null) {
                        EditInfoDialog dialog = EditInfoDialog.newInstance("修改联系电话", merchantInfo.getPhone());
                        dialog.setHintText("仅支持中国大陆手机号");
                        dialog.setOnConfirmListener(newContent -> {
                            MerchantInfoEntity entity = new MerchantInfoEntity();
                            this.pendingContent = newContent;
                            this.currentEditType = 4;
                            entity.setPhone(newContent);
                            submitShopInfoPresenter.submitShopInfo(entity);
                        });
                        dialog.show(getSupportFragmentManager(), "dialog_editPhone");
                    }

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(clDeliveryFee)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(this);
                    if (merchantInfo != null) {
                        EditInfoDialog dialog = EditInfoDialog.newInstance("修改运费", String.valueOf(merchantInfo.getDeliveryFee()));
                        dialog.setHintText("最多保留两位小数");
                        dialog.setOnConfirmListener(newContent -> {
                            if (TextUtils.isEmpty(newContent)) {
                                showError("请输入运费");
                                return;
                            }
                            MerchantInfoEntity entity = new MerchantInfoEntity();
                            this.pendingContent = newContent;
                            this.currentEditType = 5;
                            entity.setDeliveryFee(new BigDecimal(newContent));
                            submitShopInfoPresenter.submitShopInfo(entity);
                        });
                        dialog.show(getSupportFragmentManager(), "dialog_editDeliveryFee");
                    }

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(clMinOrder)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(this);
                    if (merchantInfo != null) {
                        EditInfoDialog dialog = EditInfoDialog.newInstance("修改起送价", String.valueOf(merchantInfo.getMinOrder()));
                        dialog.setHintText("最多保留两位小数");
                        dialog.setOnConfirmListener(newContent -> {
                            MerchantInfoEntity entity = new MerchantInfoEntity();
                            this.pendingContent = newContent;
                            this.currentEditType = 6;
                            entity.setMinOrder(new BigDecimal(newContent));
                            submitShopInfoPresenter.submitShopInfo(entity);
                        });
                        dialog.show(getSupportFragmentManager(), "dialog_editMinOrder");
                    }

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(clDescription)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(this);
                    if (merchantInfo != null) {
                        EditInfoDialog dialog = EditInfoDialog.newInstance("修改店铺简介", merchantInfo.getDescription());
                        dialog.setHintText("50字");
                        dialog.setOnConfirmListener(newContent -> {
                            MerchantInfoEntity entity = new MerchantInfoEntity();
                            this.pendingContent = newContent;
                            this.currentEditType = 7;
                            entity.setDescription(newContent);
                            submitShopInfoPresenter.submitShopInfo(entity);
                        });
                        dialog.show(getSupportFragmentManager(), "dialog_editDescription");
                    }

                }, throwable -> {
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onSubmitShopInfoSuccess(String data) {
        LoginEntity.ShopInfo merchantInfo = MerchantUtils.getMerchantInfo(this);

        if (merchantInfo != null && pendingContent != null) {
            DecimalFormat df = new DecimalFormat("#.##");

            switch (currentEditType) {
                case 0: // 店铺头像
                    merchantInfo.setImg(pendingContent);
                    Glide.with(this)
                            .load(pendingContent)
                            .circleCrop()
                            .into(ivShopImg);
                    break;

                case 1: // 店铺名
                    merchantInfo.setShopName(pendingContent);
                    tvShopName.setText(pendingContent);
                    break;

                case 2: // 账号
                    merchantInfo.setAccount(pendingContent);
                    tvShopAccount.setText(pendingContent);
                    break;

                case 3: // 密码
                    merchantInfo.setPassword(pendingContent);
                    break;

                case 4: // 手机号
                    merchantInfo.setPhone(pendingContent);
                    tvPhone.setText(pendingContent);
                    break;

                case 5: // 运费
                    BigDecimal deliveryFee = new BigDecimal(pendingContent);
                    merchantInfo.setDeliveryFee(deliveryFee);
                    tvDeliveryFee.setText(df.format(deliveryFee) + "￥");
                    break;

                case 6: // 起送价
                    BigDecimal minOrder = new BigDecimal(pendingContent);
                    merchantInfo.setMinOrder(minOrder);
                    tvMinOrder.setText(df.format(minOrder) + "￥");
                    break;

                case 7: // 店铺描述
                    merchantInfo.setDescription(pendingContent);
                    break;
            }

            // 执行本地持久化保存 (SP)
            MerchantUtils.saveMerchantInfo(this, merchantInfo);
        }

        ToastUtils.show(this, "修改成功");
        EventBus.getDefault().post(new RefreshInfoEvent());
        // 重置临时变量
        this.pendingContent = null;
    }

    @Override
    public void onSubmitShopInfoFailed(String errorMsg) {
        ToastUtils.show(this, "修改信息失败");
        LogUtils.error("修改信息失败：" + errorMsg);
    }
}
