package com.chingchan.theClap.ui.compliment.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompCatData(
    @SerializedName("boardTypeId") var boardTypeId: Int = 0,
    @SerializedName("name") var name: String = "전체",
    var count: Int = 0,
    var isSelect: Boolean = false,
    @SerializedName("createdTs") var createdTs: String = ""
) : Parcelable {
    override fun toString(): String {
        return "boardTypeId:$boardTypeId" +
                ", name:$name" +
                ", count:$count" +
                ", isSelect:$isSelect" +
                ", createdTs:$createdTs"
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
