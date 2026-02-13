package com.leben.base.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import java.util.List;

/**
 * ViewPager2强制使用 FragmentStateAdapter
 * Created by youjiahui on 2026/1/30.
 */

public class CommonFragmentAdapter extends FragmentStateAdapter {

    private final List<Fragment> mFragmentList;

    public CommonFragmentAdapter(@NonNull FragmentActivity fragmentActivity, List<Fragment> fragmentList) {
        super(fragmentActivity);
        this.mFragmentList = fragmentList;
    }

    // 【新增】构造函数（用于 Fragment 嵌套）
    // 这里的 fragment 参数就是你的 BaseTabFragment
    public CommonFragmentAdapter(@NonNull Fragment fragment, List<Fragment> fragmentList) {
        super(fragment); // 这一步非常关键！它内部会自动使用 getChildFragmentManager()
        this.mFragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragmentList == null ? 0 : mFragmentList.size();
    }
}
