package com.leben.common.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.shape.ShapeAppearanceModel;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.R;
import com.leben.common.model.bean.SessionEntity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by youjiahui on 2026/4/15.
 */

public class SessionListAdapter extends BaseRecyclerAdapter<SessionEntity> {

    private SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yy/MM/dd", Locale.getDefault());
    private Calendar nowCalendar = Calendar.getInstance();
    private Calendar targetCalendar = Calendar.getInstance();

    public SessionListAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.common_item_session_list;
    }

    @Override
    protected void bindData(BaseViewHolder holder, SessionEntity data, int position) {
        // 绑定对方昵称
        holder.setText(R.id.tv_name, data.getTargetName())
                .setText(R.id.tv_last_msg, data.getLastMessage())
                .setText(R.id.tv_time, formatTime(data.getLastTime()))
                .setImageUrl(R.id.iv_avatar,data.getTargetIcon());

        // 渲染未读数逻辑
        TextView tvUnread = holder.getView(R.id.tv_unread);
        if (data.getUnreadCount() > 0) {
            tvUnread.setVisibility(View.VISIBLE);
            // 如果未读数超过99，通常显示 99+
            String countText = data.getUnreadCount() > 99 ? "99+" : String.valueOf(data.getUnreadCount());
            tvUnread.setText(countText);
        } else {
            tvUnread.setVisibility(View.GONE);
        }
    }

    private String formatTime(String timeStr) {
        try {
            Date date = isoFormat.parse(timeStr);
            targetCalendar.setTime(date);
            nowCalendar.setTime(new Date()); // 更新当前时间

            // 判断是否是今天
            if (nowCalendar.get(Calendar.YEAR) == targetCalendar.get(Calendar.YEAR) &&
                    nowCalendar.get(Calendar.DAY_OF_YEAR) == targetCalendar.get(Calendar.DAY_OF_YEAR)) {
                return timeFormat.format(date);
            } else {
                return dateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return timeStr;
        }
    }
}