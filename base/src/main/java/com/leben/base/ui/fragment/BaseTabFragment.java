package com.leben.base.ui.fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
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
 * 用于 Fragment 嵌套 Fragment 的场景
 */
public abstract class BaseTabFragment extends BaseFragment {

    protected ViewPager2 mViewPager;
    protected TabLayout mTabLayout;
    protected TitleBar mTitleBar; // 假设你的 TitleBar 类名是这个
    protected View mTabDivider; // 定义分割线变量

    @Override
    protected int getLayoutId() {
        if (isTabTop()) {
            return R.layout.ac_base_tab_top;
        } else {
            return R.layout.ac_base_tab_bottom;
        }
    }

    @Override
    protected void initView(View root) {
        mViewPager = root.findViewById(R.id.base_view_pager);
        mTabLayout = root.findViewById(R.id.base_tab_layout);
        mTitleBar = root.findViewById(R.id.title_bar);
        mTabDivider = root.findViewById(R.id.tab_divider); // 初始化

        // 控制分割线显示隐藏
        if (mTabDivider != null) {
            mTabDivider.setVisibility(showTabDivider() ? View.VISIBLE : View.GONE);
        }

        // 1. 获取子 Fragment 数据
        List<Fragment> fragments = getFragments();
        List<String> titles = getTitles();
        List<Integer> icons = getTabIcons();

        mTabLayout.setBackgroundColor(resolveColor(getTabBackgroundColor()));
        mTabLayout.setSelectedTabIndicatorColor(resolveColor(getTabIndicatorColor()));

        // 文字颜色同理
        mTabLayout.setTabTextColors(resolveColor(getNormalTextColor()), resolveColor(getSelectedTextColor()));

        // 4. 设置图标着色（如果有图标的话）
        // mTabLayout.setTabIconTint(ColorStateList.valueOf(getSelectedTextColor()));

        if (isTabCenter()) {
            // 1. 关键：宽度依然铺满全屏，这样背景色就能铺满全屏
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mTabLayout.getLayoutParams();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            mTabLayout.setLayoutParams(params);

            // 2. 设置模式为 FIXED（固定模式，不滚动）
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);

            // 3. 设置 Gravity 为 CENTER，让 Tab 整体在内部居中，而不是填满
            mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);

            // 4. (可选) 如果你想让 Tab 之间更紧凑，可以加上：
            // mTabLayout.setInlineLabel(true); // 图标文字一行显示
        } else {
            // 默认模式：填满全屏
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
            mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        }

        if (fragments == null || fragments.isEmpty()) {
            return;
        }

        if (showTitleBar()) {
            mTitleBar.setVisibility(View.VISIBLE);
        }

        // 2. 设置 Adapter
        // 传入 'this' (当前Fragment)，而不是 getActivity()
        CommonFragmentAdapter adapter = new CommonFragmentAdapter(this, fragments);
        mViewPager.setAdapter(adapter);

        // 3. 是否允许滑动
        mViewPager.setUserInputEnabled(isSwipeEnabled());

        // 4. 预加载策略 (可选，根据需要开启，防止切换时销毁)
        mViewPager.setOffscreenPageLimit(fragments.size());

        // 5. 绑定 TabLayout 和 ViewPager2
        new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            if (getTitles() != null && position < getTitles().size()) {
                tab.setText(getTitles().get(position));
            }
            if (getTabIcons() != null && position < getTabIcons().size()) {
                tab.setIcon(getTabIcons().get(position));
            }
        }).attach();

        // 6. 处理点击动画
        handleTabClickAnimation();
    }

    @Override
    public void initData() {
    }

    protected abstract List<Fragment> getFragments();

    protected abstract List<String> getTitles();

    protected List<Integer> getTabIcons() {
        return null;
    }

    protected boolean isTabTop() {
        return true;
    }

    protected boolean isSwipeEnabled() {
        return false;
    }

    private void handleTabClickAnimation() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    /**
     * 是否显示标题栏，子类重写返回 true 即可显示
     */
    protected boolean showTitleBar() {
        return false;
    }

    /**
     * 默认 Tab 背景颜色 (默认白色)
     */
    protected int getTabBackgroundColor() {
        return R.color.grey_100;
    }

    /**
     * 默认指示器颜色 (下划线)
     */
    protected int getTabIndicatorColor() {
        return Color.parseColor("#1890FF"); // 默认蓝色
    }

    /**
     * 默认文字未选中颜色
     */
    protected int getNormalTextColor() {
        return Color.parseColor("#666666"); // 默认灰色
    }

    /**
     * 默认文字选中颜色
     */
    protected int getSelectedTextColor() {
        return Color.parseColor("#1890FF"); // 默认蓝色
    }

    /**
     * 辅助方法：简单判断是 ID 还是 颜色值并返回颜色
     */
    private int resolveColor(int colorOrResId) {
        try {
            // 尝试当做资源 ID 获取
            return ContextCompat.getColor(requireContext(), colorOrResId);
        } catch (Exception e) {
            // 如果报错，说明它本身就是一个颜色整数值（如 Color.WHITE）
            return colorOrResId;
        }
    }

    /**
     * 是否让 Tab 居中且不充满全屏
     * 子类返回 true 即可切换到居中模式
     */
    protected boolean isTabCenter() {
        return false;
    }

    /**
     * 是否显示 Tab 下方的分割线
     * 默认显示，子类重写返回 false 即可隐藏
     */
    protected boolean showTabDivider() {
        return true;
    }
}