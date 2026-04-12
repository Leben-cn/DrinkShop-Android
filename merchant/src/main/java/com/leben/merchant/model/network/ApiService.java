package com.leben.merchant.model.network;

import com.leben.common.model.bean.CommonEntity;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.common.model.bean.OrderEntity;
import com.leben.common.model.bean.ShopCategoriesEntity;
import com.leben.common.model.bean.SpecOptionEntity;
import com.leben.merchant.model.bean.DrinkRequestEntity;
import com.leben.merchant.model.bean.LoginEntity;
import com.leben.merchant.model.bean.MerchantRegisterEntity;
import com.leben.merchant.model.bean.MerchantInfoEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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

    @GET("/merchant/{shopId}/drinks")
    Flowable<CommonEntity<List<DrinkEntity>>> getShopDrink(
            @Path("shopId") Long shopId
    );

    @GET("/merchant/spec/all")
    Flowable<CommonEntity<List<SpecOptionEntity>>> getAllSpec(
    );

    @GET("/merchant/category/all")
    Flowable<CommonEntity<List<ShopCategoriesEntity>>> getShopCategory(
    );

    @POST("/merchant/category/add")
    Flowable<CommonEntity<String>> addCategory(
            @Query("name") String name
    );

    @DELETE("/merchant/category/delete/{id}")
    Flowable<CommonEntity<String>> deleteCategory(
            @Path("id") Long categoryId
    );

    @POST("/merchant/category/sort/update")
    Flowable<CommonEntity<String>> updateShopCategory(
            @Body List<Long> ids
    );

    @POST("/merchant/drink/save")
    Flowable<CommonEntity<String>> saveDrink(
            @Body DrinkRequestEntity entity
    );

    @POST("/merchant/update/info")
    Flowable<CommonEntity<String>> updateMerchantInfo(
            @Body MerchantInfoEntity merchantInfoEntity
    );

    @POST("/merchant/update/status")
    Flowable<CommonEntity<String>> updateShopStatus(
            @Query("status") Integer status
    );

    @POST("/merchant/delete/drink")
    Flowable<CommonEntity<String>> delectDrink(
            @Query("drinkId") Long drinkId
    );

    @GET("/merchant/shop/stats")
    Flowable<CommonEntity<Map<String, Object>>> getShopTodayStats(
    );

    //用于店铺经营页面
    @GET("/merchant/order/list")
    Flowable<CommonEntity<List<OrderEntity>>> getOrderByDate(
            @Query("dateStr") String dateStr
    );

    @GET("/merchant/shop/revenue")
    Flowable<CommonEntity<BigDecimal>> getShopRevenue(
    );



}
