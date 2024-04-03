package com.chingchan.theClap.ui.search.data

import com.google.gson.annotations.SerializedName

data class SearchUserResData(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: SearchUserPageableResData,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: String
)
