package com.chingchan.theClap.ui.notification.data

import com.google.gson.annotations.SerializedName

data class NotificationResData(
    @SerializedName("status") val status: String,
    @SerializedName("code") val code: String,
    @SerializedName("data") val data: ArrayList<NotificationData>,
    @SerializedName("message") val message: String?,
    @SerializedName("timestamp") val timestamp: String
)
