package com.chingchan.theClap.globalFunction

import com.chingchan.theClap.BuildConfig
import com.chingchan.theClap.ui.compliment.api.ComplimentAPI
import com.chingchan.theClap.ui.login.api.LoginAPI
import com.chingchan.theClap.ui.myPage.api.MyPageAPI
import com.chingchan.theClap.ui.notification.api.NotificationAPI
import com.chingchan.theClap.ui.search.api.SearchAPI
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiObject {
    private const val BASE_URL = BuildConfig.BASE_URL

    val getRetrofit: Retrofit by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getLoginService : LoginAPI by lazy { getRetrofit.create(LoginAPI::class.java) } // 로그인 관련 API
    val getCompService : ComplimentAPI by lazy { getRetrofit.create(ComplimentAPI::class.java) }    // 게시글 관련 API
    val getMyPageService : MyPageAPI by lazy { getRetrofit.create(MyPageAPI::class.java) }  // 마이페이지 관련 API
    val getSearchService : SearchAPI by lazy { getRetrofit.create(SearchAPI::class.java) }  // 검색 관련 API
    val getNotificationService : NotificationAPI by lazy { getRetrofit.create(NotificationAPI::class.java) }    // 내소식 관련 API
}