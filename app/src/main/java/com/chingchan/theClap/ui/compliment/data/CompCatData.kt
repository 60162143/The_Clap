package com.chingchan.theClap.ui.compliment.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompCatData(
    @SerializedName("boardTypeId") var boardTypeId: Int? = null,
    @SerializedName("name") var name: String = "전체",
    @SerializedName("count") var count: Int = 0,
    @SerializedName("new") var new: Boolean = false,
    var isSelect: Boolean = false
) : Parcelable {
    override fun toString(): String {
        return "boardTypeId:$boardTypeId" +
                ", name:$name" +
                ", count:$count" +
                ", new:$new" +
                ", isSelect:$isSelect"
    }

    override fun equals(other: Any?): Boolean {
        return if(other is CompCatData){
            other.boardTypeId == this.boardTypeId
        }else{
            false
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
