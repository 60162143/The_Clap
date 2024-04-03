package com.chingchan.theClap.ui.login.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object UserTokenData {
    private const val PREFERENCE_NAME = "userAccessToken"

    private const val ACCESS_TOKEN = "accessToken"
    private const val REFRESH_TOKEN = "refreshToken"


    // EncryptedSharedPreferences 초기화
    private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
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

    // Access 토큰 저장하기
    fun setAccessToken(context: Context, type: String) =
        getEncryptedSharedPreferences(context).edit().putString(ACCESS_TOKEN, type).commit()

    // Access 토큰 가져오기
    fun getAccessToken(context: Context) : String =
        getEncryptedSharedPreferences(context).getString(ACCESS_TOKEN, "") ?: ""

    // Access 토큰 삭제
    fun removeAccessToken(context: Context) =
        getEncryptedSharedPreferences(context).edit().remove(ACCESS_TOKEN).apply()


    // Refresh 토큰 저장하기
    fun setRefreshToken(context: Context, type: String) =
        getEncryptedSharedPreferences(context).edit().putString(REFRESH_TOKEN, type).commit()

    // Refresh 토큰 가져오기
    fun getRefreshToken(context: Context) : String =
        getEncryptedSharedPreferences(context).getString(REFRESH_TOKEN, "") ?: ""

    // Refresh 토큰 삭제
    fun removeRefreshToken(context: Context) =
        getEncryptedSharedPreferences(context).edit().remove(REFRESH_TOKEN).apply()
}

