package com.leben.base.contract;

/**
 * 所有 Presenter 的基类接口
 * Created by youjiahui on 2026/1/28.
 */

public interface IBasePresenter<V extends IBaseView> {
    
    //绑定 View
    void attachView(V view);

    //解绑 View
    void detachView();

    //获取 View 引用，通常由实现类处理
    V getView();

}
