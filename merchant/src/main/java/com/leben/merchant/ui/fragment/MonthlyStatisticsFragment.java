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

public class MonthlyStatisticsFragment extends BaseRecyclerFragment<OrderEntity> implements GetOrderByDateContract.View {

    private LinearLayout llMonthlyDatePicker;
    private TextView tvCurrentMonthlyDate;
    private String dateStr;
    private TextView tvTotalCount;
    private TextView tvTotalAmount;

    @InjectPresenter
    GetOrderByDatePresenter getOrderByDatePresenter;

    @Override
    protected BaseRecyclerAdapter<OrderEntity> createAdapter() {
        return new BillAdapter(requireContext());
    }

    public static MonthlyStatisticsFragment newInstance() {
        MonthlyStatisticsFragment fragment = new MonthlyStatisticsFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_frag_monthly;
    }

    @Override
    public void initView(View root) {
        setDefaultSpace(8);
        super.initView(root);
        llMonthlyDatePicker=root.findViewById(R.id.ll_monthly_date_picker);
        tvCurrentMonthlyDate=root.findViewById(R.id.tv_current_monthly_date);
        tvTotalCount=root.findViewById(R.id.tv_monthly_total_count);
        tvTotalAmount=root.findViewById(R.id.tv_monthly_total_amount);
    }

    @Override
    public void onRefresh() {
        getOrderByDatePresenter.getOrderByDate(dateStr);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(llMonthlyDatePicker)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data->{
                    DateSpinner dateSpinner = new DateSpinner(getContext(), 2);
                    dateSpinner.setOnSelectedListener(new OnSpinnerSelectedListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSelected(int year, int month, int day) {
                            tvCurrentMonthlyDate.setText(year+"年"+month+"月");
                            dateStr=year+"."+month;
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
        dateStr=year+"."+month;
        if (tvCurrentMonthlyDate != null) {
            tvCurrentMonthlyDate.setText(year + "年" + month + "月");
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
        tvTotalCount.setText("共收入"+stats[0]+"笔，合计");
        tvTotalAmount.setText(String.valueOf(stats[1]));
        refreshListSuccess(data);
    }

    @Override
    public void onGetOrderByDateFailed(String errorMsg) {
        refreshListFailed("获取账单失败");
        LogUtils.error("获取账单失败："+errorMsg);
    }
}
