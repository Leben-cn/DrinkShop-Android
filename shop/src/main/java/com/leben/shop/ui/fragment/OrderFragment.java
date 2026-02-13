package com.leben.shop.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.ui.fragment.BaseTabFragment;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.Constant.CommonConstant;
import com.leben.shop.R;
import com.leben.shop.constant.ShopConstant;
import java.util.ArrayList;
import java.util.List;

@Route(path = ShopConstant.Router.ORDER)
public class OrderFragment extends BaseTabFragment {
    private TitleBar titleBar;
    private ImageView searchIcon;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_order;
    }

    @Override
    public void initView(View root) {
        super.initView(root);
        titleBar=root.findViewById(R.id.title_bar);
        titleBar.setBackVisible(false);
        titleBar.setTitle("订单");
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
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }

}
