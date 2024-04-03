package com.chingchan.theClap.ui.myPage.data

import com.google.gson.annotations.SerializedName

data class FollowUserReqData(
    @SerializedName("user") val user: Int,  // 유저 ID
    @SerializedName("follow") val follow: Int   // 팔로우할 유저 ID
)
