package com.leben.merchant.ui.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.common.Constant.CommonConstant;
import com.leben.merchant.model.bean.GoodsEntity;

@Route(path = CommonConstant.Router.GOODS)
public class GoodsFragment extends BaseRecyclerFragment<GoodsEntity> {

    @Override
    protected BaseRecyclerAdapter<GoodsEntity> createAdapter() {
        return null;
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }
}
