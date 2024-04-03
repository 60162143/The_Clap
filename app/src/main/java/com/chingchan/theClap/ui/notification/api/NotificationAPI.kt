package com.chingchan.theClap.ui.notification.api

import com.chingchan.theClap.ui.login.data.MembershipUserData
import com.chingchan.theClap.ui.myPage.data.AnnounceResData
import com.chingchan.theClap.ui.myPage.data.CompBookmarkResData
import com.chingchan.theClap.ui.myPage.data.FAQResData
import com.chingchan.theClap.ui.myPage.data.NicknameDupReqData
import com.chingchan.theClap.ui.myPage.data.NicknameDupResData
import com.chingchan.theClap.ui.myPage.data.ProfileReqData
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.notification.data.NotificationDeleteResData
import com.chingchan.theClap.ui.notification.data.NotificationReadResData
import com.chingchan.theClap.ui.notification.data.NotificationResData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationAPI {
    // 알림 데이터 조회
    @GET("log")
    fun getLog(
        @Header("Authorization") authorization: String
    ): Call<NotificationResData>

    // 알림 데이터 삭제
    @DELETE("log")
    fun deleteLog(
        @Header("Authorization") authorization: String,
        @Query("logIds") logIds: ArrayList<Int>
    ): Call<NotificationDeleteResData>

    // 알림 데이터 '읽음' 처리
    @GET("log/{logId}")
    fun readLog(
        @Header("Authorization") authorization: String,
        @Path("logId") logId: Int
    ): Call<NotificationReadResData>
}