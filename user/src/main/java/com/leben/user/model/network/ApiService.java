package com.leben.user.model.network;

import com.leben.common.model.bean.AddressEntity;
import com.leben.common.model.bean.CommentEntity;
import com.leben.common.model.bean.CommonEntity;
import com.leben.common.model.bean.ShopEntity;
import com.leben.user.model.bean.BillEntity;
import com.leben.user.model.bean.CommentSubmitEntity;
import com.leben.common.model.bean.LoginEntity;
import com.leben.user.model.bean.UserInfoEntity;

import java.util.List;
import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("/users/login")
    Flowable<CommonEntity<LoginEntity>> login(
            @Query("account") String account,
            @Query("password") String password
    );

    @POST("/address/save")
    Flowable<CommonEntity<String>> saveAddress(
            @Body AddressEntity addressEntity
    );

    @GET("/users/address/list")
    Flowable<CommonEntity<List<AddressEntity>>> getMyAddress(
    );

    // 2. 提交评价
    @POST("/users/submit/comment")
    Flowable<CommonEntity<String>> submitComment(
            @Body CommentSubmitEntity request
    );

    @GET("/users/comment/list")
    Flowable<CommonEntity<List<CommentEntity>>> getMyComment(
    );

    @GET("/users/bill/list")
    Flowable<CommonEntity<List<BillEntity>>> getMyBill(
    );

    @GET("/users/favorite/list")
    Flowable<CommonEntity<List<ShopEntity>>> getMyFavorite(
            @Query("userLat") Double userLat,
            @Query("userLon") Double userLon
    );

    @POST("/users/update/info")
    Flowable<CommonEntity<String>> updateUserInfo(
            @Body UserInfoEntity userInfoEntity
    );

}
