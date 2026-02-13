package com.leben.base.ui.activity;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.leben.base.R;
import com.leben.base.ui.adapter.CommonFragmentAdapter;
import java.util.List;

/**
 * 通用的 Tab + ViewPager 页面基类
 * Created by youjiahui on 2026/2/1.
 */
public abstract class BaseTabActivity extends BaseActivity{

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
    public void initView() {

        mViewPager = findViewById(R.id.base_view_pager);
        mTabLayout = findViewById(R.id.base_tab_layout);

        List<Fragment> fragments = getFragments();
        List<String> titles = getTitles();
        List<Integer> icons = getTabIcons();

        if (fragments == null || fragments.isEmpty()){
            return;
        }

        CommonFragmentAdapter adapter = new CommonFragmentAdapter(this, fragments);
        mViewPager.setAdapter(adapter);

        mViewPager.setUserInputEnabled(isSwipeEnabled());

        mViewPager.setOffscreenPageLimit(fragments.size());

        new TabLayoutMediator(mTabLayout, mViewPager, (tab, position) -> {
            if (titles != null && position < titles.size()) {
                tab.setText(titles.get(position));
            }
            if (icons != null && position < icons.size()) {
                tab.setIcon(icons.get(position));
            }
        }).attach();

        handleTabClickAnimation();

    }

    @Override
    public void initListener() {

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

    //默认禁止滑动
    protected boolean isSwipeEnabled() {
        return false;
    }

    private void handleTabClickAnimation() {
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //false表示无动画切换
                mViewPager.setCurrentItem(tab.getPosition(), false);
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

}
