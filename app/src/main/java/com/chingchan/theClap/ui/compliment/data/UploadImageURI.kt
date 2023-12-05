package com.chingchan.theClap.ui.compliment.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UploadImageURI(
    var uri: Uri
) : Parcelable {
    override fun toString(): String {
        return "uri:$uri"
    }

    override fun equals(other: Any?): Boolean {
        return if(other is UploadImageURI){
            other.uri == this.uri
        }else{
            false
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}