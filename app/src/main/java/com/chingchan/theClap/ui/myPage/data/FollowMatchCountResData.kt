package com.chingchan.theClap.ui.myPage.data

import com.google.gson.annotations.SerializedName

data class FollowMatchCountResData(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: FollowMatchCountData,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: String
)
