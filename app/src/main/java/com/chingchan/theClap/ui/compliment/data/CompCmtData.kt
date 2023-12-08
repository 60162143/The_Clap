package com.chingchan.theClap.ui.compliment.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompCmtData(
    @SerializedName("boardId") var boardId: Int = 0,
    @SerializedName("content") var content: String = "",
    @SerializedName("parentId") var parentId: Int = 0,
    @SerializedName("groupOrder") var groupOrder: Int = 0,
    @SerializedName("groupLayer") var groupLayer: Int = 0
): Parcelable {
    override fun toString(): String {
        return "boardId:$boardId" +
                ", content:$content" +
                ", parentId:$parentId" +
                ", groupOrder:$groupOrder" +
                ", groupLayer:$groupLayer"
    }
}
