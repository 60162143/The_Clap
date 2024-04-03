package com.chingchan.theClap.globalFunction

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object FileUtil {
    // 임시 파일 생성
    fun createTempFile(context: Context, fileName: String): File {
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(storageDir, fileName)
    }

    // 압축 함수 추가
    fun compressAndSave(context: Context, uri: Uri, file: File) {
        if(uri.scheme == "content"){
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            val outputStream = FileOutputStream(file)

            // 압축할 품질과 함께 Bitmap을 압축하여 저장
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)

            outputStream.flush()
            outputStream.close()
        }
    }

    // 압축 함수 추가
    fun save(context: Context, url: String, file: File) {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()

        val inputStream = connection.inputStream
        val bitmap = BitmapFactory.decodeStream(inputStream)

        val outputStream = FileOutputStream(file)

        // 압축할 품질과 함께 Bitmap을 압축하여 저장
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        outputStream.flush()
        outputStream.close()
    }
}