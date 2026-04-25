package com.leben.common.ui.activity;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alibaba.fastjson.JSON;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.model.event.RefreshEvent;
import com.leben.base.ui.activity.BaseRecyclerActivity;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.util.LogUtils;
import com.leben.base.util.SharedPreferencesUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.BuildConfig;
import com.leben.common.R;
import com.leben.common.constant.CommonConstant;
import com.leben.common.contract.GetMessageListContract;
import com.leben.common.model.bean.ChatMessageEntity;
import com.leben.common.model.bean.LoginEntity;
import com.leben.common.model.event.UpdateTabUnreadEvent;
import com.leben.common.presenter.GetMessageListPresenter;
import com.leben.common.ui.adapter.ChatAdapter;
import com.leben.common.util.MerchantUtils;
import com.leben.common.util.UserUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Created by youjiahui on 2026/4/15.
 */

@Route(path = CommonConstant.Router.CHAT_DETAIL)
public class ChatDetailActivity extends BaseRecyclerActivity<ChatMessageEntity> implements GetMessageListContract.View {

    private String mCurrentRole;       // 自己的ID
    private Long mCurrentId;
    private EditText etInput;
    private Button btnSend;
    private String targetName;
    private Long targetRoleId;
    private String targetRole;
    private String targetAvatar;
    private WebSocket mWebSocket;
    private OkHttpClient mClient;

    @InjectPresenter
    GetMessageListPresenter getMessageListPresenter;

    @Override
    protected BaseRecyclerAdapter<ChatMessageEntity> createAdapter() {
        // 1. 获取当前角色
        mCurrentRole = SharedPreferencesUtils.getParam(this, CommonConstant.Key.ROLE, "");

        // 2. 获取我的头像
        String myPic = "";
        if ("USER".equals(mCurrentRole)) {
            LoginEntity.UserInfo info = UserUtils.getUserInfo(this);
            if (info != null) {
                myPic = info.getAvatar();
                mCurrentId = info.getId();
            }
        } else {
            LoginEntity.ShopInfo info = MerchantUtils.getMerchantInfo(this);
            if (info != null) {
                myPic = info.getImg();
                mCurrentId = info.getId();
            }

        }

        // 3. 获取对方的头像（从跳转 Intent 中直接获取）
        String targetPic = getIntent().getStringExtra("targetIcon");
        // 4. 传入 Adapter
        return new ChatAdapter(this, mCurrentRole, myPic, targetPic);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.common_ac_chat_detail;
    }

    @Override
    public void onInit() {
        super.onInit();
        // 1. 获取对方的 ID (SessionEntity 中的 targetId)
        targetRoleId = getIntent().getLongExtra("targetId", -1);
        // 2. 获取对方的名称
        targetName = getIntent().getStringExtra("targetName");
        // 3. 获取对方的角色字符串
        targetRole = getIntent().getStringExtra("targetRoleStr");
        targetAvatar=getIntent().getStringExtra("targetAvatar");

        LogUtils.debug("聊天对象详情: ID=" + targetRoleId + ", Role=" + targetRole);
    }

    @Override
    public void initView() {
        super.initView();
        TitleBar titleBar=findViewById(R.id.title_bar);
        titleBar.setTitle(targetName);
        etInput =findViewById(R.id.et_chat_input);
        btnSend =findViewById(R.id.btn_chat_send);
    }

    @Override
    public void initData() {
        onRefresh();
        initWebSocket();
    }

    @Override
    public void onRefresh() {
        getMessageListPresenter.getMessageList(targetRoleId,targetRole);
    }

    private void initWebSocket() {
        mClient = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS) // 保持长连接
                .build();

        String token = SharedPreferencesUtils.getParam(this, "token", "");
        // 这里的 URL 必须以 ws:// 或 wss:// 开头
        // 注意：后端 WS 地址是 /ws/chat?token=xxx
        Request request = new Request.Builder()
                .url("ws://"+ BuildConfig.SERVER_IP+":8080//ws/chat?token=" + token)
                .build();

        mWebSocket = mClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                // 收到消息（可能是别人发的，也可能是自己发的确认）
                ChatMessageEntity message = JSON.parseObject(text, ChatMessageEntity.class);
                runOnUiThread(() -> {
                    // 如果收到的是自己刚才发出的消息，就不再重复添加
                    // 假设你本地能拿到自己的 ID，或者通过角色简单判断
                    if (mCurrentRole.equals(message.getSenderRole()) &&
                            mCurrentId.equals(message.getSenderId())) {
                        LogUtils.debug("收到自己发送的消息回显，忽略");
                        return;
                    }

                    mAdapter.addData(message);
                    mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                });
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                LogUtils.error("WebSocket连接失败");
                // 可以在这里做重连逻辑
            }
        });
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(btnSend)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    String content = etInput.getText().toString().trim();
                    if (TextUtils.isEmpty(content)) return;

                    // 1. 构建发给后端的 Map (严格保持你后端的 4 个参数)
                    Map<String, Object> map = new HashMap<>();
                    map.put("toId", targetRoleId);
                    map.put("toRole", targetRole);
                    map.put("content", content);
                    map.put("msgType", 0);

                    if (mWebSocket != null) {
                        boolean sent = mWebSocket.send(JSON.toJSONString(map));
                        if (sent) {
                            // 2. 【重点】发送成功后，手动创建一个本地显示的 Entity
                            ChatMessageEntity myMsg = new ChatMessageEntity();
                            myMsg.setContent(content);
                            myMsg.setSenderRole(mCurrentRole); // 标记是我发的，适配器才会放右边

                            // 手动生成 ISO 时间字符串，解决你说的“一长串数字”Bug
                            String nowTime;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                nowTime = java.time.LocalDateTime.now().toString();
                            } else {
                                nowTime = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
                            }
                            myMsg.setSendTime(nowTime);

                            // 3. 更新 UI
                            mAdapter.addData(myMsg);
                            if (mAdapter.getItemCount() == 1) {
                                showContent();
                            }
                            mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
                            etInput.setText("");
                        }
                    } else {
                        LogUtils.error("WebSocket 未连接");
                    }
                });
    }



    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.white;
    }

    @Override
     public void onDestroy() {
        super.onDestroy();
        if (mWebSocket != null) {
            mWebSocket.close(1000, "Activity Destroyed");
        }
    }


    @Override
    public void onGetMessageListSuccess(List<ChatMessageEntity> data) {
        // 1. 填充数据
        refreshListSuccess(data);

        // 2. 强制滚动到底部
        // 使用 post 确保在 RecyclerView 完成布局计算后再执行滚动
        mRecyclerView.post(() -> {
            if (mAdapter != null && mAdapter.getItemCount() > 0) {
                // 直接跳转到最后一条
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    @Override
    public void onGetMessageListFailed(String errorMsg) {
        refreshListFailed(errorMsg);
        LogUtils.error("获取消息列表失败："+errorMsg);
    }

    @Override
    protected boolean isSupportLoadMore() {
        return false;
    }

    @Override
    public void finish() {
        // 在销毁页面前，通知 SessionList 刷新数据
        EventBus.getDefault().post(new RefreshEvent());
        super.finish();
    }
}
