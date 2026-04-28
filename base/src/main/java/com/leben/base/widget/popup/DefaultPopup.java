package com.leben.base.widget.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leben.base.R;
import com.leben.base.model.bean.PopupEntity;
import java.util.List;

public class DefaultPopup extends BaseCustomPopup {
    public DefaultPopup(Context context, List<PopupEntity> items) {
        super(context, items);
        // 1. 设置背景资源（带圆角）
        container.setBackgroundResource(R.drawable.bg_popup_floating);

        // 2. 设置阴影高度（API 21+）
        // 注意：必须先设置背景，阴影才会生效
        container.setElevation(dip2px(2));

        // 3. 关键：解决阴影显示不全的问题
        // PopupWindow 默认的 WindowContentOverlay 可能会挡住阴影
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void renderItems() {
        for (int i = 0; i < menuItems.size(); i++) {
            PopupEntity item = menuItems.get(i);
            TextView tv = new TextView(context);
            tv.setText(item.text);
            tv.setTextColor(item.textColor);
            // 1. 创建布局参数，高度设为 wrap_content
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            // 2. 设置间距（关键点）
            // 如果不是最后一项，就设置底部间距
            if (i < menuItems.size() - 1) {
                lp.bottomMargin = dip2px(8); // 这里设置项与项之间的距离，比如 8dp
            }
            tv.setLayoutParams(lp);

            // 3. 其他样式设置
            tv.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            tv.setPaddingRelative(dip2px(12), dip2px(12), dip2px(12), dip2px(12));

            tv.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
                dismiss();
            });

            container.addView(tv);
        }
    }
}
