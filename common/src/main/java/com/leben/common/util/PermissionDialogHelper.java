package com.leben.common.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import com.leben.base.util.ToastUtils; // 假设你有这个工具类

public class PermissionDialogHelper {
    /**
     * 显示相机权限被拒的弹窗 (专用)
     */
    public static void showCameraPermissionDialog(Context context) {
        showSettingsDialog(context, "拍照功能需要您授予相机权限，否则无法使用。\n请前往设置页面手动开启。");
    }

    /**
     * 显示存储权限被拒的弹窗 (专用，留作备用)
     */
    public static void showStoragePermissionDialog(Context context) {
        showSettingsDialog(context, "保存图片需要存储权限，否则无法使用。\n请前往设置页面手动开启。");
    }

    /**
     * 【核心通用方法】显示去设置的弹窗
     * @param context 上下文
     * @param message 提示内容
     */
    public static void showSettingsDialog(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("权限申请")
                .setMessage(message)
                .setCancelable(false) // 建议禁止点击外部取消，强制用户选择
                .setPositiveButton("去设置", (dialog, which) -> {
                    gotoAppDetailSetting(context);
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    ToastUtils.show(context, "您取消了授权，功能无法使用");
                })
                .show();
    }

    /**
     * 跳转到当前应用的详情设置页面 (通用逻辑)
     */
    private static void gotoAppDetailSetting(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(context, "无法自动打开设置页面，请手动前往设置");
        }
    }
}
