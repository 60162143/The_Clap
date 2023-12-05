package com.chingchan.theClap.globalFunction

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

object CheckPermission : AppCompatActivity() {
    private const val PERMISSION_ALL_REQUEST_CODE = 100

    // 초기 전체 권한 확인
    public fun checkPermission(context: Context) {
        val permission = mutableMapOf<String, String>()
        // 카메라 권한
        permission["camera"] = Manifest.permission.CAMERA

        // 저장소 권한
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){      // api 33 이상
            permission["storageMediaImage"] = Manifest.permission.READ_MEDIA_IMAGES
        }else{// api 29 이하
            permission["storageRead"] = Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){      // api 28이하, 이후로는 체크 X
            permission["storageWrite"] =  Manifest.permission.WRITE_EXTERNAL_STORAGE
        }

        // 연락처 권한
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){      // api 30 이상
            permission["readPhoneNumbers"] =  Manifest.permission.READ_PHONE_NUMBERS
        }else{      // api 29 이하
            permission["readPhoneState"] = Manifest.permission.READ_PHONE_STATE
        }

        // 알림 권한
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){      // api 33 이상
            permission["postNotifications"] =  Manifest.permission.POST_NOTIFICATIONS
        }

        // 현재 권한 상태 검사
        var denied = permission.count { ContextCompat.checkSelfPermission(context, it.value)  == PackageManager.PERMISSION_DENIED }

        Log.e("permission","여기 오냐?")

        // 동의하지 않은 권한 있을 경우
        if(denied > 0) {
            requestPermissions(permission.values.toTypedArray(), PERMISSION_ALL_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_ALL_REQUEST_CODE) {

            /* 1. 권한 확인이 다 끝난 후 동의하지 않은것이 있다면 종료
            var count = grantResults.count { it == PackageManager.PERMISSION_DENIED } // 동의 안한 권한의 개수
            if(count != 0) {
                Toast.makeText(applicationContext, "서비스의 필요한 권한입니다.\n권한에 동의해주세요.", Toast.LENGTH_SHORT).show()
                finish()
            } */

            /* 2. 권한 요청을 거부했다면 안내 메시지 보여주며 앱 종료 */
            grantResults.forEach {
                if(it == PackageManager.PERMISSION_DENIED) {
                    Log.e("authority", "denied!!")
                }else{
                    Log.e("authority", "agreement!!")
                }
            }
        }
    }
}