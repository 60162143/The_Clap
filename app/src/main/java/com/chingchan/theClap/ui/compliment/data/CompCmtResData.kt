package com.chingchan.theClap.ui.compliment.data

import com.google.gson.annotations.SerializedName

data class CompCmtResData(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: CompCommentResData,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: String
)
