package com.chingchan.theClap.ui.compliment.data

import com.google.gson.annotations.SerializedName

data class CompPageableResData(
    @SerializedName("content") val content: ArrayList<CompData>,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("last") val isLast: Boolean,
    @SerializedName("size") val pageSize: Int,
    @SerializedName("number") val pageNumber: Int
)
