package com.chingchan.theClap.globalFunction

import com.chingchan.theClap.BuildConfig
import com.chingchan.theClap.ui.compliment.api.ComplimentAPI
import com.chingchan.theClap.ui.login.api.LoginAPI
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

    val getLoginService : LoginAPI by lazy { getRetrofit.create(LoginAPI::class.java) }
    val getCompService : ComplimentAPI by lazy { getRetrofit.create(ComplimentAPI::class.java) }
}