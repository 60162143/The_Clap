package com.chingchan.theClap.ui.myPage.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReportUserData(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("user") var user: Int = 0,  // 신고한 User Id
    @SerializedName("report") var report: Int = 0,  // 신고된 User Id
    @SerializedName("profile_image") var profile_image: String? = "",
    @SerializedName("introduction") var introduction: String? = "",
    @SerializedName("nickname") var nickname: String? = "",
    @SerializedName("reason") var reason: String? = ""  // 신고 이유
): Parcelable {
    override fun toString(): String {
        return "id:$id" +
                "user:$user" +
                ", report:$report" +
                ", profile_image:$profile_image" +
                ", introduction:$introduction" +
                ", nickname:$nickname" +
                ", reason:$reason"
    }
}
