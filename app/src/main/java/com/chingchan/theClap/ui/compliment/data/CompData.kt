package com.chingchan.theClap.ui.compliment.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompData(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("userId") var userId: Int = 0,
    @SerializedName("nickname") var nickname: String? = "" ?: "",
    @SerializedName("profile") var profile: String? = "" ?: "",
    @SerializedName("typeId") var typeId: Int = 0,
    @SerializedName("typeName") var typeName: String = "",
    @SerializedName("image") var image: ArrayList<String>,
    @SerializedName("title") var title: String = "",
    @SerializedName("content") var content: String = "",
    @SerializedName("visible") var visible: String? = "",
    @SerializedName("views") var views: Int = 0,
    @SerializedName("likes") var likes: Int = 0,
    @SerializedName("comments") var comments: Int = 0,
    @SerializedName("createTs") var createTs: String = "",
    @SerializedName("modifyTs") var modifyTs: String = "",
    @SerializedName("best") var best: Boolean = false,
    @SerializedName("like") var like: Boolean = false,
    @SerializedName("bookmark") var bookmark: Boolean = false,
    @SerializedName("rank") var rank: Int = 0,
    var editFlag: Boolean = false   // 마이페이지 칭찬 내역 조회에서 편집 가능한지에 대한 flag 변수
): Parcelable {
    override fun toString(): String {
        return "id:$id" +
                ", userId:$userId" +
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
                ", comments:$comments" +
                ", createTs:$createTs" +
                ", modifyTs:$modifyTs" +
                ", best:$best" +
                ", like:$like" +
                ", bookmark:$bookmark" +
                ", rank:$rank" +
                ", editFlag:$editFlag"

    }
}
