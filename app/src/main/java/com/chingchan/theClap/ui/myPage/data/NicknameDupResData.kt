package com.chingchan.theClap.ui.myPage.data

import com.chingchan.theClap.ui.compliment.data.CompData
import com.google.gson.annotations.SerializedName

data class NicknameDupResData(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: NicknameDupData,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: String
)
