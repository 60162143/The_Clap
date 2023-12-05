package com.chingchan.theClap.ui.compliment.api

import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.login.data.LoginUserResData
import com.chingchan.theClap.ui.login.data.MembershipUserData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ComplimentAPI {
    @GET("boards/board-type")
    fun getCompCat(): Call<CompCatResData>

    @GET("boards/board-type/count")
    fun getCompCatCount(): Call<CompCatResData>
}