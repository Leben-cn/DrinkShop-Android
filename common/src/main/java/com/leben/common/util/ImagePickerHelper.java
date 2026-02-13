package com.leben.common.util;

import android.net.Uri;
import android.os.Build;
import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import com.leben.base.util.FileUtil;
import com.leben.base.util.ToastUtils;
import java.io.File;

public class ImagePickerHelper {

    private final ComponentActivity mActivity;
    private final OnImageSelectedListener mListener;

    // 启动器
    private final ActivityResultLauncher<String> mGalleryLauncher;
    private final ActivityResultLauncher<Uri> mCameraLauncher;

    // 拍照临时变量
    private String mCurrentPhotoPath;
    private Uri mCameraUri;

    public interface OnImageSelectedListener {
        void onImageReady(String path);
    }

    /**
     * 构造函数 (必须在 Activity 的 onCreate 或 onInit 中调用)
     */
    public ImagePickerHelper(ComponentActivity activity, OnImageSelectedListener listener) {
        this.mActivity = activity;
        this.mListener = listener;

        // 1. 注册相册选择
        mGalleryLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // 复制到私有目录 (复用你原本的逻辑)
                        String savedPath = FileUtil.copyImageToAppDir(activity, uri);
                        if (savedPath != null && mListener != null) {
                            mListener.onImageReady(savedPath);
                        } else {
                            ToastUtils.show(activity, "图片处理失败");
                        }
                    }
                }
        );

        // 2. 注册拍照
        mCameraLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && mCurrentPhotoPath != null && mListener != null) {
                        mListener.onImageReady(mCurrentPhotoPath);
                    }
                }
        );
    }

    /**
     * 打开相册
     */
    public void openGallery() {
        mGalleryLauncher.launch("image/*");
    }

    /**
     * 打开相机
     */
    public void openCamera() {
        // 1. 创建临时文件
        File photoFile = new File(mActivity.getExternalCacheDir(), "temp_" + System.currentTimeMillis() + ".jpg");
        mCurrentPhotoPath = photoFile.getAbsolutePath();

        // 2. 获取 URI (适配 Android 7.0+)
        // 注意：这里的 authority 必须和 AndroidManifest.xml 里的保持一致
        String authority = mActivity.getPackageName() + ".fileprovider";

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mCameraUri = FileProvider.getUriForFile(mActivity, authority, photoFile);
            } else {
                mCameraUri = Uri.fromFile(photoFile);
            }
            // 3. 启动相机
            mCameraLauncher.launch(mCameraUri);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(mActivity, "相机启动失败: " + e.getMessage());
        }
    }


}
