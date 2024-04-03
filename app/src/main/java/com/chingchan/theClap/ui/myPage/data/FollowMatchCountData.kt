package com.chingchan.theClap.ui.myPage.data

import android.os.Parcelable
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FollowMatchCountData(
    @SerializedName("followMatchCount") var followMatchCount: Int
): Parcelable {
    override fun toString(): String {
        return "followMatchCount:$followMatchCount"

    }
}
