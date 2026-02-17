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
    public static Long getMerchantId(Context context) {
        LoginEntity.ShopInfo shopInfo = getMerchantInfo(context);
        return shopInfo != null ? shopInfo.getId() : null;
    }
    /**
     * 更新用户信息到 SharedPreferences
     */
    public static void saveMerchantInfo(Context context, LoginEntity.ShopInfo shopInfo) {
        if (shopInfo == null) {
            return;
        }

        // 1. 转成 JSON 字符串
        String json = new Gson().toJson(shopInfo);

        // 2. 存入 SP
        SharedPreferencesUtils.setParam(context, CommonConstant.Key.MERCHANT_INFO, json);
    }

}
