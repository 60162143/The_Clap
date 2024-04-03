package com.chingchan.theClap.ui.myPage.data

import com.google.gson.annotations.SerializedName

data class ProfileReqData(
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profile_image") val profile_image: String = "",
    @SerializedName("introduction") val introduction: String
)
