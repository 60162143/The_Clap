package com.chingchan.theClap.ui.myPage.data

import android.content.Context

class AppLock(context: Context) {
    private var sharedPref = context.getSharedPreferences("appLock", Context.MODE_PRIVATE)

    // 잠금 설정
    fun setPassLock(password : String){
        sharedPref.edit().apply{
            putString("applock", password)
            apply()
        }
    }

    // 잠금 설정 제거
    fun removePassLock(){
        sharedPref.edit().apply{
            remove("applock")
            apply()
        }
    }


    // 잠금 해제 진행중인지에 대한 여부
    // 잠금 해제 진행중 여부 설정
    fun setDoingPassLock(isDoingFlag : Boolean){
        sharedPref.edit().apply{
            putBoolean("isDoingPassLock", isDoingFlag)
            apply()
        }
    }

    // 잠금 해제 진행중 여부 반환
    fun getDoingPassLock(): Boolean{
        return sharedPref.getBoolean("isDoingPassLock", false)
    }


    // 입력한 비밀번호가 맞는가?
    fun checkPassLock(password: String): Boolean {
        return sharedPref.getString("applock","0") == password
    }

    // 잠금 설정이 되어있는가?
    fun isPassLockSet(): Boolean {
        if(sharedPref.contains("applock")){
            return true
        }
        return false
    }


}