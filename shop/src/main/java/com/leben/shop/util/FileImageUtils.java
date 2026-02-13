package com.leben.shop.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class FileImageUtils {

    // 定义回调接口
    public interface OnSaveResultCallback {
        void onSuccess(String savedPath);
        void onFailed(String errorMsg);
    }

    /**
     * 将 Uri 保存为文件
     */
    public static void saveImageUriToFile(Context context, Uri uri, OnSaveResultCallback callback) {
        // 生成目标保存路径
        String finalPath = getImageFilePath(context);
        File file = new File(finalPath);

        // 使用 Glide 获取 Bitmap
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        // 开启子线程进行 IO 操作，避免阻塞主线程
                        new Thread(() -> {
                            try {
                                FileOutputStream fos = new FileOutputStream(file);
                                // 压缩并保存，PNG 是无损的，如果想要文件小一点可以用 JPEG
                                resource.compress(Bitmap.CompressFormat.PNG, 100, fos);
                                fos.flush();
                                fos.close();

                                // 切换回主线程通知成功
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (callback != null) {
                                        callback.onSuccess(file.getAbsolutePath());
                                    }
                                });

                            } catch (IOException e) {
                                e.printStackTrace();
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    if (callback != null) callback.onFailed("保存文件失败: " + e.getMessage());
                                });
                            }
                        }).start();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // 可以在这里处理取消加载的逻辑
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (callback != null) callback.onFailed("图片加载失败");
                    }
                });
    }

    /**
     * 获取应用私有图片目录的路径 (无需权限)
     * 路径示例: /storage/emulated/0/Android/data/你的包名/files/Pictures/xxxx-xxxx.png
     */
    public static String getImageFilePath(Context context) {
        File directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (directory != null && !directory.exists()) {
            directory.mkdirs();
        }
        String fileName = UUID.randomUUID().toString().replace("-", "") + ".png";
        return new File(directory, fileName).getAbsolutePath();
    }
}