package com.leben.shop.ui.activity;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.activity.BaseTabActivity;
import com.leben.base.util.ConvertUtils;
import com.leben.base.util.LogUtils;
import com.leben.base.util.ToastUtils;
import com.leben.base.widget.titleBar.TitleBar;
import com.leben.common.LocationManager;
import com.leben.shop.contract.CheckFavoriteContract;
import com.leben.shop.contract.GetShopInfoContract;
import com.leben.shop.contract.ToggleFavoriteContract;
import com.leben.shop.controller.CartController;
import com.leben.shop.R;
import com.leben.shop.constant.ShopConstant;
import com.leben.common.model.bean.ShopEntity;
import com.leben.shop.model.event.CartEvent;
import com.leben.shop.presenter.CheckFavoritePresenter;
import com.leben.shop.presenter.GetShopInfoPresenter;
import com.leben.shop.presenter.ToggleFavoritePresenter;
import com.leben.shop.ui.dialog.CartDialog;
import com.leben.shop.ui.fragment.ShopCommentFragment;
import com.leben.shop.ui.fragment.ShopMenuFragment;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = ShopConstant.Router.SHOP)
public class ShopActivity extends BaseTabActivity implements CheckFavoriteContract.View,
        GetShopInfoContract.View , ToggleFavoriteContract.View {
    //为什么购物车要写在这？因为 CoordinatorLayout 会把 fragment 向下挤压，导致需要上滑才能看见购物车,在外层 Activity 就不会有这个问题
    private View mCartView;
    private TextView badgeCount;
    private TextView totalPrice;
    private TextView submit;
    private TextView deliveryFee;
    private ShopEntity mShop;
    private Long shopId;
    private RelativeLayout cartIcon;
    private TitleBar titleBar;
    private ImageView ivCollect,ivMessage;
    private boolean isCollected = false;

    private ImageView shopLogo;
    private TextView shopName;
    private TextView shopDes;
    private TextView shopSales;
    private TextView shopDistance;

    @InjectPresenter
    CheckFavoritePresenter checkFavoritePresenter;

    @InjectPresenter
    ToggleFavoritePresenter toggleFavoritePresenter;

    @InjectPresenter
    GetShopInfoPresenter getShopInfoPresenter;

    @Override
    public void onInit() {
        super.onInit();

        EventBus.getDefault().register(this);

        shopId= getIntent().getLongExtra("shopId",-1);
        // 进店时检查：如果是新店，控制器内部会自动清空上一家店的数据
        CartController.getInstance().checkShopId(shopId);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.shop_ac_shop;
    }

    @Override
    public void initView() {
        super.initView();
        mCartView = findViewById(R.id.rl_bottom_cart);
        badgeCount=findViewById(R.id.tv_badge_count);
        totalPrice=findViewById(R.id.tv_total_price);
        submit=findViewById(R.id.tv_submit);
        deliveryFee=findViewById(R.id.tv_delivery_fee);
        cartIcon=findViewById(R.id.rl_cart_icon_container);
        titleBar=findViewById(R.id.title_bar);

        shopLogo=findViewById(R.id.iv_shop_logo);
        shopName=findViewById(R.id.tv_shop_name);
        shopDes=findViewById(R.id.tv_shop_des);
        shopSales=findViewById(R.id.tv_shop_sales);
        shopDistance=findViewById(R.id.tv_shop_distance);

        // 初始化时手动获取一次状态，防止页面刚进来是空的
        updateCartUI(CartController.getInstance().getTotalQuantity(),
                CartController.getInstance().getTotalPrice());

        if (titleBar != null) {
            ivCollect = new ImageView(this);
            ivCollect.setImageResource(R.drawable.ic_collect_no);
            ivMessage=new ImageView(this);
            ivMessage.setImageResource(R.drawable.ic_shop_message);
            titleBar.addRightView(ivCollect);
            titleBar.addRightView(ivMessage);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        // 2. 监听 ViewPager 的页面切换
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handleCartVisibility(position);
            }
        });
        RxView.clicks(cartIcon) // 这里通常绑定整个购物车区域，或者是你的 cartIcon
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()) // 确保在主线程显示 UI
                .subscribe(unit -> {

                    // 1. 防御性判断：购物车没东西就不弹窗
                    if (CartController.getInstance().getTotalQuantity() <= 0) {
                        ToastUtils.show(this,"购物车空空如也");
                        return;
                    }

                    // 2. 正确的调用方式
                    // newInstance() 是静态方法，直接用类名调用
                    // show() 需要传入 FragmentManager
                    CartDialog.newInstance()
                            .show(getSupportFragmentManager(), "cart_dialog");

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(submit)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()) // 确保在主线程显示 UI
                .subscribe(unit->{
                    ARouter.getInstance()
                            .build(ShopConstant.Router.SHOP_PAY)
                            .withSerializable("shop",mShop)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(ivCollect)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(unit -> {
                    // 1. 切换状态变量
                    isCollected = !isCollected;

                    // 2. 更新 UI
                    updateCollectIcon();

                    toggleFavoritePresenter.toggleFavorite(shopId);

                }, throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
    }

    @Override
    public void initData() {
        Double latitude = LocationManager.getInstance().getLatitude();
        Double longitude = LocationManager.getInstance().getLongitude();
        checkFavoritePresenter.checkFavorite(shopId);
        getShopInfoPresenter.getShopInfo(shopId,latitude,longitude);

    }

    @Override
    protected List<Fragment> getFragments() {
        long drinkId = getIntent().getLongExtra("drinkId", 0);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(ShopMenuFragment.newInstance(shopId,drinkId));
        fragmentList.add(ShopCommentFragment.newInstance(shopId));
        return fragmentList;
    }

    @Override
    protected List<String> getTitles() {
        List<String> list = new ArrayList<>();
        list.add("商品");
        list.add("评价");
        return list;
    }

    /**
     * 根据当前 Tab 控制购物车显示/隐藏
     */
    private void handleCartVisibility(int position) {
        if (mCartView == null) return;

        // 获取 CoordinatorLayout (或者是你的 ViewPager) 的布局参数
        View contentContainer = findViewById(R.id.coordinator); // 确保你在xml里给CoordinatorLayout加了这个id
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentContainer.getLayoutParams();

        if (position == 0) {
            // === 商品页 ===
            mCartView.setVisibility(View.VISIBLE);
            // 恢复底部间距，防止被购物车遮挡
            params.bottomMargin = ConvertUtils.toPx(this,50);
        } else {
            // === 评价页 ===
            mCartView.setVisibility(View.GONE);
            // 去掉底部间距，让评价列表能沉底
            params.bottomMargin = 0;
        }
        contentContainer.setLayoutParams(params);
    }

    // 3. 接收事件 (必须是 public void)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartEvent(CartEvent event) {
        updateCartUI(event.getTotalQuantity(), event.getTotalPrice());
    }

    @SuppressLint("SetTextI18n")
    private void updateCartUI(int count, BigDecimal price){

        if (mCartView == null || badgeCount == null || totalPrice == null || submit == null) {
            return;
        }

        mCartView.setVisibility(View.VISIBLE);

        if (count > 0) {

            // --------------- 状态 A：购物车有商品 ---------------
            // 1. 角标：显示并设置数字
            badgeCount.setVisibility(View.VISIBLE);
            deliveryFee.setVisibility(View.VISIBLE);
            badgeCount.setText(String.valueOf(count));

            // 设置配送费
            if (mShop != null) {
                deliveryFee.setText(formatDeliveryFee(mShop.getDeliveryFee()));
            }

            // 2. 价格：显示总价
            if (price != null) {
                //获取打包费 (从 Controller 拿)
                BigDecimal packingFee = CartController.getInstance().getTotalPackingFee();
                price = price.add(packingFee);
                totalPrice.setText("¥" + price.setScale(2, RoundingMode.HALF_UP));
                totalPrice.setTextColor(getResources().getColor(android.R.color.white));

                // 3. 检查是否满足起送金额
                if (mShop != null && price.compareTo(mShop.getMinOrder()) < 0) {
                    // 不满足起送金额
                    submit.setBackgroundColor(0xFF555555); // 灰色
                    submit.setTextColor(0xFF999999);
                    BigDecimal needMore = mShop.getMinOrder().subtract(price).setScale(2, RoundingMode.HALF_UP);
                    submit.setText("还差 ¥" + needMore + "起送");
                    submit.setTextSize(14);
                    submit.setEnabled(false);
                } else {
                    // 满足起送金额
                    submit.setBackgroundColor(0xFFF8D347); // 黄色
                    submit.setTextColor(0xFF333333);
                    submit.setText("去结算");
                    submit.setTextSize(16);
                    submit.setEnabled(true);
                }
            }

        } else {
            // 1. 角标：隐藏
            badgeCount.setVisibility(View.GONE);
            deliveryFee.setVisibility(View.GONE);

            // 2. 价格：显示 ¥0.00，灰色
            totalPrice.setText("¥0.00");
            totalPrice.setTextColor(0xFF999999);   // 浅灰色

            // 3. 结算按钮：深灰色背景，不可点击
            submit.setBackgroundColor(0xFF555555); // 深灰色背景
            submit.setTextColor(0xFF999999);       // 浅灰色文字
            submit.setText("未选购");               // 或者显示 "¥0起送"
            submit.setEnabled(false);
            submit.setTextSize(16);// 禁止点击
        }
    }

    @Override
    public void onDestroy() {
        // 1. 必须先注销 EventBus，防止 clear() 发出的通知回调到已经销毁的页面导致崩溃

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        // 不要在这里 clear，因为跳转 PayActivity 时，ShopActivity 还在后台
        // 如果系统回收了 ShopActivity，这里一 clear，PayActivity 里的数据就没了
        // CartController.getInstance().clear();
        super.onDestroy();
    }

    public static String formatDeliveryFee(BigDecimal fee) {
        if (fee == null || fee.compareTo(BigDecimal.ZERO) <= 0) {
            return "免配送费";
        }

        // 去掉末尾的0
        BigDecimal stripped = fee.stripTrailingZeros();
        return "另需配送费￥ " + stripped.toPlainString();
    }

    private void updateCollectIcon() {
        if (isCollected) {
            ivCollect.setImageResource(R.drawable.ic_collect);
        } else {
            ivCollect.setImageResource(R.drawable.ic_collect_no);
        }
    }

    @Override
    public void onCheckFavoriteSuccess(Boolean data) {
        isCollected=data;
        updateCollectIcon();
    }

    @Override
    public void onCheckFavoriteFailed(String errorMsg) {
        ToastUtils.show(this,"检查收藏店铺失败");
        LogUtils.error("检查收藏店铺失败："+errorMsg);
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
    public void onGetShopInfoSuccess(ShopEntity data) {
        mShop=data;
        Glide.with(this).load(mShop.getImg()).into(shopLogo);
        shopName.setText(mShop.getName());
        shopDes.setText(mShop.getDescription());
        shopSales.setText(String.valueOf(mShop.getTotalSales()));
        shopDistance.setText(mShop.getDistance());
    }

    @Override
    public void onGetShopInfoFailed(String errorMsg) {
        ToastUtils.show(this,"获取店铺信息失败");
        LogUtils.error("获取店铺信息失败："+errorMsg);
    }

    @Override
    public void onToggleFavoriteSuccess(Boolean data) {
        if(data){
            ToastUtils.show(this,"收藏成功");
        }else{
            ToastUtils.show(this,"已取消收藏");
        }
    }

    @Override
    public void onToggleFavoriteFailed(String errorMsg) {
        ToastUtils.show(this,"收藏/取消收藏失败");
        LogUtils.error("收藏/取消收藏失败："+errorMsg);
    }
}
