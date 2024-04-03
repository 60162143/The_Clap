package com.chingchan.theClap.ui.myPage.data

import android.os.Parcelable
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class AnnounceData(
    @SerializedName("id") var id: Int,
    @SerializedName("title") var title: String,
    @SerializedName("content") var content: String,
    @SerializedName("createTs") var createTs: String,
    @SerializedName("modifyTs") var modifyTs: String,
    var isAnnounceContVisible: Boolean = false
): Parcelable {
    override fun equals(other: Any?): Boolean {
        return if(other is AnnounceData){
            other.id == this.id
        }else{
            false
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
