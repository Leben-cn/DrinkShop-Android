package com.leben.shop.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.ui.fragment.BaseTabFragment;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.constant.CommonConstant;
import com.leben.shop.R;
import java.util.ArrayList;
import java.util.List;

@Route(path = CommonConstant.Router.ORDER)
public class OrderFragment extends BaseTabFragment {

    private TitleBar titleBar;
    private ImageView searchIcon;

    @Override
    public void initView(View root) {
        super.initView(root);
        titleBar=root.findViewById(R.id.title_bar);
        titleBar.setBackVisible(false);
        titleBar.setTitle("订单");
        titleBar.setBackgroundResource(R.color.white);
        searchIcon = new ImageView(getContext());
        searchIcon.setImageResource(R.drawable.ic_search);
        titleBar.addRightView(searchIcon);
    }

    @Override
    protected List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new OrderAllFragment());
        fragmentList.add(new OrderNoCommentFragment());
        fragmentList.add(new OrderCancelFragment());
        return fragmentList;
    }

    @Override
    protected List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add("全部订单");
        list.add("待评价");
        list.add("退款/售后");
        return list;
    }

    @Override
    public void initListener() {

    }

    @Override
    protected View getTitleBarView() {
        return mRootView.findViewById(R.id.title_bar);
    }

    @Override
    protected int getTabBackgroundColor() {
        return R.color.white;
    }

    @Override
    protected int getSelectedTextColor() {
        return R.color.orange_500;
    }

    @Override
    protected int getTabIndicatorColor() {
        return R.color.orange_500;
    }

    @Override
    protected int getNormalTextColor() {
        return R.color.black;
    }

    @Override
    protected boolean isTabCenter() {
        return true;
    }

    @Override
    protected boolean hideTitleBar() {
        return false;
    }

    @Override
    protected int getTabTextSize() {
        return 14;
    }
}
