package com.leben.merchant.ui.dialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.leben.base.widget.dialog.BaseBottomSheetDialog;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.common.model.bean.GroupEntity;
import com.leben.common.model.bean.SpecGroupEntity;
import com.leben.common.model.bean.SpecOptionEntity;
import com.leben.merchant.R;
import com.leben.merchant.ui.adapter.SpecGroupAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DrinkSpecDialog extends BaseBottomSheetDialog {

    private DrinkEntity mDrink;
    private List<GroupEntity<SpecGroupEntity, SpecOptionEntity>> mAllSystemGroups; // 系统的全量规格字典
    private SpecGroupAdapter mAdapter;

    private OnSpecsSavedListener mListener;

    public interface OnSpecsSavedListener {
        void onSpecsSaved(List<SpecOptionEntity> selectedOptions);
    }

    /**
     * 实例化 Dialog - 【编辑商品】模式
     */
    public static DrinkSpecDialog newInstance(DrinkEntity drink, List<GroupEntity<SpecGroupEntity, SpecOptionEntity>> allSystemGroups) {
        DrinkSpecDialog dialog = new DrinkSpecDialog();
        Bundle args = new Bundle();
        args.putSerializable("data_drink", drink);
        args.putSerializable("data_all_specs", (Serializable) allSystemGroups);
        dialog.setArguments(args);
        return dialog;
    }

    /**
     * 实例化 Dialog - 【添加商品】模式 (重载方法)
     */
    public static DrinkSpecDialog newInstance(List<GroupEntity<SpecGroupEntity, SpecOptionEntity>> allSystemGroups) {
        DrinkSpecDialog dialog = new DrinkSpecDialog();
        Bundle args = new Bundle();
        args.putSerializable("data_all_specs", (Serializable) allSystemGroups);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_dialog_drink_spec;
    }

    @Override
    protected void initView(View root) {
        if (getArguments() == null) return;
        mDrink = (DrinkEntity) getArguments().getSerializable("data_drink");
        mAllSystemGroups = (List<GroupEntity<SpecGroupEntity, SpecOptionEntity>>) getArguments().getSerializable("data_all_specs");

        if (mAllSystemGroups == null) {
            mAllSystemGroups = new ArrayList<>();
        }

        // 1. 初始化回显逻辑 (核心差异点)
        initEchoSelection();

        ImageView ivClose = root.findViewById(R.id.iv_close);
        Button btnSave = root.findViewById(R.id.btn_add_cart);
        RecyclerView rv = root.findViewById(R.id.rv_specs);

        ivClose.setOnClickListener(v -> dismiss());

        // 2. 设置 Adapter
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        // 这里的 Adapter 需要支持“多选”样式 (CheckBox)，且不需要像 C 端那样互斥
        mAdapter = new SpecGroupAdapter(mAllSystemGroups);
        rv.setAdapter(mAdapter);

        // 3. 点击保存按钮
        btnSave.setOnClickListener(v -> {
            if (mListener != null) {
                List<SpecOptionEntity> finalSelected = new ArrayList<>();
                for (GroupEntity<SpecGroupEntity, SpecOptionEntity> group : mAllSystemGroups) {
                    for (SpecOptionEntity opt : group.getChildren()) {
                        if (opt.isSelected) {
                            finalSelected.add(opt);
                        }
                    }
                }
                mListener.onSpecsSaved(finalSelected);
                dismiss();
            }
        });
    }

    /**
     * 回显逻辑：对比商品已有的 specs，把全量字典里对应的项设为 isSelected = true
     */
    private void initEchoSelection() {
        if (mAllSystemGroups == null) {
            return;
        }

        // 如果 mDrink 为空，或者没有规格，说明是“添加商品”模式，强制全部设为未选中
        if (mDrink == null || mDrink.getSpecs() == null || mDrink.getSpecs().isEmpty()) {
            for (GroupEntity<SpecGroupEntity, SpecOptionEntity> group : mAllSystemGroups) {
                if (group.getChildren() != null) {
                    for (SpecOptionEntity opt : group.getChildren()) {
                        opt.setSelected(false);
                    }
                }
            }
            return;
        }

        // 把当前商品已有的 OptionId 存入 Set，方便快速查找
        Set<Long> existingOptionIds = new HashSet<>();
        for (SpecOptionEntity existingOpt : mDrink.getSpecs()) {
            existingOptionIds.add(existingOpt.getOptionId());
        }

        // 遍历全量规格字典，如果 ID 在 Set 中，则打钩；否则不勾
        for (GroupEntity<SpecGroupEntity, SpecOptionEntity> group : mAllSystemGroups) {
            if (group.getChildren() != null) {
                for (SpecOptionEntity opt : group.getChildren()) {
                    opt.setSelected(existingOptionIds.contains(opt.getOptionId()));
                }
            }
        }
    }

    public void setListener(OnSpecsSavedListener listener) {
        this.mListener = listener;
    }
}