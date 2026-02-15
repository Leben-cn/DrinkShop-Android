package com.leben.user.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding2.view.RxView;
import com.leben.base.ui.fragment.BaseFragment;
import com.leben.base.util.LogUtils;
import com.leben.base.util.SharedPreferencesUtils;
import com.leben.base.widget.dialog.CommonDialog;
import com.leben.common.Constant.CommonConstant;
import com.leben.user.model.bean.LoginEntity;
import com.leben.user.util.UserUtils;
import com.leben.user.R;
import com.leben.user.constant.UserConstant;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

@Route(path = CommonConstant.Router.USER_CENTER)
public class UserCenterFragment extends BaseFragment {

    private TextView tvLogout;
    private ImageView ivAvatar;
    private TextView tvNickname;
    private TextView tvAddress;
    private TextView tvComment;
    private TextView tvBill;
    private TextView tvCollect;
    private TextView tvUserInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.frag_user_center;
    }

    @Override
    protected void initView(View root) {
        tvLogout=root.findViewById(R.id.item_logout);
        ivAvatar=root.findViewById(R.id.iv_avatar);
        tvNickname=root.findViewById(R.id.tv_nickname);
        tvAddress=root.findViewById(R.id.tv_address);
        tvComment=root.findViewById(R.id.tv_comment);
        tvBill=root.findViewById(R.id.tv_bill);
        tvCollect=root.findViewById(R.id.tv_collect);
        tvUserInfo=root.findViewById(R.id.item_userInfo);

        loadUserInfo();

    }

    @SuppressLint("CheckResult")
    @Override
    public void initListener() {
        RxView.clicks(tvLogout)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .flatMap(unit -> Observable.<Boolean>create(emitter -> {
                    CommonDialog dialog = CommonDialog.newInstance()
                            .setTitle("退出登录")
                            .setContent("确定要退出当前账号吗？");

                    dialog.setOnCancelListener(d -> {
                        emitter.onNext(false); // 发射 false 表示取消
                        emitter.onComplete();
                        d.dismiss();
                    });

                    dialog.setOnConfirmListener(d -> {
                        emitter.onNext(true); // 发射 true 表示确定
                        emitter.onComplete();
                        d.dismiss();
                    });

                    dialog.show(getParentFragmentManager(), "logout_tag");
                }))
                .filter(result -> result) // 只处理 true（确定）的情况
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    // 1. 清除用户数据
                    SharedPreferencesUtils.removeParam(getContext(), CommonConstant.Key.TOKEN);
                    SharedPreferencesUtils.removeParam(getContext(), CommonConstant.Key.USER_INFO);
                    SharedPreferencesUtils.removeParam(getContext(), CommonConstant.Key.ROLE);

                    // 2. 跳转到登录页
                    ARouter.getInstance()
                            .build(CommonConstant.Router.USER_LOGIN)
                            .navigation();

                    // 3. 结束当前页面
                    if (getActivity() != null) {
                        getActivity().finish();
                    }

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(tvAddress)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    ARouter.getInstance()
                            .build(UserConstant.Router.ADDRESS)
                            .navigation();

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(tvComment)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    ARouter.getInstance()
                            .build(UserConstant.Router.MY_COMMENT)
                            .navigation();

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(tvBill)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    ARouter.getInstance()
                            .build(UserConstant.Router.BILL)
                            .navigation();

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(tvCollect)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    ARouter.getInstance()
                            .build(UserConstant.Router.FAVORITE)
                            .navigation();

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
        RxView.clicks(tvUserInfo)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    ARouter.getInstance()
                            .build(UserConstant.Router.USER_INFO)
                            .navigation();

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });

        RxView.clicks(ivAvatar)
                .throttleFirst(500, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{

                    ARouter.getInstance()
                            .build(UserConstant.Router.USER_INFO)
                            .navigation();

                },throwable -> {
                    LogUtils.error("点击事件错误: " + throwable.getMessage());
                });
    }

    @Override
    public void initData() {

    }

    private void loadUserInfo(){
        LoginEntity.UserInfo myInfo = UserUtils.getUserInfo(getContext());
        if (myInfo != null) {
            tvNickname.setText(myInfo.getNickname());
            Glide.with(requireContext())
                    .load(myInfo.getAvatar())
                    .error(R.drawable.pic_default_avatar)
                    .circleCrop()
                    .into(ivAvatar);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }
}
