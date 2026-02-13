package com.leben.user.ui.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.user.constant.UserConstant;
import com.leben.common.model.bean.MessageEntity;

@Route(path = UserConstant.Router.MESSAGE)
public class MessageFragment extends BaseRecyclerFragment<MessageEntity> {

    @Override
    protected BaseRecyclerAdapter<MessageEntity> createAdapter() {
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
