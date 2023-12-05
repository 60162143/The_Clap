package com.chingchan.theClap.ui.login.data

import com.google.gson.annotations.SerializedName

data class LoginUserResData(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: LoginUserResDataDetail,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String
)
