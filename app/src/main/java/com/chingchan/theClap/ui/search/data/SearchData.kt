package com.chingchan.theClap.ui.search.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.chingchan.theClap.ui.login.data.UserData
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

object SearchData {
    private const val PREFERENCE_NAME = "searchData"

    private const val SEARCH_DATA = "searchContData"
    private const val IS_AUTO_SAVE = "isAutoSave"

    private val gson = GsonBuilder().create()
    private val searchDataListType: Type = object : TypeToken<ArrayList<SearchContData?>?>() {}.type // 이 부분이 중요하다. Json을 객체로 만들 때 타입을 추론하는 역할을 한다.

    // 검색 데이터 저장하기
    fun setSearchData(context: Context, schData: SearchContData) {

        val searchDataList = getSearchData(context)
        searchDataList.add(0, schData)

        val stringPrefs = gson.toJson(searchDataList, searchDataListType)


        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().putString(SEARCH_DATA, stringPrefs).apply()
    }

    // 검색 데이터 가져오기
    fun getSearchData(context: Context) : ArrayList<SearchContData> {
        val stringPrefs = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).getString(SEARCH_DATA, "") ?: ""

        return if(stringPrefs != ""){
            gson.fromJson(stringPrefs, searchDataListType)
        }else{
            ArrayList<SearchContData>()
        }

    }

    // 검색 데이터 삭제
    fun removeSearchData(context: Context, index: Int) {
        val searchDataList = getSearchData(context)
        searchDataList.removeAt(index)

        val stringPrefs = gson.toJson(searchDataList, searchDataListType)

        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().putString(SEARCH_DATA, stringPrefs).apply()
    }


    // 검색어 자동 저장 설정하기
    fun setAutoSave(context: Context, autoSaveType: Boolean) =
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().putBoolean(IS_AUTO_SAVE, autoSaveType).apply()

    // 검색어 자동 저장 가져오기
    fun getAutoSave(context: Context) : Boolean {
        return context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).getBoolean(IS_AUTO_SAVE, true)

    }


    // 전체 데이터 삭제
    fun removeAll(context: Context){
        context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE).edit().clear().apply()
    }
}

