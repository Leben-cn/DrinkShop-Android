package com.leben.base.ui.activity;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leben.base.R;
import com.leben.base.controller.LoadMoreController;
import com.leben.base.controller.StateController;
import com.leben.base.decoration.SpaceItemDecoration;
import com.leben.base.ui.adapter.BaseRecyclerAdapter;
import com.leben.base.ui.adapter.HistoryAdapter;
import com.leben.base.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 搜索通用基类
 * 优化点：严格遵守 BaseActivity 的模板方法模式
 */
public abstract class BaseSearchActivity<T> extends BaseRefreshActivity {

    // 【核心】保存当前的搜索关键词
    protected String mCurrentKeyword = "";

    protected EditText etSearch;

    protected ImageView ivClear;

    protected StateController mStateController;
    // 加载更多控制器
    protected LoadMoreController mLoadMoreController;

    protected RecyclerView mRecyclerView;
    // 默认间距值
    private int mDefaultSpace = 16;

    protected BaseRecyclerAdapter<T> mAdapter;

    protected View mHistoryLayout; // 对应 nsv_history

    protected RecyclerView mRvHistory;

    protected View mDeleteHistory; // 垃圾桶图标

    protected HistoryAdapter historyAdapter;

    protected View mSearchResultLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.ac_base_search;
    }

    @Override
    public void initView() {
        super.initView();
        etSearch = findViewById(R.id.et_search);
        ivClear = findViewById(R.id.iv_clear);

        // 2. 初始化历史记录 View
        mHistoryLayout = findViewById(R.id.nsv_history);
        mRvHistory = findViewById(R.id.rv_history);
        mDeleteHistory = findViewById(R.id.iv_delete_history);

        mSearchResultLayout = findViewById(R.id.swipeRefresh);

        mRvHistory.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 3));
        historyAdapter = new HistoryAdapter(this);
        mRvHistory.setAdapter(historyAdapter);

        //1.查找 RecyclerView
        int rvId=getResources().getIdentifier("contentView","id",getPackageName());
        if(rvId!=0){
            mRecyclerView=findViewById(rvId);
        }
        if(mRecyclerView!=null){
            //2.初始化 LayoutManager
            mRecyclerView.setLayoutManager(getLayoutManager());
            // 只在需要时添加默认间距
            if (shouldAddDefaultSpaceDecoration()) {
                mRecyclerView.addItemDecoration(new SpaceItemDecoration(mDefaultSpace));
            }

            //3.创建并绑定 Adapter
            mAdapter=createAdapter();
            mRecyclerView.setAdapter(mAdapter);

            mAdapter.setOnItemClickListener((view, position, entity) -> {
                onSearchResultClick(entity, position);
            });

            //4.初始化状态控制器，查找空布局和错误布局
            mStateController=new StateController(this,mRecyclerView);
            int emptyStubId=getResources().getIdentifier("emptyStub","id",getPackageName());
            if(emptyStubId!=0){
                ViewStub stub=findViewById(emptyStubId);
                mStateController.setEmptyViewStub(stub);
            }

            int errorStubId=getResources().getIdentifier("errorStub","id",getPackageName());
            if(errorStubId!=0){
                ViewStub stub=findViewById(errorStubId);
                mStateController.setErrorViewStub(stub);
            }

            if (isSupportLoadMore()) {
                mLoadMoreController = new LoadMoreController(mRecyclerView, mAdapter);
                mLoadMoreController.setOnLoadMoreListener(this::onLoadMore);
                // 注册控制器
                registerController("loadMore_controller", mLoadMoreController);
            }

            //注册生命周期
            registerController("state_controller",mStateController);
        }
    }

    // 3. 在 initListener 中设置监听器
    @Override
    public void initListener() {
        // 返回
        findViewById(R.id.iv_back).setOnClickListener(v -> finish());

        // 清除
        ivClear.setOnClickListener(v -> {
            etSearch.setText("");
            // 清空后显示软键盘，方便重新输入
            showKeyboard(etSearch);
            mCurrentKeyword = "";
            // 清空搜索框 -> 切换回历史记录模式
            showHistoryView();
        });

        mDeleteHistory.setOnClickListener(v -> {
            // 弹窗确认删除 -> clearSearchHistory()
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setMessage("确认清空历史记录吗？")
                    .setPositiveButton("确认", (d, w) -> {
                        clearSearchHistory();
                        loadAndShowHistory(); // 刷新 UI
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        // 文本变化
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                boolean hasContent = s.length() > 0;
                ivClear.setVisibility(hasContent ? View.VISIBLE : View.GONE);
                // 这里可以扩展防抖动搜索逻辑
                if (!hasContent) {
                    showHistoryView();
                } else {
                    // 这里看需求：
                    // 策略A: 一打字就隐藏历史，显示搜索结果容器(此时可能是空的)
                    // hideHistoryView();

                    // 策略B: 只有点了搜索才隐藏历史 (推荐策略B，防止打字时页面乱闪)
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
    }

    // 工具方法：显示键盘
    protected void showKeyboard(EditText view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
    }

    // 工具方法：隐藏键盘
    protected void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected View getTitleBarView() {
        return findViewById(R.id.title_bar);
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.white;
    }

    /**
     * 数据请求成功后调用此方法
     * 停止刷新 + 填充数据 + 切换空布局状态
     */
    public void refreshListSuccess(List<T> list){
        refreshComplete();
        if (mAdapter != null) {
            mAdapter.setList(list);
        }
        if(mStateController!=null){
            mStateController.handleData(list);
        }
        if (mLoadMoreController != null) {
            mLoadMoreController.reset();
        }
    }

    public void refreshListFailed(String msg) {
        refreshComplete();
        //如果当前列表是空的，就显示全屏错误页
        if(mAdapter==null||mAdapter.getItemCount()==0){
            if(mStateController!=null){
                mStateController.showError();
            }
        }else{
            //如果列表里已经有数据了，就不全屏显示，简单弹个吐司就行
            showError(msg);
        }
    }

    public void loadMoreSuccess(List<T> list, boolean hasMore) {
        if (mAdapter != null) {
            mAdapter.addList(list);
        }
        if (mLoadMoreController != null) {
            mLoadMoreController.loadMoreSuccess(hasMore);
        }
    }

    public void loadMoreFailed() {
        if (mLoadMoreController != null) {
            mLoadMoreController.loadMoreFail();
        }
    }

    protected void setDefaultSpace(int space) {
        mDefaultSpace = space;
    }

    protected boolean isSupportLoadMore() {
        return true;
    }

    /**
     * 默认线性布局，子类可覆盖
     */
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this);
    }

    protected boolean shouldAddDefaultSpaceDecoration() {
        return true;
    }

    protected abstract BaseRecyclerAdapter<T> createAdapter();

    /**
     * 【核心方法 1】执行搜索
     * 把它看作是一次“带关键词的刷新”
     */
    protected void performSearch() {
        String keyword = etSearch.getText().toString().trim();
        if (TextUtils.isEmpty(keyword)) {
            return;
        }
        hideKeyboard();
        hideHistoryView();

        // 更新关键词
        mCurrentKeyword = keyword;

        saveSearchHistory(keyword);

        // 触发刷新 (这会调用 onRefresh)
        autoRefresh();
    }

    private void saveSearchHistory(String keyword) {
        String key = getHistoryKey();
        // 1. 获取旧数据 (假设存储格式是 "apple,banana,orange")
        String oldHistory = SharedPreferencesUtils.getParam(this, key, "");

        List<String> historyList = new ArrayList<>();
        if (!TextUtils.isEmpty(oldHistory)) {
            String[] split = oldHistory.split(",");
            historyList.addAll(Arrays.asList(split));
        }

        // 2. 移除重复 (如果已经有了，先删掉，再加到最前面)
        // 倒序遍历删除，避免 ConcurrentModificationException
        for (int i = historyList.size() - 1; i >= 0; i--) {
            if (historyList.get(i).equals(keyword)) {
                historyList.remove(i);
            }
        }

        // 3. 插入到最前面
        historyList.add(0, keyword);

        // 4. 限制数量 (比如只存 10 条)
        if (historyList.size() > 10) {
            historyList = historyList.subList(0, 10);
        }

        // 5. 拼接回字符串并保存
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < historyList.size(); i++) {
            sb.append(historyList.get(i));
            if (i < historyList.size() - 1) {
                sb.append(",");
            }
        }
        SharedPreferencesUtils.setParam(this, key, sb.toString());

        // 6. 可选：如果当前正好展示着历史记录页面，需要刷新一下 UI
        // updateHistoryUI(historyList);
    }

    /**
     * 获取历史记录列表
     * 子类如果想展示历史记录，调用这个方法即可拿到数据
     */
    protected List<String> getSearchHistory() {
        String key = getHistoryKey();
        String oldHistory = SharedPreferencesUtils.getParam(this, key, "");
        List<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(oldHistory)) {
            String[] split = oldHistory.split(",");
            list.addAll(Arrays.asList(split));
        }
        return list;
    }

    /**
     * 清空历史记录
     */
    protected void clearSearchHistory() {
        SharedPreferencesUtils.removeParam(this, getHistoryKey());
        // updateHistoryUI(new ArrayList<>());
    }

    protected void onSearchResultClick(T item, int position) {}

    @Override
    public void onRefresh() {
        if (TextUtils.isEmpty(mCurrentKeyword)) {
            refreshComplete(); // 如果没有关键词，直接结束刷新
            return;
        }
        // 调用子类去请求网络
        doSearch(mCurrentKeyword);
    }

    /**
     * 不管是下拉刷新、点击搜索、还是上拉加载，最终都走这里。
     */
    protected abstract void doSearch(String keyword);

    @Override
    public void initData() {
        etSearch.postDelayed(() -> showKeyboard(etSearch), 200);
        loadAndShowHistory();
    }

    public void onLoadMore() {
        doLoadMore(mCurrentKeyword);
    }

    protected abstract void doLoadMore(String keyword);

    // 【新增】用于保存历史记录的 Key，默认是 "common_search_history"
    // 子类可以重写 getHistoryKey() 来隔离不同业务的历史记录
    protected String getHistoryKey() {
        return "common_search_history";
    }

    /**
     * 显示历史记录，隐藏搜索结果
     */
    protected void showHistoryView() {
        if (mHistoryLayout != null) mHistoryLayout.setVisibility(View.VISIBLE);
        // 隐藏 SwipeRefreshLayout (BaseRefreshActivity 里的 mRefreshLayout)
        if (mSearchResultLayout != null) mSearchResultLayout.setVisibility(View.GONE);

        // 重新加载一下数据，保证是最新的
        loadAndShowHistory();
    }

    /**
     * 加载数据并刷新 Adapter
     */
    protected void loadAndShowHistory() {
        List<String> history = getSearchHistory();
         if (historyAdapter != null) {
             historyAdapter.setList(history);
         }

        // 如果没有历史记录，也可以把整个 mHistoryLayout 隐藏，或者只隐藏 RecyclerView
        if (history.isEmpty()) {
            if (mHistoryLayout != null) {
                mHistoryLayout.setVisibility(View.GONE);
            }
        } else {
            // 只有当 etSearch 为空时才强制显示出来
            if (TextUtils.isEmpty(etSearch.getText())) {
                if (mHistoryLayout != null) {
                    mHistoryLayout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * 隐藏历史记录，显示搜索结果
     */
    protected void hideHistoryView() {
        if (mHistoryLayout != null) mHistoryLayout.setVisibility(View.GONE);

        // 【修改】使用 mSearchResultLayout
        if (mSearchResultLayout != null) mSearchResultLayout.setVisibility(View.VISIBLE);
    }

}