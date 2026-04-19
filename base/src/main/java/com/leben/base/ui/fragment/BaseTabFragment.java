package com.leben.base.ui.fragment;

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
 * 通用的 Tab + ViewPager Fragment 基类
 * Created by youjiahui on 2026/4/12.
 */
public abstract class BaseTabFragment extends BaseFragment {

    protected ViewPager2 mViewPager;
    protected TabLayout mTabLayout;
    protected TitleBar mTitleBar;
    protected View mTabDivider;

    @Override
    protected int getLayoutId() {
        // 使用与 Activity 相同的布局资源
        return isTabTop() ? R.layout.ac_base_tab_top : R.layout.ac_base_tab_bottom;
    }

    @Override
    protected void initView(View root) {
        mViewPager = root.findViewById(R.id.base_view_pager);
        mTabLayout = root.findViewById(R.id.base_tab_layout);
        mTitleBar = root.findViewById(R.id.title_bar);
        mTabDivider = root.findViewById(R.id.tab_divider);

        if (mTabDivider != null) {
            mTabDivider.setVisibility(showTabDivider() ? View.VISIBLE : View.GONE);
        }

        mTabLayout.setBackgroundColor(resolveColor(getTabBackgroundColor()));
        mTabLayout.setTabIconTint(null); // 允许图标使用原始颜色
        mTabLayout.setTabRippleColor(ColorStateList.valueOf(Color.TRANSPARENT)); // 去除点击阴影

        if (hideTabIndicator()) {
            mTabLayout.setSelectedTabIndicatorHeight(0);
        } else {
            mTabLayout.setSelectedTabIndicatorColor(resolveColor(getTabIndicatorColor()));
        }

        setupTabLayoutStyle();

        setupTitleBar();

        List<Fragment> fragments = getFragments();
        if (fragments == null || fragments.isEmpty()) return;

        // 注意：Fragment 嵌套 Fragment 必须使用 this (ChildFragmentManager)
        mViewPager.setAdapter(new CommonFragmentAdapter(this, fragments));
        mViewPager.setUserInputEnabled(isSwipeEnabled());
        mViewPager.setOffscreenPageLimit(fragments.size());

        new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.ly_custom_tab, null);
            // 修正垂直方向边距
            view.setPadding(view.getPaddingLeft(), dp2px(8), view.getPaddingRight(), dp2px(8));

            ImageView tabIcon = view.findViewById(R.id.tab_icon);
            TextView tabText = view.findViewById(R.id.tab_text);

            if (getTitles() != null && position < getTitles().size()) {
                tabText.setText(getTitles().get(position));
            }
            List<Integer> icons = getTabIcons();
            if (icons != null && position < icons.size() && icons.get(position) != 0) {
                tabIcon.setVisibility(View.VISIBLE);
                tabIcon.setImageResource(icons.get(position));
                // 设置图标大小
                tabIcon.getLayoutParams().width = dp2px(getTabIconSize());
                tabIcon.getLayoutParams().height = dp2px(getTabIconSize());
            } else {
                tabIcon.setVisibility(View.GONE);
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
                // 如果需要点击切换无动画，保持 false；若需要平滑滚动，改为 true
                mViewPager.setCurrentItem(tab.getPosition(), false);
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
    /** -2: 默认, -1: Match_Parent, >0: dp数值 */
    protected int getTitleBarHeight() { return -2; }

    // --- 工具方法 ---
    private int resolveColor(int colorOrResId) {
        try {
            return ContextCompat.getColor(requireContext(), colorOrResId);
        } catch (Exception e) {
            return colorOrResId;
        }
    }

    protected int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private ColorStateList createTabColorStateList(int normal, int selected) {
        return new ColorStateList(
                new int[][]{{android.R.attr.state_selected}, {}},
                new int[]{selected, normal}
        );
    }

    @Override public void initData() {}

    /**
     * 设置指定 Tab 的未读数
     * @param index Tab的索引（从0开始）
     * @param count 未读数量
     */
    public void setTabUnread(int index, int count) {
        TabLayout.Tab tab = mTabLayout.getTabAt(index);
        if (tab != null && tab.getCustomView() != null) {
            TextView tvUnread = tab.getCustomView().findViewById(R.id.tv_tab_unread);
            if (tvUnread != null) {
                if (count > 0) {
                    tvUnread.setVisibility(View.VISIBLE);
                    tvUnread.setText(count > 99 ? "99+" : String.valueOf(count));
                } else {
                    tvUnread.setVisibility(View.GONE);
                }
            }
        }
    }
}