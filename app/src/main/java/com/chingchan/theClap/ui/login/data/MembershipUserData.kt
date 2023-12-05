package com.chingchan.theClap.ui.login.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MembershipUserData(
    var email: String = "",
    var phone: String = "",
    var name: String = "",
    var nickname: String = "",
    var profile_image: String = "",
    var introduction: String = "",
    var loginType: String,
    var userType: String = "",
    var socialId: String = ""
) : Parcelable {
    override fun toString(): String {
        return "email:$email" +
                ", phone:$phone" +
                ", name:$name" +
                ", nickname:$nickname" +
                ", profile_image:$profile_image" +
                ", introduction:$introduction" +
                ", loginType:$loginType" +
                ", userType:$userType" +
                ", socialId:$socialId"
    }
}
