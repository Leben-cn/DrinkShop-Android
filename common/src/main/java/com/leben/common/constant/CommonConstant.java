package com.leben.common.constant;

public interface CommonConstant {
    interface Key{
        String TOKEN="token";
        String ROLE="role";
        String USER_INFO="user_info";
        String MERCHANT_INFO="merchant_info";
    }
    interface Router{
        String MERCHANT_TAB="/common/merchant/tab";
        String SESSION_LIST="/common/session/list";
        String CHAT_DETAIL="/common/chat/detail";
        String CUSTOMER_TAB="/common/customer/tab";
        String ORDER="/shop/orders";
        String RECOMMEND_DRINKS="/shop/drinks";
        String USER_CENTER="/user/center";
        String WORK_BENCH="/merchant/workbench";
        String GOODS="/merchant/goods";
        String USER_LOGIN="/user/login";
        String USER_COMMENT="/user/comment";

        String ADD_ADDRESS="/add/address";
    }

    interface URL{
        String BASE_URL="172.16.40.125:8080";
    }

}
