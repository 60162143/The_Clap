package com.chingchan.theClap.ui.compliment.data

import com.google.gson.annotations.SerializedName

data class CompCatCountResData(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: ArrayList<CompCatData>,
    @SerializedName("message") val message: String,
    @SerializedName("timestamp") val timestamp: String
)
