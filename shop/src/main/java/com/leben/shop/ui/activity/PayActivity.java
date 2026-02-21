package com.leben.shop.ui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseActivity;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.model.bean.AddressEntity;
import com.leben.common.model.event.SelectAddressEvent;
import com.leben.shop.contract.SubmitOrderContract;
import com.leben.shop.controller.CartController;
import com.leben.shop.R;
import com.leben.shop.constant.ShopConstant;
import com.leben.common.model.bean.OrderEntity;
import com.leben.common.model.bean.ShopEntity;
import com.leben.shop.presenter.SubmitOrderPresenter;
import com.leben.shop.ui.adapter.PayAdapter;
import com.leben.shop.ui.dialog.RemarkDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = ShopConstant.Router.SHOP_PAY)
public class PayActivity extends BaseActivity implements SubmitOrderContract.View {

    private ShopEntity mShop;
    private RecyclerView mRvOrderList;
    private TextView mTvAddressDetail, mTvUserInfo;
    private TextView mTvTotalPrice, mTvPackingFee, mTvDeliveryFee, mTvShopName;
    private PayAdapter mAdapter;
    private ConstraintLayout clAddressCard;
    private TitleBar titleBar;
    private ImageView ivRemark;
    private OrderEntity orderEntity;
    private RemarkDialog dialog;
    private TextView tvPay;

    @InjectPresenter
    SubmitOrderPresenter submitOrderPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.shop_ac_pay;
    }

    @Override
    public void onInit() {
        super.onInit();
        Object obj = getIntent().getSerializableExtra("shop");
        if (obj instanceof ShopEntity) {
            mShop = (ShopEntity) obj;
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
        // 绑定控件
        mRvOrderList = findViewById(R.id.rv_order_list);
        mTvTotalPrice = findViewById(R.id.tv_total_price);
        mTvPackingFee = findViewById(R.id.tv_packing_fee);
        mTvDeliveryFee = findViewById(R.id.tv_delivery_fee);
        mTvShopName = findViewById(R.id.tv_shop_name);
        mTvAddressDetail = findViewById(R.id.tv_address_detail);
        mTvUserInfo = findViewById(R.id.tv_user_info);
        clAddressCard=findViewById(R.id.cl_address_card);
        titleBar=findViewById(R.id.title_bar);
        ivRemark=findViewById(R.id.iv_remark_arrow);
        tvPay=findViewById(R.id.tv_pay_now);

        mRvOrderList.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new PayAdapter(this);
        mRvOrderList.setAdapter(mAdapter);
        mAdapter.setList(CartController.getInstance().getCartList());

        if(titleBar!=null){
            titleBar.setTitle("付款页");
        }

        if (orderEntity == null) {
            orderEntity = new OrderEntity();  // 或者从其他地方获取
        }

        dialog=new RemarkDialog();

        // 填充数据
        fillData();
    }

    @SuppressLint("SetTextI18n")
    private void fillData() {
        if (mShop != null) {
            mTvShopName.setText(mShop.getName());

            DecimalFormat df=new DecimalFormat("#.##");

            // 1. 配送费
            BigDecimal deliveryFee = mShop.getDeliveryFee() == null ? BigDecimal.ZERO : mShop.getDeliveryFee();
            mTvDeliveryFee.setText("¥ " + df.format(deliveryFee));

            // 2. 打包费
            BigDecimal packingFee = CartController.getInstance().getTotalPackingFee();
            mTvPackingFee.setText("¥ " + df.format(packingFee));

            // 3. 商品总价 (纯商品)
            BigDecimal goodsPrice = CartController.getInstance().getTotalPrice();

            // 4. 计算合计：商品 + 打包 + 配送
            BigDecimal total = goodsPrice.add(packingFee).add(deliveryFee);
            //mTvTotalPrice.setText("¥" + total.setScale(2, RoundingMode.HALF_UP));
            mTvTotalPrice.setText("¥ " + df.format(total));
            orderEntity.setDeliveryFee(mShop.getDeliveryFee());
            orderEntity.setShopId(mShop.getId());
            orderEntity.setShopName(mShop.getName());
            orderEntity.setShopLogo(mShop.getImg());
            orderEntity.setPayAmount(total);
            orderEntity.setGoodsTotalPrice(goodsPrice);
            orderEntity.setPackingFee(packingFee);
            orderEntity.setItems(CartController.getInstance().getOrderItemsForSubmit());
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(clAddressCard)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(unit->{
                    ARouter.getInstance()
                            .build(ShopConstant.Router.ADDRESS)
                            .withBoolean("isSelectMode", true)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
        RxView.clicks(ivRemark)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .subscribe(unit->{
                    dialog.setOnConfirmListener(new RemarkDialog.OnConfirmListener() {
                        @Override
                        public void onConfirm(String content) {
                            orderEntity.setRemark(content);
                        }
                    });
                    dialog.show(getSupportFragmentManager(), "dialog_remark");
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(tvPay)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .filter(unit->{
                    if(orderEntity.getReceiverName()==null){
                        ToastUtils.show(this, "请选择收货地址");
                        return false;
                    }
                    if (orderEntity.getItems() == null || orderEntity.getItems().isEmpty()) {
                        ToastUtils.show(this, "购物车是空的，快去选购吧");
                        return false;
                    }

                    // --- 3. 校验业务逻辑 (防御性检查) ---
                    if (orderEntity.getShopId() == null) {
                        ToastUtils.show(this, "店铺信息异常，请重新进入");
                        return false;
                    }

                    // 校验金额 (防止金额为null或者负数)
                    if (orderEntity.getPayAmount() == null || orderEntity.getPayAmount().doubleValue() < 0) {
                        ToastUtils.show(this, "订单金额异常");
                        return false;
                    }

                    return true;
                })
                .flatMap(unit -> Observable.<Boolean>create(emitter -> {
                    CommonDialog dialog = CommonDialog.newInstance()
                            .setTitle("付款")
                            .setContent("确定要付款吗？");
                    dialog.setOnCancelListener(d -> {
                        emitter.onNext(false); // 发射 false 表示取消
                        emitter.onComplete();
                        d.dismiss();
                    });

                    dialog.setOnConfirmListener(d -> {
                        emitter.onNext(true); // 发射 true 表示确定
                        emitter.onComplete();
                        d.dismiss();
                    });

                    dialog.show(getSupportFragmentManager(), "dialog_logout");
                }))
                .filter(unit -> unit) // 只处理 true（确定）的情况
                .subscribe(unit->{
                    submitOrderPresenter.submitOrder(orderEntity);
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return com.leben.base.R.color.white;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    /**
     * 地址信息回填
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectAddressEvent(SelectAddressEvent event) {
        if (event != null && event.getAddress() != null) {
            AddressEntity address = event.getAddress();

            mTvUserInfo.setText(address.getContactName()+" "+address.getContactPhone());
            String filteredAddress = filterAddress(address.getAddressPoi() + "-" + address.getAddressDetail());
            mTvAddressDetail.setText(filteredAddress);
            orderEntity.setReceiverName(address.getContactName());
            orderEntity.setReceiverPhone(address.getContactPhone());
            orderEntity.setReceiverAddress(address.getAddressPoi() + address.getAddressDetail());

        }
    }

    // 过滤地址中的省市区信息
    private String filterAddress(String fullAddress) {
        if (fullAddress == null || fullAddress.isEmpty()) {
            return "";
        }

        // 匹配 "XX省XX市XX区" 或 "XX市XX区" 格式
        String regex = "^(.*?省)?(.*?市)?(.*?区)?";
        return fullAddress.replaceFirst(regex, "").trim();
    }

    @Override
    public void onSubmitOrderSuccess(Long data) {
        ToastUtils.show(this,"下单成功");
        finish();
    }

    @Override
    public void onSubmitOrderFailed(String errorMsg) {
        ToastUtils.show(this,"下单失败");
        LogUtils.error("下单失败："+errorMsg);
        finish();
    }
}