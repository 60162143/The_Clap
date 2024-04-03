package com.chingchan.theClap.ui.compliment.data

import com.google.gson.annotations.SerializedName

data class PageResData(
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("sort") val sort: ArrayList<String>? = null
)
