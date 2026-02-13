package com.leben.base.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewStub;
import com.leben.base.Lifecycle;
import java.util.List;

/**
 * 状态控制器：负责切换 内容/空数据/错误页
 * Created by youjiahui on 2026/1/28.
 */

public class StateController implements Lifecycle {

    private Activity mActivity;
    private Context mContext;
    private View mContentView;//正常显示的内容
    private View mEmptyView;//空布局
    private ViewStub mEmptyViewStub;//懒加载空布局
    private View mErrorView;//出错布局
    private ViewStub mErrorViewStub;//懒加载出错布局

    public StateController(Activity activity, View contentView) {
        this.mActivity = activity;
        this.mContentView = contentView;
    }

    public StateController(View rootView, View contentView) {
        this.mContext = rootView.getContext();
        this.mContentView = contentView;
    }

    /**
     * 设置空布局的 ViewStub
     */
    public void setEmptyViewStub(ViewStub viewStub) {
        this.mEmptyViewStub = viewStub;
    }

    /**
     * 设置错误布局的 ViewStub
     */
    public void setErrorViewStub(ViewStub viewStub) {
        this.mErrorViewStub = viewStub;
    }

    public void handleData(List<?> data){
        if(data==null||data.isEmpty()){
            showEmpty();
        }else{
            showContent();
        }
    }

    public void showContent(){
        if(mContentView!=null){
            mContentView.setVisibility(View.VISIBLE);
        }
        if(mEmptyView!=null){
            mEmptyView.setVisibility(View.GONE);
        }
    }

    public void showEmpty(){
        if(mEmptyView==null){
            if(mEmptyViewStub!=null){
                mEmptyView=mEmptyViewStub.inflate();
            }else{
                return;
            }
        }
        mEmptyView.setVisibility(View.VISIBLE);
    }

    public void showError(){
        if(mErrorView==null){
            if(mErrorViewStub!=null){
                mErrorView=mErrorViewStub.inflate();
            }else{
                return;
            }
        }
        mErrorView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onInit() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {
        mActivity = null;
        mContentView = null;
        mEmptyView = null;
    }

    @Override
    public void onRetry() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
