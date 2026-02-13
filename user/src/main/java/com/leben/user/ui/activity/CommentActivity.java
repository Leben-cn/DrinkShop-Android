package com.leben.user.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.ConvertUtils;
import com.leben.base.util.FileUtil;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.OrderEntity;
import com.leben.common.model.bean.OrderItemEntity;
import com.leben.common.util.ImagePickerHelper;
import com.leben.common.util.PermissionDialogHelper;
import com.leben.user.R;
import com.leben.user.constant.UserConstant;
import com.leben.user.contract.SubmitCommentContract;
import com.leben.user.model.bean.CommentSubmitEntity;
import com.leben.user.presenter.SubmitCommentPresenter;
import com.leben.user.ui.adapter.CommentItemAdapter;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = UserConstant.Router.COMMENT)
public class CommentActivity extends BaseRecyclerActivity<OrderItemEntity> implements SubmitCommentContract.View {

    private TitleBar titleBar;
    private OrderEntity order;
    private ImageView ivAddBtn;
    private ImageView ivDelPhoto;
    private ImageView ivSelectedPhoto;
    private Button btnSubmit;
    private EditText etContent;

    private ImagePickerHelper imagePickerHelper;
    private String currentPhotoPath; // 当前选中的图片本地路径

    @InjectPresenter
    SubmitCommentPresenter submitCommentPresenter;

    @Override
    protected BaseRecyclerAdapter<OrderItemEntity> createAdapter() {
        return new CommentItemAdapter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ac_comment;
    }

    @Override
    public void onInit() {
        super.onInit();
        order = (OrderEntity) getIntent().getSerializableExtra("order");

    }

    @Override
    public void initView() {
        super.initView();
        titleBar=findViewById(R.id.title_bar);
        ivAddBtn=findViewById(R.id.iv_add_btn);
        btnSubmit=findViewById(R.id.btn_submit);
        ivDelPhoto=findViewById(R.id.iv_del_photo);
        etContent = findViewById(R.id.et_content);
        ivSelectedPhoto=findViewById(R.id.iv_selected_photo);

        if (titleBar != null) {
            titleBar.setTitle("");
        }

        imagePickerHelper=new ImagePickerHelper(this,path -> {
           this.currentPhotoPath=path;
           displayImage(path);
        });

        refreshListSuccess(order.getItems());
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

        RxView.clicks(btnSubmit)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                    String content = etContent.getText().toString().trim();
                    CommentItemAdapter adapter = (CommentItemAdapter) mAdapter;
                    Map<Long, Integer> ratings = adapter.getRatingResults();

                    CommentSubmitEntity entity = new CommentSubmitEntity();
                    entity.setOrderId(order.getId());
                    entity.setContent(content);

                    // 【关键】直接把本地路径存进去！
                    // 后端数据库里存的就是 "/storage/emulated/0/.../xxx.jpg"
                    entity.setPicture(currentPhotoPath);
                    List<CommentSubmitEntity.ProductRating> items = new ArrayList<>();
                    if (ratings != null) {
                        for (Map.Entry<Long, Integer> entry : ratings.entrySet()) {
                            CommentSubmitEntity.ProductRating item = new CommentSubmitEntity.ProductRating();
                            item.setProductId(entry.getKey()); // Key 是商品ID
                            item.setRating(entry.getValue());  // Value 是分数
                            items.add(item);
                        }
                    }
                    entity.setItems(items);
                    submitCommentPresenter.submitComment(entity);

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(ivDelPhoto)
                .subscribe(unit -> {
                    currentPhotoPath = null;
                    ivSelectedPhoto.setVisibility(View.GONE);
                    ivDelPhoto.setVisibility(View.GONE);
                    ivAddBtn.setVisibility(View.VISIBLE); // 恢复加号按钮
                });
    }

    @Override
    public void initData() {

    }

    @Override
    public void onRefresh() {
        refreshListSuccess(order.getItems());
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }

    @Override
    public void onSubmitCommentSuccess(String data) {
        ToastUtils.show(this,"评论成功");
        finish();
    }

    @Override
    public void onSubmitCommentFailed(String errorMsg) {
        ToastUtils.show(this,"评论失败");
        LogUtils.error("评论失败："+errorMsg);
    }

    // 3. 统一显示图片的方法
    private void displayImage(String path) {
        this.currentPhotoPath = path; // 更新当前路径

        ivSelectedPhoto.setVisibility(View.VISIBLE);
        ivDelPhoto.setVisibility(View.VISIBLE);
        ivAddBtn.setVisibility(View.GONE); // 隐藏加号按钮

        Glide.with(this).load(path).into(ivSelectedPhoto);
    }


}
