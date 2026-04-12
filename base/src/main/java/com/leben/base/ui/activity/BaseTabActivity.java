package com.leben.base.ui.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.leben.base.R;
import com.leben.base.ui.adapter.CommonFragmentAdapter;
import com.leben.base.widget.titleBar.TitleBar;
import java.util.List;

/**
 * 通用的 Tab + ViewPager 页面基类
 * Created by youjiahui on 2026/4/12.
 */

public abstract class BaseTabActivity extends BaseActivity {

    protected ViewPager2 mViewPager;
    protected TabLayout mTabLayout;
    protected TitleBar mTitleBar;
    protected View mTabDivider;

    @Override
    protected int getLayoutId() {
        return isTabTop() ? R.layout.ac_base_tab_top : R.layout.ac_base_tab_bottom;
    }

    @Override
    public void initView() {
        mViewPager = findViewById(R.id.base_view_pager);
        mTabLayout = findViewById(R.id.base_tab_layout);
        mTitleBar = findViewById(R.id.title_bar);
        mTabDivider = findViewById(R.id.tab_divider);

        // 1. 基础 UI 配置
        if (mTabDivider != null) {
            mTabDivider.setVisibility(showTabDivider() ? View.VISIBLE : View.GONE);
        }

        mTabLayout.setBackgroundColor(resolveColor(getTabBackgroundColor()));
        mTabLayout.setTabIconTint(null); // 允许图标使用原始颜色/Selector
        // 去除点击时的水波纹/按压灰色背景
        mTabLayout.setTabRippleColor(ColorStateList.valueOf(Color.TRANSPARENT));

        // 2. 指示器配置
        if (hideTabIndicator()) {
            mTabLayout.setSelectedTabIndicatorHeight(0);
        } else {
            mTabLayout.setSelectedTabIndicatorColor(resolveColor(getTabIndicatorColor()));
        }

        // 3. Tab 布局模式配置
        setupTabLayoutStyle();

        // 4. TitleBar 配置
        setupTitleBar();

        // 5. ViewPager2 适配器绑定
        List<Fragment> fragments = getFragments();
        if (fragments == null || fragments.isEmpty()) return;

        mViewPager.setAdapter(new CommonFragmentAdapter(this, fragments));
        mViewPager.setUserInputEnabled(isSwipeEnabled());
        mViewPager.setOffscreenPageLimit(fragments.size());

        // 6. 绑定 TabLayout (使用自定义 View 方案)
        new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            View view = LayoutInflater.from(this).inflate(R.layout.layout_custom_tab, null);
            view.setPadding(view.getPaddingLeft(), dp2px(8), view.getPaddingRight(), dp2px(8));
            ImageView tabIcon = view.findViewById(R.id.tab_icon);
            TextView tabText = view.findViewById(R.id.tab_text);

            if (getTitles() != null && position < getTitles().size()) {
                tabText.setText(getTitles().get(position));
            }
            if (getTabIcons() != null && position < getTabIcons().size()) {
                tabIcon.setImageResource(getTabIcons().get(position));
            }

            // 设置文字颜色状态
            tabText.setTextColor(createTabColorStateList(
                    resolveColor(getNormalTextColor()),
                    resolveColor(getSelectedTextColor())
            ));

            // 设置大小
            tabText.setTextSize(TypedValue.COMPLEX_UNIT_SP, getTabTextSize());
            tabIcon.getLayoutParams().width = dp2px(getTabIconSize());
            tabIcon.getLayoutParams().height = dp2px(getTabIconSize());

            tab.setCustomView(view);
        }).attach();

        handleTabClickAnimation();
    }

    private void setupTabLayoutStyle() {
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        if (isTabCenter()) {
            mTabLayout.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
            mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
        } else {
            mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }
    }

    private void setupTitleBar() {
        if (mTitleBar == null) return;
        int heightValue = getTitleBarHeight();
        if (heightValue != -2) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTitleBar.getLayoutParams();
            lp.height = (heightValue == -1) ? -1 : dp2px(heightValue);
            mTitleBar.setLayoutParams(lp);
        }
        mTitleBar.setVisibility(hideTitleBar() ? View.GONE : View.VISIBLE);
    }

    private void handleTabClickAnimation() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false); // 无动画切换
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    protected abstract List<Fragment> getFragments();
    protected abstract List<String> getTitles();
    protected List<Integer> getTabIcons() { return null; }

    protected int getTabTextSize() { return 12; }
    protected int getTabIconSize() { return 24; }
    protected int getSelectedTextColor() { return Color.parseColor("#1890FF"); }
    protected int getNormalTextColor() { return Color.parseColor("#666666"); }
    protected int getTabIndicatorColor() { return Color.parseColor("#1890FF"); }
    protected int getTabBackgroundColor() { return R.color.white; }
    protected boolean isTabTop() { return true; }
    protected boolean isTabCenter() { return false; }
    protected boolean isSwipeEnabled() { return false; }
    protected boolean hideTitleBar() { return true; }
    protected boolean hideTabIndicator() { return false; }
    protected boolean showTabDivider() { return true; }
    protected int getTitleBarHeight() { return -2; }

    private int resolveColor(int colorOrResId) {
        try {
            return ContextCompat.getColor(this, colorOrResId);
        } catch (Exception e) {
            return colorOrResId;
        }
    }

    protected int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private ColorStateList createTabColorStateList(int normal, int selected) {
        return new ColorStateList(new int[][]{{android.R.attr.state_selected}, {}}, new int[]{selected, normal});
    }

    @Override public void initListener() {}
    @Override public void initData() {}
}