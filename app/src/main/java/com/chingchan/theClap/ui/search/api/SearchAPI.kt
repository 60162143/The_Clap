package com.chingchan.theClap.ui.search.api

import com.chingchan.theClap.ui.compliment.data.CompResData
import com.chingchan.theClap.ui.search.data.SearchUserResData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface SearchAPI {
    // 게시글 검색
    @GET("boards/search")
    fun getSearchComp(
        @Header("Authorization") authorization: String,
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Call<CompResData>

    // 유저 검색
    @GET("usr/search")
    fun getSearchUser(
        @Header("Authorization") authorization: String,
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<SearchUserResData>
}