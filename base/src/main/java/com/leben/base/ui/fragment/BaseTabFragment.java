package com.leben.base.ui.fragment;

import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.leben.base.R;
import com.leben.base.ui.adapter.CommonFragmentAdapter;
import java.util.List;

/**
 * 通用的 Tab + ViewPager Fragment 基类
 * 用于 Fragment 嵌套 Fragment 的场景
 */
public abstract class BaseTabFragment extends BaseFragment {

    protected ViewPager2 mViewPager;
    protected TabLayout mTabLayout;

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

        // 1. 获取子 Fragment 数据
        List<Fragment> fragments = getFragments();
        List<String> titles = getTitles();
        List<Integer> icons = getTabIcons();

        if (fragments == null || fragments.isEmpty()) {
            return;
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
            if (titles != null && position < titles.size()) {
                tab.setText(titles.get(position));
            }
            if (icons != null && position < icons.size()) {
                tab.setIcon(icons.get(position));
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
}