package com.chingchan.theClap.ui.myPage.data

import android.os.Parcelable
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProfileData(
    @SerializedName("id") var id: Int,
    @SerializedName("email") var email: String = "",
    @SerializedName("phone") var phone: String? = "",
    @SerializedName("name") var name: String,
    @SerializedName("google") var google: String? = "",
    @SerializedName("naver") var naver: String? = "",
    @SerializedName("kakao") var kakao: String? = "",
    @SerializedName("nickname") var nickname: String,
    @SerializedName("profile_image") var profile_image: String,
    @SerializedName("introduction") var introduction: String,
    @SerializedName("follow") var follow: Boolean,
    @SerializedName("following") var following: Int,
    @SerializedName("follower") var follower: Int
): Parcelable {
    override fun toString(): String {
        return "id:$id" +
                ", email:$email" +
                ", phone:$phone" +
                ", name:$name" +
                ", google:$google" +
                ", naver:$naver" +
                ", kakao:$kakao" +
                ", nickname:$nickname" +
                ", profile_image:$profile_image" +
                ", introduction:$introduction" +
                ", follow:$follow" +
                ", following:$following" +
                ", follower:$follower"

    }

    override fun equals(other: Any?): Boolean {
        return if(other is ProfileData){
            other.id == this.id
        }else{
            false
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
