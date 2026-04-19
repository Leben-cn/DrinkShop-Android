package com.leben.common.ui.activity;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.ui.activity.BaseTabActivity;
import com.leben.base.util.LogUtils;
import com.leben.common.constant.CommonConstant;
import com.leben.common.R;
import com.leben.common.model.bean.ShopCategoriesEntity;
import com.leben.common.model.event.UpdateTabUnreadEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

@Route(path = CommonConstant.Router.MERCHANT_TAB)
public class MerchantActivity extends BaseTabActivity {

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
        Fragment workBenchFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.WORK_BENCH).navigation();
        Fragment goodsFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.GOODS).navigation();
        Fragment sessionListFragment = (Fragment) ARouter.getInstance().build(CommonConstant.Router.SESSION_LIST).navigation();

        if (workBenchFragment != null) {
            fragmentList.add(workBenchFragment);
        }
        if (goodsFragment != null) {
            fragmentList.add(goodsFragment);
        }
        if (sessionListFragment != null) {
            fragmentList.add(sessionListFragment);
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
