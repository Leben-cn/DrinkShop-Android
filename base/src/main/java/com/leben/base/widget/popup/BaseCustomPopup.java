package com.leben.base.widget.popup;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.leben.base.R;
import com.leben.base.model.bean.PopupEntity;

import java.util.List;

public abstract class BaseCustomPopup extends PopupWindow {
    protected Context context;
    protected LinearLayout container;
    protected List<PopupEntity> menuItems;

    public BaseCustomPopup(Context context, List<PopupEntity> items) {
        super(context);
        this.context = context;
        this.menuItems = items;
        init();
    }

    private void init() {
        // 创建容器
        container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setBackgroundResource(R.drawable.bg_popup_white); // 你的圆角阴影背景

        int p = dip2px(8);
        container.setPadding(p, p, p, p);

        // 核心：由子类实现如何填充这个容器
        renderItems();

        setContentView(container);
        setWidth(dip2px(150));
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(10f);
        }
    }

    // 抽象方法：交给子类去决定具体长什么样
    protected abstract void renderItems();

    protected int dip2px(float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }

    // 统一的回调接口
    public interface OnItemClickListener {
        void onItemClick(PopupEntity item);
    }

    protected OnItemClickListener listener;
    public BaseCustomPopup setOnItemClickListener(OnItemClickListener l) { this.listener = l; return this;}
}
