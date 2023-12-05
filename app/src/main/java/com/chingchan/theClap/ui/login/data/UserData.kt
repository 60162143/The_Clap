package com.chingchan.theClap.ui.login.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object UserData {
    private const val PREFERENCE_NAME = "userData"

    private const val LOGIN_TYPE = "loginType"
    private const val ACCESS_TOKEN = "accessToken"
    private const val USER_ID = "userId"
    private const val USER_NAME = "userName"


    // EncryptedSharedPreferences 초기화
    private fun getSharedPreferences(context: Context): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            PREFERENCE_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    // 로그인 타입 저장하기
    fun setLoginType(context: Context, type: String) =
        getSharedPreferences(context).edit().putString(LOGIN_TYPE, type).commit()

    // 로그인 타입 가져오기
    fun getLoginType(context: Context) : String =
        getSharedPreferences(context).getString(LOGIN_TYPE, "") ?: ""

    // 로그인 타입 삭제
    fun removeLoginType(context: Context, type: String) =
        getSharedPreferences(context).edit().remove(LOGIN_TYPE).apply()


    // Access 토큰 저장하기
    fun setAccessToken(context: Context, type: String) =
        getSharedPreferences(context).edit().putString(ACCESS_TOKEN, type).commit()

    // Access 토큰 가져오기
    fun getAccessToken(context: Context) : String =
        getSharedPreferences(context).getString(ACCESS_TOKEN, "") ?: ""

    // Access 토큰 삭제
    fun removeAccessToken(context: Context, type: String) =
        getSharedPreferences(context).edit().remove(ACCESS_TOKEN).apply()


    // User Id 저장하기
    fun setUserId(context: Context, type: Int) =
        getSharedPreferences(context).edit().putInt(USER_ID, type).commit()

    // User Id 가져오기
    fun getUserId(context: Context) : Int =
        getSharedPreferences(context).getInt(USER_ID, 0) ?: 0

    // User Id 삭제
    fun removeUserId(context: Context, type: Int) =
        getSharedPreferences(context).edit().remove(USER_ID).apply()


    // User Name 저장하기
    fun setUserName(context: Context, type: String) =
        getSharedPreferences(context).edit().putString(USER_NAME, type).commit()

    // User Name 가져오기
    fun getUserName(context: Context) : String =
        getSharedPreferences(context).getString(USER_NAME, "") ?: ""

    // User Name 삭제
    fun removeUserName(context: Context, type: String) =
        getSharedPreferences(context).edit().remove(USER_NAME).apply()


    // 전체 데이터 삭제
    fun removeAll(context: Context) =
        getSharedPreferences(context).edit().clear().apply()
}

