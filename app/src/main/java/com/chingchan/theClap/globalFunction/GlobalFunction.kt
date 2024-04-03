package com.chingchan.theClap.globalFunction

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import java.io.Serializable

object GlobalFunction {

    // 객체 전당
    fun <T: Serializable> getSerializableExtra(intent: Intent, key: String, clazz: Class<T>): T?{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU 이상 버전
            intent.getSerializableExtra(key, clazz)
        } else {    // TIRABIMU 이하 버전
            intent.getSerializableExtra(key) as T?
        }
    }

    // 리스트 전달
    fun <T: Parcelable> getParcelableExtra(intent: Intent, key: String, clazz: Class<T>): T?{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU 이상 버전
            intent.getParcelableExtra(key, clazz)
        } else {    // TIRABIMU 이하 버전
            intent.getParcelableExtra(key) as T?
        }
    }
}