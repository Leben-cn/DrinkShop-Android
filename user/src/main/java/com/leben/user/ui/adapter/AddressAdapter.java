package com.leben.user.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.user.R;
import com.leben.common.model.bean.AddressEntity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.internal.operators.observable.ObservableNever;
import io.reactivex.subjects.PublishSubject;

public class AddressAdapter extends BaseRecyclerAdapter<AddressEntity> {

    public AddressAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.user_item_address;
    }

    @SuppressLint("CheckResult")
    @Override
    protected void bindData(BaseViewHolder holder, AddressEntity data, int position) {
        //把市和区去掉，要不然太长了
        String formattedPoi = data.getAddressPoi().replaceAll("^.*?市", "").replaceAll("^.*?区", "");
        holder.setText(R.id.tv_address_full,formattedPoi+data.getAddressDetail())
                .setText(R.id.tv_user_info,data.getContactName()+"  "+data.getContactPhone());

        holder.getView(R.id.iv_edit).setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(v, v.getId(), position, data);
            }
        });
    }
}
