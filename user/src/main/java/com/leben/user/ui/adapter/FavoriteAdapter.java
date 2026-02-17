package com.leben.user.ui.adapter;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.common.model.bean.DrinkSimpleEntity;
import com.leben.common.model.bean.ShopEntity;
import com.leben.common.ui.adapter.PreviewDrinksAdapter;
import com.leben.user.R;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class FavoriteAdapter extends BaseRecyclerAdapter<ShopEntity> {

    private final RecyclerView.RecycledViewPool mViewPool = new RecyclerView.RecycledViewPool();

    private final PublishSubject<ShopClickEvent> clickSubject = PublishSubject.create();

    public static class ShopClickEvent {
        public ShopEntity shop;
        public DrinkSimpleEntity drink; // 如果是点击店铺整体，这个为 null

        public ShopClickEvent(ShopEntity shop, DrinkSimpleEntity drink) {
            this.shop = shop;
            this.drink = drink;
        }
    }

    // 暴露 Observable 给 Fragment
    public Observable<ShopClickEvent> observeClicks() {
        return clickSubject;
    }

    public FavoriteAdapter(Context context) {
        super(context);
        mViewPool.setMaxRecycledViews(0, 20);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.user_item_favorite;
    }

    @Override
    protected void bindData(BaseViewHolder holder, ShopEntity data, int position) {
        holder.setText(R.id.tv_shop_name, data.getName())
                .setImageUrl(R.id.iv_shop_logo, data.getImg(),R.drawable.pic_no_shop)
                .setText(R.id.tv_distance, data.getDistance())
                .setText(R.id.tv_score,data.getRating().toString())
                .setText(R.id.tv_sales,"月售"+data.getTotalSales().toString())
                .setText(R.id.tv_delivery_info,"起送￥"+data.getMinOrder().toString());

        // 2. 使用 RxView 监听【店铺整体】点击
        RxView.clicks(holder.itemView)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .map(o -> new ShopClickEvent(data, null))
                .subscribe(clickSubject);

        RecyclerView rvProducts=holder.getView(R.id.rv_horizontal_products);

        // 【优化1】设置 LayoutManager 并开启预加载
        LinearLayoutManager layoutManager = new LinearLayoutManager(rvProducts.getContext(), RecyclerView.HORIZONTAL, false);
        layoutManager.setInitialPrefetchItemCount(4); // 预加载4个，提升滑动性能
        rvProducts.setLayoutManager(layoutManager);

        // 【优化2】关键！设置共用缓存池
        rvProducts.setRecycledViewPool(mViewPool);

        //设置 innerAdapter
        List<DrinkSimpleEntity> previewList = data.getDrinks();
        if (previewList != null) {
            PreviewDrinksAdapter previewDrinksAdapter = new PreviewDrinksAdapter(previewList);
            // 3. 在这里拦截内部 Adapter 的点击，转给 clickSubject
            // 假设 PreviewDrinksAdapter 内部也用了 RxView 或者简单的回调
            previewDrinksAdapter.setOnItemClickListener(drink -> {
                clickSubject.onNext(new ShopClickEvent(data, drink));
            });
            rvProducts.setAdapter(previewDrinksAdapter);
        }

        rvProducts.setHasFixedSize(true);
    }
}
