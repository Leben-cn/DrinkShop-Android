package com.leben.common.ui.activity;

import androidx.fragment.app.Fragment;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.ui.activity.BaseTabActivity;
import com.leben.common.constant.CommonConstant;
import com.leben.common.R;
import java.util.ArrayList;
import java.util.List;

@Route(path = CommonConstant.Router.MERCHANT_TAB)
public class MerchantActivity extends BaseTabActivity {
    @Override
    protected List<Fragment> getFragments() {

        List<Fragment> fragmentList = new ArrayList<>();
        //使用 ARouter 获取实例
        Fragment workBenchFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.WORK_BENCH).navigation();
        Fragment goodsFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.GOODS).navigation();
        Fragment messageFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.MESSAGE).navigation();

        if (workBenchFragment != null) {
            fragmentList.add(workBenchFragment);
        }
        if (goodsFragment != null) {
            fragmentList.add(goodsFragment);
        }
        if (messageFragment != null) {
            fragmentList.add(messageFragment);
        }

        return fragmentList;
    }

    @Override
    protected List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add("工作台");
        list.add("商品");
        list.add("消息");
        return list;
    }

    @Override
    protected List<Integer> getTabIcons() {
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.tab_workbench_selector);
        list.add(R.drawable.tab_product_selector);
        list.add(R.drawable.tab_message_selector);
        return list;
    }

    @Override
    protected boolean isTabTop() {
        return false;
    }

    @Override
    protected boolean hideTabIndicator() {
        return true;
    }

    @Override
    protected int getSelectedTextColor() {
        return R.color.orange_400;
    }

    @Override
    protected int getTabIconSize() {
        return 28;
    }

}
