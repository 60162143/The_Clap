package com.chingchan.theClap.ui.notification.data

import android.os.Parcelable
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationData(
    @SerializedName("id") var id: Int,
    @SerializedName("userId") var userId: Int,
    @SerializedName("logTypeId") var logTypeId: Int,
    @SerializedName("boardId") var boardId: Int = 0,
    @SerializedName("commentId") var commentId: Int = 0,
    @SerializedName("userLogActiveType") var userLogActiveType: String,
    @SerializedName("target") var target: String? = "",
    @SerializedName("message") var message: String,
    @SerializedName("isRead") var isRead: String,
    @SerializedName("createTs") var createTs: String,
    @SerializedName("followYN") var followYN: Boolean,
    @SerializedName("targetUserId") var targetUserId: Int   // 나를 팔로우한 유저의 ID
): Parcelable
