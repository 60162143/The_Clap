package com.chingchan.theClap.ui.login.data

import com.google.gson.annotations.SerializedName

data class LoginUserResDataDetail(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("userId") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("refresh_token") val refreshToken: String,
)
