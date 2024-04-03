package com.chingchan.theClap.ui.myPage.data

import android.os.Parcelable
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FAQData(
    @SerializedName("id") var id: Int,
    @SerializedName("title") var title: String,
    @SerializedName("content") var content: String,
    @SerializedName("createTs") var createTs: String,
    @SerializedName("modifyTs") var modifyTs: String,
    var isFaqContVisible: Boolean = false
): Parcelable {
    override fun toString(): String {
        return "id:$id" +
                ", title:$title" +
                ", content:$content" +
                ", createTs:$createTs" +
                ", modifyTs:$modifyTs" +
                ", isFaqContVisible:$isFaqContVisible"
    }

    override fun equals(other: Any?): Boolean {
        return if(other is FAQData){
            other.id == this.id
        }else{
            false
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}
