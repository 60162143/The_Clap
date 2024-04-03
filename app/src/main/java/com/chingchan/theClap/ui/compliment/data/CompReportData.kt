package com.chingchan.theClap.ui.compliment.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompReportData(
    @SerializedName("id") var id: Int = 0,
    @SerializedName("reportedUser") var reportedUser: Int = 0,
    @SerializedName("boardId") var boardId: Int = 0,
    @SerializedName("commentId") var commentId: Int? = 0,
    @SerializedName("reportTarget") var reportTarget: String = "",
    @SerializedName("report") var report: String = "",
    @SerializedName("reportState") var reportState: String = "",
    @SerializedName("createTs") var createTs: String = "",
    @SerializedName("modifyTs") var modifyTs: String = ""
): Parcelable {
    override fun toString(): String {
        return "id:$id" +
                "reportedUser:$reportedUser" +
                ", boardId:$boardId" +
                ", commentId:$commentId" +
                ", reportTarget:$reportTarget" +
                ", report:$report" +
                ", reportState:$reportState" +
                ", createTs:$createTs" +
                ", modifyTs:$modifyTs"
    }
}
