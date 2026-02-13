package com.leben.base.contract;

import android.app.Activity;

/**
 * 所有 View 层接口必须继承此接口
 * 这里可以定义通用的 UI 操作
 * Created by youjiahui on 2026/1/28.
 */

public interface IBaseView {

    //方便 Presenter 中使用 Context
    Activity getActivityContext();

    void showLoading(String msg);

    void hideLoading();

    void showError(String msg);

}
