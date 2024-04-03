package com.chingchan.theClap.ui.login.data

import android.content.Context
import android.content.Context.MODE_PRIVATE

object UserData {
    private const val PREFERENCE_NAME = "userData"

    private const val LOGIN_TYPE = "loginType"
    private const val USER_ID = "userId"
    private const val USER_NAME = "userName"
    private const val NICK_NAME = "nickName"

    // 로그인 타입 저장하기
    fun setLoginType(context: Context, type: String) =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().putString(LOGIN_TYPE, type).commit()

    // 로그인 타입 가져오기
    fun getLoginType(context: Context) : String =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).getString(LOGIN_TYPE, "") ?: ""

    // 로그인 타입 삭제
    fun removeLoginType(context: Context) =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().remove(LOGIN_TYPE).apply()


    // Access 토큰 저장하기
    // Access 토큰은 암호화된 sharedPreferences로 따로 저장
    fun setAccessToken(context: Context, type: String) =
        UserTokenData.setAccessToken(context, type)

    // Access 토큰 가져오기
    // Access 토큰은 암호화된 sharedPreferences로 따로 저장
    fun getAccessToken(context: Context) : String =
        UserTokenData.getAccessToken(context)

    // Access 토큰 삭제
    // Access 토큰은 암호화된 sharedPreferences로 따로 저장
    fun removeAccessToken(context: Context) =
        UserTokenData.removeAccessToken(context)


    // Refresh 토큰 저장하기
    // Refresh 토큰은 암호화된 sharedPreferences로 따로 저장
    fun setRefreshToken(context: Context, type: String) =
        UserTokenData.setRefreshToken(context, type)

    // Refresh 토큰 가져오기
    // Refresh 토큰은 암호화된 sharedPreferences로 따로 저장
    fun getRefreshToken(context: Context) : String =
        UserTokenData.getRefreshToken(context)

    // Refresh 토큰 삭제
    // Refresh 토큰은 암호화된 sharedPreferences로 따로 저장
    fun removeRefreshToken(context: Context) =
        UserTokenData.removeRefreshToken(context)



    // User Id 저장하기
    fun setUserId(context: Context, type: Int) =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().putInt(USER_ID, type).commit()

    // User Id 가져오기
    fun getUserId(context: Context) : Int =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).getInt(USER_ID, 0) ?: 0

    // User Id 삭제
    fun removeUserId(context: Context) =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().remove(USER_ID).apply()


    // User Name 저장하기
    fun setUserName(context: Context, type: String) =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().putString(USER_NAME, type).commit()

    // User Name 가져오기
    fun getUserName(context: Context) : String =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).getString(USER_NAME, "") ?: ""

    // User Name 삭제
    fun removeUserName(context: Context) =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().remove(USER_NAME).apply()


    // Nick Name 저장하기
    fun setNickName(context: Context, type: String) =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().putString(NICK_NAME, type).commit()

    // Nick Name 가져오기
    fun getNickName(context: Context) : String =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).getString(NICK_NAME, "") ?: ""

    // Nick Name 삭제
    fun removeNickName(context: Context) =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().remove(NICK_NAME).apply()


    // 전체 데이터 삭제
    fun removeAll(context: Context){
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().clear().apply()

        removeAccessToken(context)    // 토큰은 암호화된 sharedPreference로 따로 만들어지기 때문에 따로 제거
    }
}

