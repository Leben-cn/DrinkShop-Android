package com.leben.merchant.model.network;

import com.leben.common.model.bean.CommonEntity;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.common.model.bean.OrderEntity;
import com.leben.merchant.model.bean.LoginEntity;
import com.leben.merchant.model.bean.MerchantRegisterEntity;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/shops/register")
    Flowable<CommonEntity<String>> register(
            @Body MerchantRegisterEntity entity
    );

    @POST("/shops/login")
    Flowable<CommonEntity<LoginEntity>> login(
            @Query("account") String account,
            @Query("password") String password
    );

    @GET("/merchant/orders/all")
    Flowable<CommonEntity<List<OrderEntity>>> getAllOrder(
    );

    @GET("/merchant/orders/pending")
    Flowable<CommonEntity<List<OrderEntity>>> getPendingOrder(
    );

    @GET("/merchant/orders/completed")
    Flowable<CommonEntity<List<OrderEntity>>> getCompletedOrder(
    );

    @GET("/merchant/orders/refund")
    Flowable<CommonEntity<List<OrderEntity>>> getRefundOrder(
    );

    @GET("/shops/{shopId}/menu")
    Flowable<CommonEntity<List<DrinkEntity>>> getShopDrink(
            @Path("shopId") Long shopId
    );

}
