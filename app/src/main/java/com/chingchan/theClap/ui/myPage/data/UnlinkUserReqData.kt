package com.chingchan.theClap.ui.myPage.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UnlinkUserReqData(
    @SerializedName("deleteType") var deleteType: String,
    @SerializedName("deleteReason") var deleteReason: String
): Parcelable {
}
