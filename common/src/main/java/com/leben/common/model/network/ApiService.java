package com.leben.common.model.network;

import com.leben.common.model.bean.ChatMessageEntity;
import com.leben.common.model.bean.CommonEntity;
import com.leben.common.model.bean.SessionEntity;
import java.util.List;
import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("/chat/sessions")
    Flowable<CommonEntity<List<SessionEntity>>> getSessionList(
    );

    @GET("/chat/history")
    Flowable<CommonEntity<List<ChatMessageEntity>>> getMessageList(
            @Query("targetId") Long targetId,
            @Query("targetRole") String targetRole
    );
}
