package com.leben.shop.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.fragment.BaseFragment;
import com.leben.base.widget.linkage.LinkageView;
import com.leben.common.LocationManager;
import com.leben.shop.controller.CartController;
import com.leben.shop.R;
import com.leben.shop.constant.ShopConstant;
import com.leben.shop.contract.ShopMenuContract;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.common.model.bean.GroupEntity;
import com.leben.shop.model.bean.LinkageRightEntity;
import com.leben.common.model.bean.ShopCategoriesEntity;
import com.leben.shop.model.event.CartEvent;
import com.leben.shop.presenter.ShopMenuPresenter;
import com.leben.shop.ui.adapter.LinkageLeftAdapter;
import com.leben.shop.ui.adapter.LinkageRightAdapter;
import com.leben.shop.ui.dialog.ProductSpecDialog;
import com.leben.common.util.ListGroupUtils;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;

@Route(path = ShopConstant.Router.SHOP_MENU)
public class ShopMenuFragment extends BaseFragment implements ShopMenuContract.View {

    @InjectPresenter
    ShopMenuPresenter shopMenuPresenter;

    private LinkageView mLinkageView;

    private long mShopId = 0;
    private long mDrinkId = -1;

    private LinkageRightAdapter mRightAdapter;
    private LinkageLeftAdapter mLeftAdapter;
    private List<LinkageRightEntity> rightList = new ArrayList<>();
    private List<ShopCategoriesEntity> leftList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.shop_frag_shop_menu;
    }


    @Override
    protected void initView(View root) {
        mLinkageView = root.findViewById(R.id.lv_user_menu);
        mRightAdapter = new LinkageRightAdapter(rightList);
        mLeftAdapter = new LinkageLeftAdapter(leftList);
    }

    @Override
    public void onStart() {
        super.onStart();
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

    /**
     * 3. 接收购物车变化事件
     * 当购物车在外面（Activity底部栏）或弹窗（Dialog）里发生变化时，这里会收到通知
     */
    @SuppressLint("NotifyDataSetChanged")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCartEvent(CartEvent event) {
        if (mRightAdapter != null) {
            // 4. 刷新列表
            // 这一步会触发 Adapter 的 onBindViewHolder
            // 从而重新调用 CartController.getProductQuantity() 获取最新数量
            mRightAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void initListener() {
        mRightAdapter.setOnGoodsAddListener(drink -> {
            // 这里调用弹窗方法
            ProductSpecDialog dialog = ProductSpecDialog.newInstance(drink);
            //specDesc:大杯 常温 正常糖
            dialog.setListener((entity, selectedOptions, finalPrice, specDesc) -> {
//                if (selectedOptions != null && !selectedOptions.isEmpty()) {
//                    String optionsSummary = selectedOptions.stream()
//                            .map(opt -> opt.getName() + "(¥" + opt.getPrice() + ")")
//                            .collect(Collectors.joining(", "));
//                    LogUtils.info("规格汇总: " + optionsSummary);
//                }
                CartController.getInstance().add(
                        entity,
                        selectedOptions,
                        finalPrice,
                        specDesc
                );

                mRightAdapter.notifyDataSetChanged();
            });
            dialog.show(getChildFragmentManager(), "spec_dialog");
        });
    }

    //定义静态方法实例化 Fragment，并传入参数
    public static ShopMenuFragment newInstance(long shopId,long drinkId) {
        ShopMenuFragment fragment = new ShopMenuFragment();
        Bundle args = new Bundle();
        args.putLong("shopId", shopId);
        args.putLong("drinkId", drinkId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void initData() {
        Double latitude= LocationManager.getInstance().getLatitude();
        Double longitude=LocationManager.getInstance().getLongitude();
        if (getArguments() != null) {
            mShopId = getArguments().getLong("shopId", 0);
            mDrinkId = getArguments().getLong("drinkId", 0);
        }
        if (mShopId != 0) {
            shopMenuPresenter.getShopMenuDrinks(mShopId, latitude, longitude);
        }
    }

    @Override
    public void onGetShopMenuSuccess(List<DrinkEntity> data) {
        if(data==null||data.isEmpty()){
            return;
        }
        //处理扁平化数据
        //既然是按照 ShopCategoriesEntity 对象来分组的（把它作为 Map 的 Key），必须确保 ShopCategoriesEntity 重写了 equals() and hashCode() 方法。
        //否则，即使 ID 相同的两个分类对象，Map 也会认为它们是不同的 Key，导致分组失败（出现重复的分类）。
        List<GroupEntity<ShopCategoriesEntity, DrinkEntity>> groupList= ListGroupUtils.groupList(
                data,
                DrinkEntity::getShopCategories
        );
        List<Integer> headerPositions=new ArrayList<>();

        int currentPosition=0;

        for(GroupEntity<ShopCategoriesEntity, DrinkEntity> group:groupList){
            ShopCategoriesEntity shopCategory=group.getHeader();
            List<DrinkEntity> drinks=group.getChildren();

            leftList.add(shopCategory);
            headerPositions.add(currentPosition);

            // 填充右侧列表（标题）
            rightList.add(new LinkageRightEntity(shopCategory.getName()));
            currentPosition++;

            // 填充右侧列表（内容）
            for(DrinkEntity drink:drinks){
                rightList.add(new LinkageRightEntity(drink));
                currentPosition++;
            }
        }
        mRightAdapter.setNewData(rightList);
        // 初始化 View
        mLinkageView.init(mLeftAdapter, mRightAdapter, headerPositions);

        if (mDrinkId != -1) {
            int targetPosition = -1;

            // 遍历右侧列表 (rightList 包含了 Header 和 Item)
            for (int i = 0; i < rightList.size(); i++) {
                LinkageRightEntity entity = rightList.get(i);

                // 只有当它是普通商品(不是标题)，且ID匹配时
                if (!entity.isHeader() && entity.getDrink() != null && entity.getDrink().getId() == mDrinkId) {
                    targetPosition = i;
                    break;
                }
            }

            // 如果找到了，让 LinkageView 滚过去
            if (targetPosition != -1) {
                // 为了保险，可以用 post 确保 View 测量完成（有时候立即滚动会不准）
                final int pos = targetPosition;
                mLinkageView.post(() -> {
                    mLinkageView.smoothScrollToPositionAtTop(pos);
                });

                // 滚动完后重置 ID，防止刷新数据时又滚一次
                mDrinkId = -1;
            }
        }

    }

    @Override
    public void onGetShopMenuFailed(String errorMsg) {

    }

}
