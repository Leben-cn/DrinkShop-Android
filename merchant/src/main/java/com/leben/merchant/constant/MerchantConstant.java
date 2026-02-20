package com.leben.merchant.constant;

public interface MerchantConstant {
    interface Router{
        String REGISTER="/merchant/register";
        String MERCHANT_LOGIN="/merchant/login";
        String ADD_ADDRESS="/merchant/add/address";
        String ORDER_ALL="/merchant/all/order";
        String ORDER_CANCEL="/merchant/cancel/order";
        String ORDER_DONE="/merchant/done/order";
        String ORDER_PENDING="/merchant/pending/order";
        String DRINK_EDIT="/merchant/edit/drink";
        String CATEGORY_EDIT="/merchant/edit/shop/category";
    }
}
