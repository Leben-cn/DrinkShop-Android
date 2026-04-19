package com.leben.common.ui.fragment;

import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.model.event.RefreshEvent;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.R;
import com.leben.common.constant.CommonConstant;
import com.leben.common.contract.GetSessionListContract;
import com.leben.common.model.bean.SessionEntity;
import com.leben.common.model.event.UpdateTabUnreadEvent;
import com.leben.common.presenter.GetSessionListPresenter;
import com.leben.common.ui.adapter.SessionListAdapter;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

/**
 * Created by youjiahui on 2026/4/15.
 */

@Route(path = CommonConstant.Router.SESSION_LIST)
public class SessionListFragment extends BaseRecyclerFragment<SessionEntity> implements GetSessionListContract.View {

    @InjectPresenter
    GetSessionListPresenter getSessionListPresenter;

    @Override
    protected BaseRecyclerAdapter<SessionEntity> createAdapter() {
        return new SessionListAdapter(requireContext());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.common_frag_session_list;
    }

    @Override
    public void onInit() {
        super.onInit();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void initView(View root) {
        setDefaultSpace(8);
        super.initView(root);
        TitleBar titleBar=root.findViewById(R.id.title_bar);
        titleBar.setTitle("消息中心");
        titleBar.setBackVisible(false);
    }

    @Override
    public void onRefresh() {
        getSessionListPresenter.getSessionList();
    }

    @Override
    public void initListener() {
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<SessionEntity>() {
            @Override
            public void onItemClick(View view, int position, SessionEntity entity) {
                String roleStr = "USER";
                if (entity.targetRole == 1) roleStr = "MERCHANT";
                else if (entity.targetRole == 2) roleStr = "ADMIN";

                ARouter.getInstance()
                        .build(CommonConstant.Router.CHAT_DETAIL)
                        .withLong("targetId", entity.getTargetId()) // 注意：这里传的是对方真实的 ID
                        .withString("targetName", entity.getTargetName())
                        .withString("targetRoleStr", roleStr) // 传字符串
                        .withString("targetIcon", entity.getTargetIcon())
                        .navigation();
            }
        });
    }

    @Override
    public void initData() {
        onRefresh();
    }

    @Override
    protected View getTitleBarView() {
        return mRootView.findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }

    @Override
    public void onGetSessionListSuccess(List<SessionEntity> data) {
        // 累加总未读数
        int totalUnread = 0;
        if (data != null) {
            for (SessionEntity session : data) {
                totalUnread += session.getUnreadCount();
            }
        }
        EventBus.getDefault().postSticky(new UpdateTabUnreadEvent(totalUnread));
        refreshListSuccess(data);
    }

    @Override
    public void onGetSessionListFailed(String errorMsg) {
        refreshListFailed(errorMsg);
        LogUtils.error("加载消息列表失败："+errorMsg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshEvent(RefreshEvent event) {
        onRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


}
