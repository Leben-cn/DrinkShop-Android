package com.leben.merchant.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import com.alibaba.android.arouter.launcher.ARouter;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.base.util.LogUtils;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.merchant.R;
import com.leben.merchant.constant.MerchantConstant;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by youjiahui on 2026/2/17.
 */
public class DrinkAdapter extends BaseRecyclerAdapter<DrinkEntity> {

    public DrinkAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.merchant_item_drink;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void bindData(BaseViewHolder holder, DrinkEntity data, int position) {
        DecimalFormat df=new DecimalFormat("#.##");
        holder.setImageUrl(R.id.iv_drink_img,data.getImg())
                .setText(R.id.tv_drink_name,data.getName())
                .setText(R.id.tv_rating,data.getMark()+"分")
                .setText(R.id.tv_edit,"编辑");
        if (data.getSpecs().isEmpty()) {
            holder.setText(R.id.tv_drink_price,"￥"+df.format(data.getPrice()));
        }else{
            holder.setText(R.id.tv_drink_price,"￥"+df.format(data.getPrice())+" 起");
        }

        RxView.clicks(holder.getView(R.id.tv_edit))
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    ARouter.getInstance()
                            .build(MerchantConstant.Router.DRINK_EDIT)
                            .withString("TAG","DRINK_ADAPTER")
                            .withSerializable("drink", data)
                            .navigation();
                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

    }
}
