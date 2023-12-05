package com.chingchan.theClap.ui.login.api

import com.chingchan.theClap.ui.login.data.LoginUserReqData
import com.chingchan.theClap.ui.login.data.LoginUserResData
import com.chingchan.theClap.ui.login.data.MembershipUserData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginAPI {
    @POST("login/app")
    fun getLoginCheck(
        @Body req : LoginUserReqData,
    ): Call<LoginUserResData>

    @POST("login/signup")
    fun getSignUp(
        @Body req : MembershipUserData,
    ): Call<LoginUserResData>
}