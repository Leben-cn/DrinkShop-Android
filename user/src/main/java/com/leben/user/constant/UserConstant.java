package com.leben.user.constant;

public interface UserConstant {

    interface Router{
        String USER_LOGIN="/user/login";
        String USER_CENTER="/user/center";
        String MESSAGE="/user/message";
        String SHOP="/shop/main";
        String CUSTOMER="/app/customer";
        String ADDRESS="/user/address";
        String ADD_ADDRESS="/user/addAddress";
        String COMMENT="/user/comment";
        String MY_COMMENT="/user/myComment";
        String BILL="/user/bill";
        String FAVORITE="/user/favorite";
        String USER_INFO="/user/info";
    }

    interface Key{
        String TOKEN="token";
        String ROLE="role";
        String USER_INFO="user_info";
    }

}
