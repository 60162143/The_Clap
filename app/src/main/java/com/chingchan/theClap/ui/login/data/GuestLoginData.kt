package com.chingchan.theClap.ui.login.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GuestLoginData(
    @SerializedName("nickname") var nickname: String,
    @SerializedName("kakao") var kakao: String?,
    @SerializedName("apple") var apple: String?,
    @SerializedName("goole") var goole: String?,
    @SerializedName("guest") var guest: String
): Parcelable {
}
