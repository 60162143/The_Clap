package com.chingchan.theClap.ui.compliment.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompData(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("nickname") var nickname: String = "",
    @SerializedName("profile") var profile: String = "",
    @SerializedName("typeId") var typeId: Int = 0,
    @SerializedName("typeName") var typeName: String = "",
    @SerializedName("image") var image: String = "",
    @SerializedName("title") var title: String = "",
    @SerializedName("content") var content: String = "",
    @SerializedName("visible") var visible: String = "",
    @SerializedName("views") var views: Int = 0,
    @SerializedName("likes") var likes: Int = 0,
    @SerializedName("createTs") var createTs: String = "",
    @SerializedName("modifyTs") var modifyTs: String = ""
): Parcelable {
    override fun toString(): String {
        return "id:$id" +
                ", nickname:$nickname" +
                ", profile:$profile" +
                ", typeId:$typeId" +
                ", typeName:$typeName" +
                ", image:$image" +
                ", title:$title" +
                ", content:$content" +
                ", visible:$visible" +
                ", views:$views" +
                ", likes:$likes" +
                ", createTs:$createTs" +
                ", modifyTs:$modifyTs"
    }
}
