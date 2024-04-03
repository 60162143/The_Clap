package com.chingchan.theClap.globalFunction

import android.app.Application
import com.chingchan.theClap.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.kakao.sdk.common.KakaoSdk
import com.navercorp.nid.NaverIdLoginSDK

class InitApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 카카오 SDK 초기화
        KakaoSdk.init(applicationContext, BuildConfig.KAKAO_LOGIN_API_KEY)

        // 네이버 SDK 초기화
        NaverIdLoginSDK.initialize(applicationContext, BuildConfig.NAVER_LOGIN_CLIENT_ID, BuildConfig.NAVER_LOGIN_CLIENT_SECRET, BuildConfig.NAVER_LOGIN_CLIENT_NAME)
    }

    // 구글 SDK 초기화
    companion object {

        private val instance = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(BuildConfig.GOOGLE_LOGIN_CLIENT_ID)
//            .requestServerAuthCode(BuildConfig.GOOGLE_LOGIN_CLIENT_ID) // client id 를 이용해 server authcode를 요청한다.
            .requestEmail() // 이메일도 요청할 수 있다.
            .build()

        fun getInstance(): GoogleSignInOptions {
            return instance
        }
    }
}