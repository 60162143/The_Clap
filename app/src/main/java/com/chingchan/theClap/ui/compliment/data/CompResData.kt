package com.chingchan.theClap.ui.compliment.data

import com.google.gson.annotations.SerializedName

data class CompResData(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: CompPageableResData,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: String
)
