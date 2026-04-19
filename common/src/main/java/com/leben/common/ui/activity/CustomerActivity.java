package com.leben.common.ui.activity;

import androidx.fragment.app.Fragment;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.ui.activity.BaseTabActivity;
import com.leben.common.constant.CommonConstant;
import com.leben.common.R;
import com.leben.common.model.event.UpdateTabUnreadEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by youjiahui on 2026/1/30.
 */
@Route(path = CommonConstant.Router.CUSTOMER_TAB)
public class CustomerActivity extends BaseTabActivity {

    @Override
    public void onInit() {
        super.onInit();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<>();
        //使用 ARouter 获取实例
        Fragment recommendDrinksFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.RECOMMEND_DRINKS).navigation();
        Fragment orderFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.ORDER).navigation();
        Fragment sessionListFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.SESSION_LIST).navigation();
        Fragment userCenterFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.USER_CENTER).navigation();

        if (recommendDrinksFragment != null) {
            fragmentList.add(recommendDrinksFragment);
        }
        if (orderFragment != null) {
            fragmentList.add(orderFragment);
        }
        if (sessionListFragment != null) {
            fragmentList.add(sessionListFragment);
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
        list.add(R.drawable.tab_home_selector);
        list.add(R.drawable.tab_order_selector);
        list.add(R.drawable.tab_message_selector);
        list.add(R.drawable.tab_me_selector);
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

    /**
     * Tab 未读数回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnReadMessageEvent(UpdateTabUnreadEvent event) {
        if (event != null) {
            setTabUnread(2, event.getUnreadCount());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
