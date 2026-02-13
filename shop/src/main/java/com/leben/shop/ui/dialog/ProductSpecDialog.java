package com.leben.shop.ui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.leben.base.widget.dialog.BaseBottomSheetDialog;
import com.leben.shop.R;
import com.leben.shop.model.bean.DrinkEntity;
import com.leben.shop.model.bean.SpecGroupEntity;
import com.leben.shop.model.bean.SpecOptionEntity;
import com.leben.shop.ui.adapter.SpecGroupAdapter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecDialog extends BaseBottomSheetDialog {

    private DrinkEntity mDrink;
    private TextView tvTitle, tvPrice, tvSummary;
    private SpecGroupAdapter mAdapter;
    // 回调接口
    private OnAddToCartListener mListener;

    public interface OnAddToCartListener {
        void onAddToCart(DrinkEntity drink, List<SpecOptionEntity> selectedOptions, BigDecimal finalPrice, String specDesc);
    }

    public static ProductSpecDialog newInstance(DrinkEntity drink) {
        ProductSpecDialog dialog = new ProductSpecDialog();
        Bundle args = new Bundle();
        args.putSerializable("data", drink);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_product_spec;
    }

    @Override
    protected void initView(View root) {
        mDrink = (DrinkEntity) getArguments().getSerializable("data");
        if(mDrink == null) return;

        // 初始化默认选中：如果还没选中，默认选每组的第一个
        initDefaultSelection();

        tvTitle = root.findViewById(R.id.tv_title);
        tvPrice = root.findViewById(R.id.tv_total_price);
        tvSummary = root.findViewById(R.id.tv_selected_summary);
        ImageView ivClose = root.findViewById(R.id.iv_close);
        Button btnAdd = root.findViewById(R.id.btn_add_cart);
        RecyclerView rv = root.findViewById(R.id.rv_specs);

        tvTitle.setText(mDrink.getName());
        ivClose.setOnClickListener(v -> dismiss());

        // 设置 Adapter
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new SpecGroupAdapter(mDrink.specs, this::updateUI);
        rv.setAdapter(mAdapter);

        // 首次计算
        updateUI();

        // 加入购物车事件
        btnAdd.setOnClickListener(v -> {
            if (mListener != null) {
                // 收集选中的 specs
                List<SpecOptionEntity> selected = new ArrayList<>();
                BigDecimal total = mDrink.getPrice(); // 基础价
                StringBuilder sb = new StringBuilder();

                for (SpecGroupEntity group : mDrink.specs) {
                    for (SpecOptionEntity opt : group.options) {
                        if (opt.isSelected) {
                            selected.add(opt);
                            if (opt.price != null) total = total.add(opt.price);
                            sb.append(opt.name).append(" ");
                        }
                    }
                }
                mListener.onAddToCart(mDrink, selected, total, sb.toString());
                dismiss();
            }
        });
    }

    // 默认选中逻辑：每组单选必须默认选中第一个
    private void initDefaultSelection() {
        if (mDrink.specs == null) return;
        for (SpecGroupEntity group : mDrink.specs) {
            if (!group.isMultiple && group.options != null && !group.options.isEmpty()) {
                boolean hasSelected = false;
                for (SpecOptionEntity opt : group.options) {
                    if (opt.isSelected) {
                        hasSelected = true;
                        break;
                    }
                }
                // 如果单选组一个都没选，强制选中第一个
                if (!hasSelected) {
                    group.options.get(0).isSelected = true;
                }
            }
        }
    }

    // 更新价格和描述 UI
    private void updateUI() {
        BigDecimal total = mDrink.getPrice(); // 起始为基础价
        StringBuilder summary = new StringBuilder("已选：");

        if (mDrink.specs != null) {
            for (SpecGroupEntity group : mDrink.specs) {
                for (SpecOptionEntity opt : group.options) {
                    if (opt.isSelected) {
                        if (opt.price != null) {
                            total = total.add(opt.price);
                        }
                        summary.append(opt.name).append(" ");
                    }
                }
            }
        }
        tvPrice.setText("¥" + total);
        tvSummary.setText(summary.toString());
    }

    public void setListener(OnAddToCartListener listener) {
        this.mListener = listener;
    }
}