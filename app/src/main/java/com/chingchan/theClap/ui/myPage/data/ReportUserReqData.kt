package com.chingchan.theClap.ui.myPage.data

import com.google.gson.annotations.SerializedName

data class ReportUserReqData(
    @SerializedName("report") val report: Int,  // 신고 받을 User ID
    @SerializedName("reason") val reason: String,   // 신고 사유
    @SerializedName("reportType") val reportType: String    // 신고 타입
)
