package com.leben.merchant.util;

import android.content.Context;
import android.text.TextUtils;
import com.google.gson.Gson;
import com.leben.base.util.SharedPreferencesUtils;
import com.leben.common.Constant.CommonConstant;
import com.leben.merchant.model.bean.LoginEntity;

public class MerchantUtils {
    /**
     * 获取当前登录的用户信息
     * @param context 上下文
     * @return UserInfo 对象，如果没登录则返回 null
     */
    public static LoginEntity.ShopInfo getMerchantInfo(Context context) {
        // 1. 取出 JSON 字符串 (默认值为 "")
        String json = (String) SharedPreferencesUtils.getParam(context, CommonConstant.Key.MERCHANT_INFO, "");

        // 2. 判空 (防止没登录时崩溃)
        if (TextUtils.isEmpty(json)) {
            return null;
        }

        // 3. 解析 JSON
        try {
            return new Gson().fromJson(json, LoginEntity.ShopInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取当前用户ID (快捷方法)
     */
    public static Long getUserId(Context context) {
        LoginEntity.ShopInfo userInfo = getMerchantInfo(context);
        return userInfo != null ? userInfo.getId() : null;
    }
    /**
     * 【新增】更新用户信息到 SharedPreferences
     */
    public static void saveUserInfo(Context context, LoginEntity.ShopInfo userInfo) {
        if (userInfo == null) return;

        // 1. 转成 JSON 字符串
        String json = new Gson().toJson(userInfo);

        // 2. 存入 SP
        SharedPreferencesUtils.setParam(context, CommonConstant.Key.USER_INFO, json);
    }

}
