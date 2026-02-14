package com.leben.merchant.model.network;

import com.leben.common.model.bean.CommonEntity;
import com.leben.merchant.model.bean.MerchantRegisterEntity;
import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("/shops/register")
    Flowable<CommonEntity<String>> register(
            @Body MerchantRegisterEntity entity
    );
}
