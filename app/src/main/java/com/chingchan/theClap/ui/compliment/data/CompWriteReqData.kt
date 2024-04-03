package com.chingchan.theClap.ui.compliment.data

import com.google.gson.annotations.SerializedName

data class CompWriteReqData(
    @SerializedName("typeId") val typeId: Int,
    val typeName: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String
)
