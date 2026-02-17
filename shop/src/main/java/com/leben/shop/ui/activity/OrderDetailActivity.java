package com.leben.shop.ui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.shop.R;
import com.leben.shop.constant.ShopConstant;
import com.leben.common.model.bean.OrderEntity;
import com.leben.shop.ui.adapter.OrderItemAdapter;

@Route(path = ShopConstant.Router.ORDER_DETAIL)
public class OrderDetailActivity extends BaseActivity {

    private OrderEntity orderEntity;
    private RecyclerView rvOrderItemList;
    private OrderItemAdapter orderItemAdapter;
    private TitleBar titleBar;
    private TextView tvAddress;
    private TextView tvName;
    private TextView tvPhone;
    private TextView tvOrderNo;
    private TextView tvOrderTime;
    private TextView tvPackingFee;
    private TextView tvDeliveryFee;
    private TextView tvTotalPrice;
    private TextView tvOrderTitle;
    private TextView tvOrderTip;

    @Override
    protected int getLayoutId() {
        return R.layout.shop_ac_order_detail;
    }

    @Override
    public void onInit() {
        super.onInit();
        Object obj = getIntent().getSerializableExtra("order");
        if (obj instanceof OrderEntity) {
            orderEntity = (OrderEntity) obj;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initView() {
        rvOrderItemList=findViewById(R.id.rv_order_items);
        titleBar=findViewById(R.id.title_bar);
        tvAddress=findViewById(R.id.tv_address_val);
        tvName=findViewById(R.id.tv_name_val);
        tvPhone=findViewById(R.id.tv_phone_val);
        tvOrderNo=findViewById(R.id.tv_order_no_val);
        tvOrderTime=findViewById(R.id.tv_order_time_val);
        tvPackingFee=findViewById(R.id.tv_packing_fee);
        tvDeliveryFee=findViewById(R.id.tv_delivery_fee);
        tvTotalPrice=findViewById(R.id.tv_total_price);
        tvOrderTitle=findViewById(R.id.tv_order_status_title);
        tvOrderTip=findViewById(R.id.tv_order_system_tip);

        if (titleBar != null) {
            titleBar.setTitle("");
        }

        switch (orderEntity.getStatus()){
            case 0:
                tvOrderTitle.setText("待制作");
                tvOrderTip.setText("商品制作中，请耐心等待~");
                break;
            case 1:
                tvOrderTitle.setText("订单已完成");
                tvOrderTip.setText("感谢您对本外卖平台的信任，期待再次光临");
                break;
            case 2:
                tvOrderTitle.setText("订单已取消");
                tvOrderTip.setText("您的订单已取消，期待再次光临");
                break;
        }

        tvAddress.setText(orderEntity.getReceiverAddress());
        tvName.setText(orderEntity.getReceiverName());
        tvPhone.setText(orderEntity.getReceiverPhone());
        tvOrderTime.setText(orderEntity.getCreateTime());
        tvOrderNo.setText(orderEntity.getOrderNo());
        tvPackingFee.setText("￥"+orderEntity.getPackingFee());
        tvDeliveryFee.setText("￥"+orderEntity.getDeliveryFee());
        tvTotalPrice.setText("￥"+orderEntity.getPayAmount());

        rvOrderItemList.setLayoutManager(new LinearLayoutManager(this));

        orderItemAdapter = new OrderItemAdapter(this);
        rvOrderItemList.setAdapter(orderItemAdapter);
        orderItemAdapter.setList(orderEntity.getItems());
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }
}
