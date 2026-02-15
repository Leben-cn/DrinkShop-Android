package com.leben.common.ui.activity;

import androidx.fragment.app.Fragment;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.ui.activity.BaseTabActivity;
import com.leben.common.Constant.CommonConstant;
import com.leben.common.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by youjiahui on 2026/1/30.
 */
@Route(path = CommonConstant.Router.CUSTOMER_TAB)
public class CustomerActivity extends BaseTabActivity {

    @Override
    protected List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<>();
        //使用 ARouter 获取实例
        Fragment recommendDrinksFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.RECOMMEND_DRINKS).navigation();
        Fragment orderFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.ORDER).navigation();
        Fragment messageFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.MESSAGE).navigation();
        Fragment userCenterFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.USER_CENTER).navigation();

        if (recommendDrinksFragment != null) {
            fragmentList.add(recommendDrinksFragment);
        }
        if (orderFragment != null) {
            fragmentList.add(orderFragment);
        }
        if (messageFragment != null) {
            fragmentList.add(messageFragment);
        }
        if (userCenterFragment != null) {
            fragmentList.add(userCenterFragment);
        }
        return fragmentList;
    }

    @Override
    protected List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add("首页");
        list.add("订单");
        list.add("消息");
        list.add("我的");
        return list;
    }

    @Override
    protected List<Integer> getTabIcons() {
        List<Integer> list = new ArrayList<>();
        list.add(R.drawable.ic_home_page);
        list.add(R.drawable.ic_order_page);
        list.add(R.drawable.ic_message_page);
        list.add(R.drawable.ic_my_page);
        return list;
    }

    @Override
    protected boolean isTabTop() {
        return false;
    }
}
