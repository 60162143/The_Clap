package com.chingchan.theClap.ui.compliment.data

import com.google.gson.annotations.SerializedName

data class CompReportReqData(
    @SerializedName("reportedUser") val reportedUser: Int,
    @SerializedName("reportType") val reportType: String,
    @SerializedName("report") val report: String
)
