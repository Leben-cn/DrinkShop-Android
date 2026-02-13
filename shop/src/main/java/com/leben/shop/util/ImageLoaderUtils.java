package com.leben.shop.util;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import java.io.File;

public class ImageLoaderUtils {
    public static void loadImage(Context context, ImageView imgView, String imgResource) {
        if (TextUtils.isEmpty(imgResource)) return;

        Object loadModel = null;

        // 判断逻辑：如果以 "/" 开头，视为文件路径；否则尝试作为资源名称处理
        if (imgResource.startsWith("/")) {
            // 是本地文件路径
            loadModel = new File(imgResource);
        } else if (imgResource.startsWith("http")) {
            // 是网络图片
            loadModel = imgResource;
        } else {
            // 假设是资源名称 (例如 "ic_logo")，尝试查找资源 ID
            int resId = context.getResources().getIdentifier(imgResource, "drawable", context.getPackageName());
            if (resId != 0) {
                loadModel = resId;
            } else {
                // 如果找不到资源ID，可能这是一个没有 "/" 开头的相对路径，或者就是无效字符
                // 此时也可以直接传字符串给 Glide 试试
                loadModel = imgResource;
            }
        }

        // 统一使用 Glide 加载
        Glide.with(context)
                .load(loadModel)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 缓存策略
                // .placeholder(R.drawable.loading) // 可选：设置加载中占位图
                // .error(R.drawable.error)         // 可选：设置加载失败占位图
                .into(imgView);
    }
}
