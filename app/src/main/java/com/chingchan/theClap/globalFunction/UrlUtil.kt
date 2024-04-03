package com.chingchan.theClap.globalFunction

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object UrlUtil {
    fun downloadFile(context: Context, url: String): File{
        val fileName = UriUtil.getFileName(context, Uri.parse(url))
        val file = FileUtil.createTempFile(context, fileName)

        FileUtil.save(context, url, file)
        return File(file.absolutePath)
    }
}