package com.chingchan.theClap.ui.search.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchUserData(
    @SerializedName("id") var id: Int,
    @SerializedName("nickname") var nickname: String,
    @SerializedName("profile_image") var profile_image: String
): Parcelable{
    override fun toString(): String {
        return "id:$id" +
                ", nickname:$nickname" +
                ", profile_image:$profile_image"
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
