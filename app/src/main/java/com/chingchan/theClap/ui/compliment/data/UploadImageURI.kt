package com.chingchan.theClap.ui.compliment.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File

@Parcelize
data class UploadImageURI(
    var uri: Uri,
    var isNew: Boolean = true,  // DB에 이미 저장되어 있는 경우 false, 새로 추가한 이미지인 경우 true
    var url: String = "", // DB에 이미 저장되어 있는 이미지의 경우 uri가 file://로 시작해서 이미지를 띄울수가 없기 때문에 url자체를 저장해서 url로 보여줘야함
    var file: File? = null // DB에 이미 저장되 있는 이미지의 경우 URL을 file로 미리 저장해놓음
) : Parcelable {
}