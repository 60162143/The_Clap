package com.chingchan.theClap.ui.login.api

import com.chingchan.theClap.ui.login.data.GuestLoginUserResData
import com.chingchan.theClap.ui.login.data.LoginUserReqData
import com.chingchan.theClap.ui.login.data.LoginUserResData
import com.chingchan.theClap.ui.login.data.MembershipUserData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginAPI {
    // 로그인 확인
    @POST("login/app")
    fun getLoginCheck(
        @Body req : LoginUserReqData,
    ): Call<LoginUserResData>

    // 회원가입
    @POST("login/signup")
    fun getSignUp(
        @Body req : MembershipUserData,
    ): Call<LoginUserResData>

    // 게스트 회원가입
    @POST("login/guestSignup")
    fun getGuestLogin(
    ): Call<GuestLoginUserResData>
}