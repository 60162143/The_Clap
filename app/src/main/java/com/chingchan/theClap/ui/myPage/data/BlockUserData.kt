package com.chingchan.theClap.ui.myPage.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class BlockUserData(
    @SerializedName("id") var id: Int,
    @SerializedName("user") var user: Int?,
    @SerializedName("block") var block: Int?,
    @SerializedName("profile_image") var profile_image: String?,
    @SerializedName("introduction") var introduction: String?,
    @SerializedName("nickname") var nickname: String?,
): Parcelable {
    override fun toString(): String {
        return "id:$id" +
                ", user:$user" +
                ", block:$block" +
                ", profile_image:$profile_image" +
                ", introduction:$introduction" +
                ", nickname:$nickname"

    }
}
