package com.leben.base.widget.titleBar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.leben.base.R;

import java.util.ArrayList;
import java.util.List;

public class TitleBar extends ConstraintLayout {

    private ImageView mIvBack;
    private LinearLayout mRightContainer;
    private TextSwitcher mTextSwitcher;
    private TextView mTvSearchBtn;
    private ConstraintLayout mSearchGroup;
    private TextView mTvCenterTitle;
    private List<String> mHintList = new ArrayList<>();
    private int mCurrentIndex = 0;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;
    private boolean isRolling = false;

    public TitleBar(Context context) { this(context, null); }

    public TitleBar(Context context, AttributeSet attrs) { this(context, attrs, 0); }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_title_bar, this, true);

        mIvBack = findViewById(R.id.iv_back);
        mRightContainer = findViewById(R.id.ll_right_custom_container);
        mTextSwitcher = findViewById(R.id.ts_hint_text);
        mTvSearchBtn = findViewById(R.id.tv_search_btn);
        mSearchGroup = findViewById(R.id.cl_search_group);
        mTvCenterTitle = findViewById(R.id.tv_center_title);

        // 设置 TextSwitcher 工厂
        mTextSwitcher.setFactory(() -> {
            TextView textView = new TextView(getContext());
            textView.setTextSize(13);
            textView.setTextColor(Color.parseColor("#999999"));
            textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            textView.setSingleLine(true);
            // 【关键点 2】设置 TextView 填满 TextSwitcher 的高度
            // 因为 TextSwitcher 本质是 FrameLayout，默认子 View 是 wrap_content 且靠顶部的
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            );
            // 这里也可以加一个 gravity，双重保险
            params.gravity = Gravity.CENTER_VERTICAL;

            textView.setLayoutParams(params);
            return textView;
        });

        mTextSwitcher.setInAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_in));
        mTextSwitcher.setOutAnimation(AnimationUtils.loadAnimation(context, android.R.anim.fade_out));

        mIvBack.setOnClickListener(v -> {
            if (context instanceof Activity) ((Activity) context).finish();
        });
    }

    /**
     * 【关键方法】由子类或外部调用，注入搜索关键词数据
     */
    public void setSearchHints(List<String> hints) {
        if (hints == null || hints.isEmpty()) return;

        this.mHintList = hints;
        this.mCurrentIndex = 0;

        // 立即显示第一个，避免空白
        mTextSwitcher.setCurrentText(mHintList.get(0));
    }

    /**
     * 设置右侧 View
     * 修改后的版本：优先使用 View 自身的 LayoutParams
     */
    public void addRightView(View view) {
        if (mRightContainer != null) {
            // 1. 尝试获取 View 现有的 LayoutParams
            ViewGroup.LayoutParams existingParams = view.getLayoutParams();
            LinearLayout.LayoutParams targetParams;

            if (existingParams instanceof LinearLayout.LayoutParams) {
                // 如果外部已经设置了 LinearLayout.LayoutParams，就直接使用它
                targetParams = (LinearLayout.LayoutParams) existingParams;
            } else {
                // 如果外部没有设置，或者设置的类型不对，则使用默认的 WRAP_CONTENT
                targetParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
            }

            // 确保设置了左边距 (如果外部没设置过)
            if (targetParams.leftMargin == 0) {
                targetParams.leftMargin = dp2px(10);
            }
            // 确保垂直居中 (通常标题栏按钮都需要垂直居中)
            targetParams.gravity = Gravity.CENTER_VERTICAL;

            mRightContainer.addView(view, targetParams);
        }
    }

    /**
     * 设置返回键显隐
     */
    public void setBackVisible(boolean visible) {
        mIvBack.setVisibility(visible ? VISIBLE : GONE);
    }

    public void startRoll() {
        // 如果没有数据，就不轮播
        if (isRolling || mHintList.isEmpty()) return;

        isRolling = true;
        mRunnable = new Runnable() {
            @Override
            public void run() {
                // 安全检查：列表不为空
                if (!mHintList.isEmpty()) {
                    mCurrentIndex = (mCurrentIndex + 1) % mHintList.size();
                    mTextSwitcher.setText(mHintList.get(mCurrentIndex));
                }
                mHandler.postDelayed(this, 3000);
            }
        };
        mHandler.postDelayed(mRunnable, 3000); // 延迟3秒开始第一次切换
    }

    public void stopRoll() {
        isRolling = false;
        mHandler.removeCallbacks(mRunnable);
    }

    private int dp2px(float dp) {
        return (int) (getContext().getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    /**
     * 设置中间标题
     * 调用此方法后，会自动隐藏搜索框，变为“标题模式”
     */
    public void setTitle(String title) {
        if (mTvCenterTitle != null) {
            mTvCenterTitle.setText(title);
            mTvCenterTitle.setVisibility(VISIBLE);
        }
        // 隐藏搜索框
        if (mSearchGroup != null) {
            mSearchGroup.setVisibility(GONE);
        }
    }

    /**
     * 如果以后需要切回搜索模式，可以调这个（可选）
     */
    public void showSearchMode() {
        if (mTvCenterTitle != null) {
            mTvCenterTitle.setVisibility(GONE);
        }
        if (mSearchGroup != null) {
            mSearchGroup.setVisibility(VISIBLE);
        }
    }

    // 在 TitleBar.java 中添加这个方法
    public void setOnBackListener(OnClickListener listener) {
        if (mIvBack != null) {
            // 直接覆盖默认的监听器
            mIvBack.setOnClickListener(listener);
        }
    }
}