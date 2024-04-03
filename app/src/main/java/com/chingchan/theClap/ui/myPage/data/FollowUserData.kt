package com.chingchan.theClap.ui.myPage.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FollowUserData(
    @SerializedName("id") var id: Int,
    @SerializedName("followYN") var followYN: Boolean
): Parcelable {
    override fun toString(): String {
        return "id:$id" +
                ", followYN:$followYN"

    }
}
