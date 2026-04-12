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

public class AnnualStatisticsFragment extends BaseRecyclerFragment<OrderEntity> implements GetOrderByDateContract.View {

    private LinearLayout llAnnualDatePicker;
    private TextView tvCurrentAnnualDate;
    private String dateStr;
    private TextView tvTotalCount;
    private TextView tvTotalAmount;

    @InjectPresenter
    GetOrderByDatePresenter getOrderByDatePresenter;

    @Override
    protected BaseRecyclerAdapter<OrderEntity> createAdapter() {
        return new BillAdapter(requireContext());
    }

    //定义静态方法实例化 Fragment，并传入参数
    public static AnnualStatisticsFragment newInstance() {
        AnnualStatisticsFragment fragment = new AnnualStatisticsFragment();
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.merchant_frag_annual;
    }

    @Override
    public void initView(View root) {
        setDefaultSpace(8);
        super.initView(root);
        llAnnualDatePicker=root.findViewById(R.id.ll_annual_date_picker);
        tvCurrentAnnualDate=root.findViewById(R.id.tv_current_annual_date);
        tvTotalCount=root.findViewById(R.id.tv_annual_total_count);
        tvTotalAmount=root.findViewById(R.id.tv_annual_total_amount);
    }

    @Override
    public void onRefresh() {
        getOrderByDatePresenter.getOrderByDate(dateStr);
    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(llAnnualDatePicker)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data->{
                    DateSpinner dateSpinner = new DateSpinner(getContext(), 1);
                    dateSpinner.setOnSelectedListener(new OnSpinnerSelectedListener() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSelected(int year, int month, int day) {
                            tvCurrentAnnualDate.setText(year+"年");
                            dateStr=String.valueOf(year);
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

        int currentYear = calendar.get(Calendar.YEAR);
        dateStr=String.valueOf(currentYear);

        if (tvCurrentAnnualDate != null) {
            tvCurrentAnnualDate.setText(currentYear + "年");
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
        tvTotalCount.setText("全年收入"+stats[0]+"笔，合计");
        tvTotalAmount.setText(String.valueOf(stats[1]));
        refreshListSuccess(data);
    }

    @Override
    public void onGetOrderByDateFailed(String errorMsg) {
        refreshListFailed("获取账单失败");
        LogUtils.error("获取账单失败："+errorMsg);
    }
}
