package com.leben.shop.ui.dialog;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leben.base.widget.dialog.BaseBottomSheetDialog;
import com.leben.shop.controller.CartController;
import com.leben.shop.R;
import com.leben.shop.model.event.CartEvent;
import com.leben.shop.ui.adapter.CartAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.RoundingMode;

public class CartDialog extends BaseBottomSheetDialog {

    private RecyclerView mRecyclerView;
    private CartAdapter mAdapter;
    private TextView packingFee;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_cart_list;
    }

    // 1. 不需要传参数，数据直接找 Controller 要
    public static CartDialog newInstance() {
        return new CartDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 注册 EventBus 以监听加减号的变化
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView(View root) {
        packingFee=root.findViewById(R.id.tv_packing_fee_hint);
        mRecyclerView = root.findViewById(R.id.rv_cart_list);
        View btnClear = root.findViewById(R.id.ll_clear_cart);

        // --- 初始化列表 ---
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new CartAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        // 获取当前购物车所有数据
        mAdapter.setList(CartController.getInstance().getCartList());

        updatePackingFee();

        btnClear.setOnClickListener(v -> {
            CartController.getInstance().clear();
            dismiss();
        });

    }

    // 接收购物车变化事件 (加减商品时触发)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartEvent(CartEvent event) {
        // 1. 如果购物车被清空了 (数量为0)，自动关闭弹窗
        if (event.getTotalQuantity() <= 0) {
            dismiss();
            return;
        }

        // 2. 刷新列表数据 (因为数量变了，或者有条目被移除了)
        if (mAdapter != null) {
            mAdapter.setList(CartController.getInstance().getCartList());
        }

        updatePackingFee();
    }

    @SuppressLint("SetTextI18n")
    private void updatePackingFee() {
        if (packingFee == null) {
            return;
        }
        packingFee.setText("另需打包费 ¥" +
                CartController.getInstance().getTotalPackingFee().setScale(1, RoundingMode.HALF_UP));
    }
}
