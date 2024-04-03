package com.chingchan.theClap.ui.compliment.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CompImageData(
    var uri: String
) : Parcelable {
    override fun toString(): String {
        return "uri:$uri"
    }
}