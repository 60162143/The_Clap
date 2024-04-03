package com.chingchan.theClap.ui.myPage.data

import com.google.gson.annotations.SerializedName

data class ProfileEditReqData(
    @SerializedName("email") val email: String = "",
    @SerializedName("phone") val phone: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("nickname") val nickname: String = "",
    @SerializedName("profile_image") val profile_image: String = "",
    @SerializedName("introduction") val introduction: String = "",
    @SerializedName("userType") val userType: String = ""
)
