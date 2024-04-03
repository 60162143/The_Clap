package com.chingchan.theClap.ui.home.data

import com.chingchan.theClap.ui.compliment.data.CompData
import com.google.gson.annotations.SerializedName

data class CompRankingResData(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: ArrayList<CompData>,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: String
)
