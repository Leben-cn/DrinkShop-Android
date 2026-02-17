package com.leben.shop.ui.dialog;

import static com.leben.base.util.ConvertUtils.toPx;

import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.leben.base.widget.dialog.BaseBottomSheetDialog;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.shop.R;

public class RemarkDialog extends BaseBottomSheetDialog {
    private EditText etRemark;
    private TextView tvCount;
    private TitleBar titleBar;
    private TextView tvComplete;

    // 1. 定义接口
    public interface OnConfirmListener {
        void onConfirm(String content);
    }

    private OnConfirmListener mListener;

    public void setOnConfirmListener(OnConfirmListener listener) {
        this.mListener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.shop_dialog_remark;
    }

    @Override
    protected void initView(View root) {
        etRemark = root.findViewById(R.id.et_remark);
        tvCount = root.findViewById(R.id.tv_count);
        titleBar=root.findViewById(R.id.title_bar);

        if(titleBar!=null){
            titleBar.setTitle("添加备注");
            tvComplete = new TextView(getContext());
            tvComplete.setText("确认");
            tvComplete.setTextColor(Color.parseColor("#333333")); // 深灰色
            tvComplete.setTextSize(14); // 字体大小

            tvComplete.setBackgroundResource(R.drawable.bg_btn);
            tvComplete.setGravity(Gravity.CENTER);

            int btnWidth = toPx(getContext(), 60);
            int btnHeight = toPx(getContext(), 34);

            // 因为 TitleBar 内部的容器是 LinearLayout，所以使用 LinearLayout.LayoutParams
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(btnWidth, btnHeight);

            // 将这个带有具体大小参数的 params 设置给 TextView
            tvComplete.setLayoutParams(params);

            titleBar.setOnBackListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss(); // 关闭当前弹窗
                }
            });

            titleBar.addRightView(tvComplete);
        }

        etRemark.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCount.setText(s.length() + "/50");
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        tvComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onConfirm(etRemark.getText().toString().trim());
                }
                dismiss(); // 这里的 dismiss 是 Fragment 提供的关闭方法
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null) {
            View bottomSheet = getDialog().findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                // 修改点：强制设置高度为屏幕的 3/4
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = screenHeight * 3 / 4;
                bottomSheet.setLayoutParams(layoutParams);

                // 配合基类的 Behavior 设置
                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setPeekHeight(screenHeight * 3 / 4); // 确保初始高度
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
        }

    }
}
