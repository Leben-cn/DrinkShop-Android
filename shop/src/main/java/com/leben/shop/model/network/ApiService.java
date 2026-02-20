package com.leben.shop.model.network;

import com.leben.common.model.bean.CommentEntity;
import com.leben.common.model.bean.CommonEntity;
import com.leben.common.model.bean.DrinkEntity;
import com.leben.common.model.bean.OrderEntity;
import com.leben.shop.model.bean.DrinkQueryEntity;
import com.leben.shop.model.bean.PageEntity;
import com.leben.common.model.bean.ShopEntity;
import java.util.List;
import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/shops/feed")
    Flowable<CommonEntity<PageEntity<ShopEntity>>> getRecommendShops(
            @Query("page") int page,
            @Query("size") int size,
            @Query("seed") long seed,
            @Query("userLat") Double userLat,
            @Query("userLon") Double userLon
    );

    @GET("/shops/{shopId}/menu")
    Flowable<CommonEntity<List<DrinkEntity>>> getShopMenu(
            @Path("shopId") Long shopId,
            @Query("userLat") Double userLat,
            @Query("userLon") Double userLon
    );

    @GET("/users/favorite/check")
    Flowable<CommonEntity<Boolean>> checkFavorite(
            @Query("shopId") long shopId
    );

    @POST("/users/favorite/toggle")
    Flowable<CommonEntity<Boolean>> toggleFavorite(
            @Query("shopId") long shopId
    );

    @POST("/users/order/submit")
    Flowable<CommonEntity<Long>> submitOrder(
            @Body OrderEntity orderEntity
    );

    @GET("/users/order/list/all")
    Flowable<CommonEntity<List<OrderEntity>>> getAllOrder(
            @Query("userLat") Double userLat,
            @Query("userLon") Double userLon
    );

    @GET("/users/order/list/toComment")
    Flowable<CommonEntity<List<OrderEntity>>> getNoCommentOrder(
            @Query("userLat") Double userLat,
            @Query("userLon") Double userLon
    );

    @GET("/users/order/list/cancel")
    Flowable<CommonEntity<List<OrderEntity>>> getCancelOrder(
            @Query("userLat") Double userLat,
            @Query("userLon") Double userLon
    );

    @GET("/shops/{shopId}")
    Flowable<CommonEntity<ShopEntity>> getShopInfo(
            @Path("shopId") Long shopId,
            @Query("userLat") Double userLat,
            @Query("userLon") Double userLon
    );

    @GET("/shops/comment/list")
    Flowable<CommonEntity<List<CommentEntity>>> getShopComment(
            @Query("shopId") Long shopId
    );

    @POST("/users/order/cancel")
    Flowable<CommonEntity<String>> cancelOrder(
            @Query("orderId") Long orderId
    );

    @POST("/drinks/search")
    Flowable<CommonEntity<PageEntity<DrinkEntity>>> queryDrink(
            @Body DrinkQueryEntity entity,
            @Query("page") int page,
            @Query("size") int size
    );

}
