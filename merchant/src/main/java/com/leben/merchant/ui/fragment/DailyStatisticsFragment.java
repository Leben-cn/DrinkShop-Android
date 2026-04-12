package com.leben.merchant.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.annotation.InjectPresenter;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.fragment.BaseRecyclerFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.widget.spinner.DateSpinner;
import com.leben.base.widget.spinner.OnSpinnerSelectedListener;
import com.leben.common.model.bean.OrderEntity;
import com.leben.merchant.R;
import com.leben.merchant.contract.GetOrderByDateContract;
import com.leben.merchant.presenter.GetOrderByDatePresenter;
import com.leben.merchant.ui.adapter.BillAdapter;
import com.leben.merchant.util.OrderUtils;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class DailyStatisticsFragment extends BaseRecyclerFragment<OrderEntity>  implements GetOrderByDateContract.View {

    private LinearLayout llDailyDatePicker;
    private TextView tvCurrentDailyDate;
    private String dateStr;
    private TextView tvTotalCount;
    private TextView tvTotalAmount;

    @InjectPresenter
    GetOrderByDatePresenter getOrderByDatePresenter;

    @Override
    protected BaseRecyclerAdapter<OrderEntity> createAdapter() {
        return new BillAdapter(requireContext());
    }

    public static DailyStatisticsFragment newInstance() {
        DailyStatisticsFragment fragment = new DailyStatisticsFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_frag_daily;
    }

    @Override
    public void initView(View root) {
        setDefaultSpace(8);
        super.initView(root);
        llDailyDatePicker=root.findViewById(R.id.ll_daily_date_picker);
        tvCurrentDailyDate=root.findViewById(R.id.tv_current_daily_date);
        tvTotalCount=root.findViewById(R.id.tv_daily_total_count);
        tvTotalAmount=root.findViewById(R.id.tv_daily_total_amount);
    }

    @Override
    public void onRefresh() {
        getOrderByDatePresenter.getOrderByDate(dateStr);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(llDailyDatePicker)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data->{
                    DateSpinner dateSpinner = new DateSpinner(getContext(), 3);
                    dateSpinner.setOnSelectedListener(new OnSpinnerSelectedListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSelected(int year, int month, int day) {
                            tvCurrentDailyDate.setText(year+"年"+month+"月"+day+"日");
                            dateStr=year+"."+month+"."+day;
                            onRefresh();
                        }
                    });
                    dateSpinner.show();
                },throwable -> {
                    LogUtils.error("点击事件错误："+throwable.getMessage());
                });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initData() {
        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 月份从0开始，需要+1
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        dateStr=year+"."+month+"."+day;
        if (tvCurrentDailyDate != null) {
            tvCurrentDailyDate.setText(year + "年" + month + "月" + day + "日");
        }
        autoRefresh();
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.background_green_500;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onGetOrderByDateSuccess(List<OrderEntity> data) {
        Object[] stats = OrderUtils.getStats(data);
        tvTotalCount.setText("当日收入"+stats[0]+"笔，合计");
        tvTotalAmount.setText(String.valueOf(stats[1]));
        refreshListSuccess(data);
    }

    @Override
    public void onGetOrderByDateFailed(String errorMsg) {
        refreshListFailed("获取账单失败");
        LogUtils.error("获取账单失败："+errorMsg);
    }
}
