package com.chingchan.theClap.ui.compliment.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompCmtData(
    @SerializedName("commentId") var commentId: Int = 0,
    @SerializedName("boardId") var boardId: Int = 0,
    @SerializedName("content") var content: String = "",
    @SerializedName("userId") var userId: Int = 0,
    @SerializedName("nickname") var nickname: String = "",
    @SerializedName("profile") var profile: String? = "",
    @SerializedName("createTs") var createTs: String = "",
    @SerializedName("modifyTs") var modifyTs: String = "",
    @SerializedName("likes") var likes: Int = 0,
    @SerializedName("like") var like: Boolean = false
): Parcelable {
    override fun toString(): String {
        return "commentId:$commentId" +
                "boardId:$boardId" +
                ", content:$content" +
                ", userId:$userId" +
                ", nickname:$nickname" +
                ", profile:$profile" +
                ", createTs:$createTs" +
                ", modifyTs:$modifyTs" +
                ", likes:$likes" +
                ", like:$like"
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
