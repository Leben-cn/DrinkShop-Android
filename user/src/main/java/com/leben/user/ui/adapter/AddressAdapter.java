package com.leben.user.ui.adapter;

import android.content.Context;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.holder.BaseViewHolder;
import com.leben.user.R;
import com.leben.common.model.bean.AddressEntity;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class AddressAdapter extends BaseRecyclerAdapter<AddressEntity> {

    // 创建点击事件的 Subject
    private final PublishSubject<AddressEntity> itemClick = PublishSubject.create();

    // 获取点击 Observable
    public Observable<AddressEntity> getItemClickObservable() {
        return itemClick;
    }

    public AddressAdapter(Context context) {
        super(context);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_address;
    }

    @Override
    protected void bindData(BaseViewHolder holder, AddressEntity data, int position) {
        holder.setText(R.id.tv_address_full,data.getAddressPoi()+data.getAddressDetail())
                .setText(R.id.tv_user_info,data.getContactName()+"  "+data.getContactPhone());

        holder.itemView.setOnClickListener(v -> {
            itemClick.onNext(data);
        });
    }
}
