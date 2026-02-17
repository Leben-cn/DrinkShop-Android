package com.leben.shop.ui.fragment;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.common.model.bean.CommentEntity;
import com.leben.common.ui.adapter.CommentAdapter;
import com.leben.shop.constant.ShopConstant;
import com.leben.shop.contract.GetShopCommentContract;
import com.leben.shop.contract.GetShopInfoContract;
import com.leben.shop.presenter.GetShopCommentPresenter;

import java.util.List;

@Route(path = ShopConstant.Router.SHOP_COMMENT)
public class ShopCommentFragment extends BaseRecyclerFragment<CommentEntity> implements GetShopCommentContract.View {

    private Long shopId;

    @InjectPresenter
    GetShopCommentPresenter getShopCommentPresenter;

    @Override
    protected BaseRecyclerAdapter<CommentEntity> createAdapter() {
        return new CommentAdapter(getContext(),CommentAdapter.TYPE_SHOP_COMMENT);
    }

    @Override
    public void onRefresh() {
        getShopCommentPresenter.getShopComment(shopId);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {
        if (getArguments() != null) {
            shopId = getArguments().getLong("shopId", 0);
        }
        if (shopId != 0) {
            getShopCommentPresenter.getShopComment(shopId);
        }
    }

    //定义静态方法实例化 Fragment，并传入参数
    public static ShopCommentFragment newInstance(long shopId) {
        ShopCommentFragment fragment = new ShopCommentFragment();
        Bundle args = new Bundle();
        args.putLong("shopId", shopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onGetShopCommentSuccess(List<CommentEntity> data) {
        refreshListSuccess(data);
    }

    @Override
    public void onGetShopCommentFailed(String errorMsg) {
        refreshListFailed("加载评论失败");
        LogUtils.error("加载评论失败："+errorMsg);
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }
}
