package com.leben.merchant.ui.activity;

import android.view.View;
import androidx.fragment.app.Fragment;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.ui.activity.BaseTabActivity;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import com.leben.merchant.ui.fragment.AnnualStatisticsFragment;
import com.leben.merchant.ui.fragment.DailyStatisticsFragment;
import com.leben.merchant.ui.fragment.MonthlyStatisticsFragment;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by youjiahui on 2026/4/11.
 */

@Route(path = MerchantConstant.Router.SHOP_STATISTICS)
public class ShopStatisticsActivity extends BaseTabActivity {

    private TitleBar titleBar;

    @Override
    public void initView() {
        super.initView();
        titleBar=findViewById(R.id.title_bar);
        if(titleBar!=null){
            titleBar.setTitle("");
            titleBar.setBackgroundResource(R.color.background_green_500);
        }
    }

    @Override
    protected List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(DailyStatisticsFragment.newInstance());
        fragmentList.add(MonthlyStatisticsFragment.newInstance());
        fragmentList.add(AnnualStatisticsFragment.newInstance());
        return fragmentList;
    }

    @Override
    protected List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add("日统计");
        list.add("月统计");
        list.add("年统计");
        return list;
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected int getTabBackgroundColor() {
        return R.color.background_green_500;
    }

    @Override
    protected int getSelectedTextColor() {
        return R.color.white;
    }

    @Override
    protected int getTabIndicatorColor() {
        return R.color.white;
    }

    @Override
    protected int getNormalTextColor() {
        return R.color.background_green_100;
    }

    @Override
    protected boolean isTabCenter() {
        return true;
    }

    @Override
    protected boolean showTabDivider() {
        return false;
    }

    @Override
    protected boolean hideTitleBar() {
        return false;
    }

    @Override
    protected int getTitleBarHeight() {
        return 33; // 设置高度为 56dp
    }
}
