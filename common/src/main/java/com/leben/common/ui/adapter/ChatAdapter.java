package com.leben.common.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.R;
import com.leben.common.model.bean.ChatMessageEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 聊天消息适配器
 * 逻辑：3分钟内隐藏时间头，非今天显示周几/日期
 */
public class ChatAdapter extends BaseRecyclerAdapter<ChatMessageEntity> {

    public static final int MSG_TYPE_RECEIVE = 10;
    public static final int MSG_TYPE_SEND = 11;

    private String mCurrentRole = "";
    private String mMyAvatar;
    private String mTargetAvatar;

    public ChatAdapter(Context context, String currentRole, String myAvatar, String targetAvatar) {
        super(context);
        this.mCurrentRole = currentRole;
        this.mMyAvatar = myAvatar;
        this.mTargetAvatar = targetAvatar;
    }

    @Override
    public int getItemViewType(int position) {
        if (getFooterLayoutCount() > 0 && position == getItemCount() - 1) {
            return TYPE_FOOTER;
        }
        ChatMessageEntity data = getItem(position);
        if (data == null || data.getSenderRole() == null) {
            return MSG_TYPE_RECEIVE;
        }
        return data.getSenderRole().equals(mCurrentRole) ? MSG_TYPE_SEND : MSG_TYPE_RECEIVE;
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == MSG_TYPE_SEND) {
            return R.layout.common_item_chat_send;
        } else if (viewType == MSG_TYPE_RECEIVE) {
            return R.layout.common_item_chat_receive;
        }
        return 0;
    }

    @Override
    protected void bindData(BaseViewHolder holder, ChatMessageEntity data, int position) {
        // 1. 绑定内容
        holder.setText(R.id.tv_content, data.getContent());

        // 2. 头像处理
        int itemViewType = getItemViewType(position);
        holder.setImageUrl(R.id.iv_avatar, itemViewType == MSG_TYPE_SEND ? mMyAvatar : mTargetAvatar);

        // 3. 智能时间头处理
        View tvTime = holder.getView(R.id.tv_time);
        if (tvTime != null) {
            boolean isShow = true;
            String currentTimeStr = data.getSendTime();

            if (TextUtils.isEmpty(currentTimeStr)) {
                isShow = false;
            } else if (position > 0) {
                // 3分钟逻辑：获取上一条时间进行对比
                ChatMessageEntity prevData = getItem(position - 1);
                if (prevData != null && !TextUtils.isEmpty(prevData.getSendTime())) {
                    long currentL = parseTimeToLong(currentTimeStr);
                    long prevL = parseTimeToLong(prevData.getSendTime());
                    if (currentL - prevL < 3 * 60 * 1000) {
                        isShow = false;
                    }
                }
            }

            if (isShow) {
                tvTime.setVisibility(View.VISIBLE);
                holder.setText(R.id.tv_time, getSmartTime(currentTimeStr));
            } else {
                tvTime.setVisibility(View.GONE); // 彻底隐藏占位
            }
        }
    }

    /**
     * 智能获取时间字符串
     * 今天：15:30
     * 本周：周一 15:30
     * 更早：04-18 15:30
     */
    private String getSmartTime(String timeStr) {
        long msgTimeMs = parseTimeToLong(timeStr);
        if (msgTimeMs == 0) return timeStr;

        Calendar msgCalendar = Calendar.getInstance();
        msgCalendar.setTimeInMillis(msgTimeMs);

        Calendar nowCalendar = Calendar.getInstance();

        // 格式化器
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        if (isSameDay(msgCalendar, nowCalendar)) {
            // 1. 今天
            return timeFormat.format(new Date(msgTimeMs));
        } else {
            // 2. 跨天了，判断是否在最近一周内 (7天内)
            long diff = nowCalendar.getTimeInMillis() - msgTimeMs;
            if (diff > 0 && diff < 7 * 24 * 60 * 60 * 1000) {
                // 显示：周几 15:30
                String weekDay = new SimpleDateFormat("EEE", Locale.getDefault()).format(new Date(msgTimeMs));
                return weekDay + " " + timeFormat.format(new Date(msgTimeMs));
            } else {
                // 3. 更久以前：04-19 15:30
                return new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()).format(new Date(msgTimeMs));
            }
        }
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private long parseTimeToLong(String timeStr) {
        if (TextUtils.isEmpty(timeStr)) return 0;
        try {
            if (timeStr.contains("T")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date date = sdf.parse(timeStr);
                return date != null ? date.getTime() : 0;
            } else {
                return Long.parseLong(timeStr);
            }
        } catch (Exception e) {
            return 0;
        }
    }
}