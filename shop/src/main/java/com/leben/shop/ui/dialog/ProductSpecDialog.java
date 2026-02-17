package com.leben.shop.ui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.leben.base.widget.dialog.BaseBottomSheetDialog;
import com.leben.common.model.bean.GroupEntity;
import com.leben.common.model.bean.SpecGroupEntity;
import com.leben.shop.R;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.common.model.bean.SpecOptionEntity;
import com.leben.shop.ui.adapter.SpecGroupAdapter;
import com.leben.shop.util.ListGroupUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecDialog extends BaseBottomSheetDialog {

    private DrinkEntity mDrink;
    private TextView tvTitle, tvPrice, tvSummary;
    private SpecGroupAdapter mAdapter;
    private List<GroupEntity<SpecGroupEntity, SpecOptionEntity>> mGroupList;
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
        return R.layout.shop_dialog_product_spec;
    }

    @Override
    protected void initView(View root) {
        mDrink = (DrinkEntity) getArguments().getSerializable("data");
        if(mDrink == null) {
            return;
        }

        if (mDrink.specs != null) {
            mGroupList = ListGroupUtils.groupList(
                    mDrink.specs,
                    // Lambda: 告诉工具类如何生成 Key
                    item -> new SpecGroupEntity(item.groupId, item.groupName, item.isMultiple)
            );
        } else {
            mGroupList = new ArrayList<>();
        }

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
        mAdapter = new SpecGroupAdapter(mGroupList, this::updateUI);
        rv.setAdapter(mAdapter);

        // 首次计算
        updateUI();

        // 加入购物车事件
        btnAdd.setOnClickListener(v -> {
            if (mListener != null) {
                List<SpecOptionEntity> selected = new ArrayList<>();
                BigDecimal total = mDrink.getPrice();
                StringBuilder sb = new StringBuilder();

                if (mGroupList != null) {
                    for (GroupEntity<SpecGroupEntity, SpecOptionEntity> group : mGroupList) {
                        for (SpecOptionEntity opt : group.getChildren()) {
                            if (opt.isSelected) {
                                selected.add(opt);
                                if (opt.price != null) total = total.add(opt.price);
                                sb.append(opt.optionName).append(" ");
                            }
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
        if (mGroupList == null) return;
        // 遍历分组列表
        for (GroupEntity<SpecGroupEntity, SpecOptionEntity> group : mGroupList) {
            SpecGroupEntity key = group.getHeader();
            List<SpecOptionEntity> options = group.getChildren();

            // 如果是单选 (!isMultiple) 且没有选中项，默认选中第一个
            if (!key.isMultiple && options != null && !options.isEmpty()) {
                boolean hasSelected = false;
                for (SpecOptionEntity opt : options) {
                    if (opt.isSelected) {
                        hasSelected = true;
                        break;
                    }
                }
                if (!hasSelected) {
                    options.get(0).setSelected(true);
                }
            }
        }
    }

    // 更新价格和描述 UI
    private void updateUI() {
        BigDecimal total = mDrink.getPrice();
        StringBuilder summary = new StringBuilder("已选：");

        if (mGroupList != null) {
            // 遍历分组数据来计算
            for (GroupEntity<SpecGroupEntity, SpecOptionEntity> group : mGroupList) {
                for (SpecOptionEntity opt : group.getChildren()) {
                    if (opt.isSelected) {
                        if (opt.price != null) {
                            total = total.add(opt.price);
                        }
                        summary.append(opt.optionName).append(" ");
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