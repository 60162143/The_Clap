package com.chingchan.theClap.globalFunction

import android.content.Intent
import android.os.Build
import android.os.Parcelable
import java.io.Serializable

object GlobalFunction {

    fun <T: Serializable> getSerializableExtra(intent: Intent, key: String, clazz: Class<T>): T?{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(key, clazz)
        } else {
            intent.getSerializableExtra(key) as T?
        }
    }

    fun <T: Parcelable> getParcelableExtra(intent: Intent, key: String, clazz: Class<T>): T?{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(key, clazz)
        } else {
            intent.getParcelableExtra(key) as T?
        }
    }
}