package com.chingchan.theClap.ui.search.data

import com.google.gson.annotations.SerializedName

data class SearchUserPageableResData(
    @SerializedName("content") val content: ArrayList<SearchUserData>,
    @SerializedName("totalElements") val totalElements: Int,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("last") val isLast: Boolean,
    @SerializedName("size") val pageSize: Int,
    @SerializedName("number") val pageNumber: Int
)
