package com.leben.merchant.ui.adapter;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.InputDialog;
import com.leben.common.model.bean.SpecOptionEntity;
import com.leben.merchant.R;
import java.math.BigDecimal;
import java.util.List;

public class SpecOptionAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<SpecOptionEntity> data;

    public SpecOptionAdapter(List<SpecOptionEntity> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.merchant_item_spec_option, parent, false);
        return new BaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        SpecOptionEntity item = data.get(position);
        TextView tv = holder.getView(R.id.tv_name);

        String name = item.getOptionName();

        if (item.getPrice() != null && item.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            String priceStr = "¥" + item.getPrice().stripTrailingZeros().toPlainString();
            String separator = " | ";
            String fullText = name + separator + priceStr;

            SpannableString spannableString = new SpannableString(fullText);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF4D4F"));
            int startColorIndex = name.length() + separator.length();
            spannableString.setSpan(colorSpan, startColorIndex, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            tv.setText(spannableString);
        } else {
            tv.setText(name);
        }

        tv.setSelected(item.isSelected());

        // 短按事件
        holder.itemView.setOnClickListener(v -> {
            item.setSelected(!item.isSelected());
            notifyItemChanged(position);
        });

        if(item.isSelected){
            // 长按事件
            holder.itemView.setOnLongClickListener(v -> {
                // 【核心修改】：使用剥洋葱方法获取 Activity
                AppCompatActivity activity = getActivity(v.getContext());

                if (activity != null) {
                    String oldPrice = "";
                    if (item.getPrice() != null && item.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                        oldPrice = item.getPrice().stripTrailingZeros().toPlainString();
                    }

                    InputDialog.newInstance()
                            .setTitle("设置（" + item.getOptionName() + "）加价")
                            .setHint("输入加价金额")
                            .setDefaultText(oldPrice)
                            .setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL)
                            .setOnConfirmListener((dialog, inputText) -> {
                                try {
                                    if (TextUtils.isEmpty(inputText)) {
                                        item.setPrice(BigDecimal.ZERO);
                                    } else {
                                        item.setPrice(new BigDecimal(inputText));
                                    }
                                    item.setSelected(true);
                                    notifyItemChanged(position);
                                } catch (Exception e) {
                                    ToastUtils.show(activity, "金额格式有误");
                                }
                            })
                            .show(activity.getSupportFragmentManager(), "dialog_InputPrice");
                }
                return true;
            });
        }


    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    /**
     * 【剥洋葱方法】从 Context 中剥离出真实的 AppCompatActivity
     */
    private AppCompatActivity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof AppCompatActivity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}