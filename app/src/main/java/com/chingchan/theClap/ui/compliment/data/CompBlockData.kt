package com.chingchan.theClap.ui.compliment.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompBlockData(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("boardId") var boardId: Int = 0,
    @SerializedName("blocked") var blocked: String = "",
    @SerializedName("createTs") var createTs: String = "",
    @SerializedName("modifyTs") var modifyTs: String = ""
): Parcelable {
    override fun toString(): String {
        return "id:$id" +
                "boardId:$boardId" +
                ", blocked:$blocked" +
                ", createTs:$createTs" +
                ", modifyTs:$modifyTs"
    }
}
