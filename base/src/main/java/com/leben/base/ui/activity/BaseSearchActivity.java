package com.leben.base.ui.activity;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.leben.base.R;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.HistoryAdapter;
import com.leben.base.util.SharedPreferencesUtils;
import com.leben.base.widget.dialog.CommonDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 搜索通用基类
 */
public abstract class BaseSearchActivity<T> extends BaseRecyclerActivity<T> {

    protected EditText etSearch;
    protected ImageView ivClear;
    protected View mSearchResultLayout; // 搜索结果容器(SwipeRefreshLayout)

    protected View mHistoryLayout;
    protected RecyclerView mRvHistory;
    protected View mDeleteHistory;
    protected HistoryAdapter historyAdapter;

    protected String mCurrentKeyword = "";

    @Override
    protected int getLayoutId() {
        return R.layout.ac_base_search;
    }

    @Override
    public void initView() {
        super.initView(); // 父类会初始化 mRecyclerView, mAdapter, mStateController 等

        // 1. 初始化搜索栏控件
        etSearch = findViewById(R.id.et_search);
        ivClear = findViewById(R.id.iv_clear);
        mSearchResultLayout = findViewById(R.id.swipeRefresh);

        // 2. 初始化历史记录模块
        initHistoryView();

        // 3. 设置列表点击事件 (父类 mAdapter 已经初始化好了)
        // 3. 设置列表点击事件
        if (mAdapter != null) {
            mAdapter.setOnItemClickListener((view, viewId, position, entity) -> {
                // 这里的 entity 类型就是 T
                onSearchResultClick(entity, position);
            });
        }
    }

    private void initHistoryView() {
        mHistoryLayout = findViewById(R.id.nsv_history);
        mRvHistory = findViewById(R.id.rv_history);
        mDeleteHistory = findViewById(R.id.iv_delete_history);

        // 使用 Flexbox 布局
        FlexboxLayoutManager flexboxManager = new FlexboxLayoutManager(this);
        flexboxManager.setFlexDirection(FlexDirection.ROW);
        flexboxManager.setFlexWrap(FlexWrap.WRAP);
        flexboxManager.setJustifyContent(JustifyContent.FLEX_START);
        mRvHistory.setLayoutManager(flexboxManager);

        historyAdapter = new HistoryAdapter(this);
        mRvHistory.setAdapter(historyAdapter);
    }

    @Override
    public void initListener() {

        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        ivClear.setOnClickListener(v -> {
            etSearch.setText("");
            showKeyboard(etSearch);
            mCurrentKeyword = "";
            showHistoryView();
        });

        mDeleteHistory.setOnClickListener(v -> {
            CommonDialog dialog = new CommonDialog();
            dialog.setContent("确认删除历史记录吗？");
            dialog.setOnConfirmListener(result -> {
                clearSearchHistory();
                loadAndShowHistory();
                dialog.dismiss();
            });
            dialog.setOnCancelListener(result -> dialog.dismiss());
            dialog.show(getSupportFragmentManager(), "dialog_history");
        });

        // 搜索框文本监听
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                boolean hasContent = s.length() > 0;
                ivClear.setVisibility(hasContent ? View.VISIBLE : View.GONE);
                if (!hasContent) {
                    showHistoryView();
                }
            }
        });

        // 软键盘搜索键
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // 右侧搜索按钮
        View btnSearch = findViewById(R.id.tv_action_search);
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> performSearch());
        }

        // 历史记录点击
        historyAdapter.setOnItemClickListener((view, viewId, position, entity) -> {
            // 这里的 entity 就会正确推断为 String
            if (TextUtils.isEmpty(entity)) return;

            hideKeyboard();
            hideHistoryView();
            etSearch.setText(entity);
            etSearch.setSelection(entity.length());

            mCurrentKeyword = entity;
            autoRefresh();
        });
    }

    @Override
    public void initData() {
        // 延时弹出键盘
        etSearch.postDelayed(() -> showKeyboard(etSearch), 200);
        loadAndShowHistory();
    }

    /**
     * 执行搜索
     */
    protected void performSearch() {
        String keyword = etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) return;

        hideKeyboard();
        hideHistoryView();

        mCurrentKeyword = keyword;
        saveSearchHistory(keyword);

        autoRefresh();
    }

    @Override
    public void onRefresh() {
        if (TextUtils.isEmpty(mCurrentKeyword)) {
            refreshComplete();
            return;
        }
        doSearch(mCurrentKeyword);
    }

    // 重写父类的 onLoadMore，桥接到 doLoadMore
    @Override
    public void onLoadMore() {
        doLoadMore(mCurrentKeyword);
    }

    /**
     * 子类实现具体搜索逻辑
     */
    protected abstract void doSearch(String keyword);

    /**
     * 子类实现加载更多逻辑
     */
    protected abstract void doLoadMore(String keyword);

    private void saveSearchHistory(String keyword) {
        String key = getHistoryKey();
        String oldHistory = SharedPreferencesUtils.getParam(this, key, "");
        List<String> historyList = new ArrayList<>();
        if (!TextUtils.isEmpty(oldHistory)) {
            historyList.addAll(Arrays.asList(oldHistory.split(",")));
        }

        for (int i = historyList.size() - 1; i >= 0; i--) {
            if (historyList.get(i).equals(keyword)) historyList.remove(i);
        }
        historyList.add(0, keyword);
        if (historyList.size() > 10) historyList = historyList.subList(0, 10);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < historyList.size(); i++) {
            sb.append(historyList.get(i));
            if (i < historyList.size() - 1) sb.append(",");
        }
        SharedPreferencesUtils.setParam(this, key, sb.toString());
    }

    protected List<String> getSearchHistory() {
        String key = getHistoryKey();
        String oldHistory = SharedPreferencesUtils.getParam(this, key, "");
        if (TextUtils.isEmpty(oldHistory)) return new ArrayList<>();
        return Arrays.asList(oldHistory.split(","));
    }

    protected void clearSearchHistory() {
        SharedPreferencesUtils.removeParam(this, getHistoryKey());
    }

    protected void loadAndShowHistory() {
        List<String> history = getSearchHistory();
        if (historyAdapter != null) historyAdapter.setList(history);

        if (history.isEmpty()) {
            if (mHistoryLayout != null) mHistoryLayout.setVisibility(View.GONE);
        } else if (TextUtils.isEmpty(etSearch.getText())) {
            if (mHistoryLayout != null) mHistoryLayout.setVisibility(View.VISIBLE);
        }
    }

    protected void showHistoryView() {
        if (mHistoryLayout != null) {
            mHistoryLayout.setVisibility(View.VISIBLE);
        }
        if (mSearchResultLayout != null) {
            mSearchResultLayout.setVisibility(View.GONE);
        }
        loadAndShowHistory();
    }

    protected void hideHistoryView() {
        if (mHistoryLayout != null) mHistoryLayout.setVisibility(View.GONE);
        if (mSearchResultLayout != null) mSearchResultLayout.setVisibility(View.VISIBLE);
    }

    protected void showKeyboard(EditText view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected String getHistoryKey() {
        return "common_search_history";
    }

    // 点击搜索结果的回调
    protected void onSearchResultClick(T item, int position) {}

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.white;
    }
}