package com.chingchan.theClap.ui.myPage.data

import com.google.gson.annotations.SerializedName

data class ProfileResData(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: ProfileData,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: String
)
