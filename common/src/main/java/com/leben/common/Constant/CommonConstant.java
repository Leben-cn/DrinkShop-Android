package com.leben.common.Constant;

public interface CommonConstant {
    interface Key{
        String TOKEN="token";
        String ROLE="role";
        String USER_INFO="user_info";
        String MERCHANT_INFO="merchant_info";
    }
    interface Router{
        String MERCHANT_TAB="/common/merchant/tab";
        String MESSAGE="/common/message";
        String CUSTOMER_TAB="/common/customer/tab";
        String ORDER="/shop/orders";
        String RECOMMEND_DRINKS="/shop/drinks";
        String USER_CENTER="/user/center";
        String WORK_BENCH="/merchant/workbench";
        String GOODS="/merchant/goods";
        String MERCHANT_CENTER="/merchant/center";
        String USER_LOGIN="/user/login";
    }

}
