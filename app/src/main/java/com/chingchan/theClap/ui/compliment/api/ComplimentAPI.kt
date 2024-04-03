package com.chingchan.theClap.ui.compliment.api

import com.chingchan.theClap.ui.compliment.data.CompBlockResData
import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.compliment.data.CompCmtResData
import com.chingchan.theClap.ui.compliment.data.CompCmtWriteReqData
import com.chingchan.theClap.ui.compliment.data.CompCmtWriteResData
import com.chingchan.theClap.ui.compliment.data.CompResData
import com.chingchan.theClap.ui.compliment.data.CompDetResData
import com.chingchan.theClap.ui.compliment.data.CompOneResData
import com.chingchan.theClap.ui.compliment.data.CompReportReqData
import com.chingchan.theClap.ui.compliment.data.CompReportResData
import com.chingchan.theClap.ui.compliment.data.CompWriteReqData
import com.chingchan.theClap.ui.compliment.data.CompWriteResData
import com.chingchan.theClap.ui.home.data.CompRankingResData
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

interface ComplimentAPI {
    // 카테고리 목록 조회
    @GET("boards/board-type/count")
    fun getCompCat(
        @Header("Authorization") authorization: String
    ): Call<CompCatResData>

    // 게시글 목록 조회
    @GET("boards")
    fun getComp(
        @Header("Authorization") authorization: String,
        @Query("typeId") typeId: Int? = null,
        @Query("userId") userId: Int? = null,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<CompResData>

    // 게시글 단건 조회
    @GET("boards/{boardId}")
    fun getOneComp(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int
    ): Call<CompOneResData>

    // 게시글 댓글 목록 조회
    @GET("boards/{boardId}/comments")
    fun getCompCmt(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Call<CompCmtResData>

    // 게시글 댓글 작성
    @POST("boards/{boardId}/comment")
    fun writeCompCmt(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int,
        @Body req : CompCmtWriteReqData
    ): Call<CompCmtWriteResData>

    // 게시글 댓글 수정
    @PUT("boards/{boardId}/comments/{commentId}")
    fun editCompCmt(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int,
        @Path("commentId") commentId: Int,
        @Body req : CompCmtWriteReqData
    ): Call<CompCmtWriteResData>

    // 게시글 댓글 삭제
    @DELETE("boards/{boardId}/comments/{commentId}")
    fun deleteCompCmt(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int,
        @Path("commentId") commentId: Int
    ): Call<CompDetResData>

    // 게시글 댓글 신고
    @POST("boards/{boardId}/comments/{commentId}/report")
    fun reportCompCmt(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int,
        @Path("commentId") commentId: Int,
        @Body req : CompReportReqData
    ): Call<CompReportResData>

    // 게시글 댓글 좋아요 등록 / 등록 해제
    @POST("boards/{boardId}/comments/{commentId}/like")
    fun likeCompCmt(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int,
        @Path("commentId") commentId: Int
    ): Call<CompDetResData>

    // 게시글 조회수 증가
    @POST("boards/{boardId}/view")
    fun increaseView(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int
    ): Call<CompDetResData>

    // 게시글 숨김
    @PUT("boards/{boardId}/hide")
    fun hideComp(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int
    ): Call<CompDetResData>

    // 게시글 작성
    @Multipart
    @POST("boards")
    fun writeComp(
        @Header("Authorization") authorization: String,
        @Part("boardDto") boardDto: CompWriteReqData,
        @Part images : List<MultipartBody.Part?>
    ): Call<CompWriteResData>

    // 게시글 수정
    @Multipart
    @PUT("boards/{boardId}")
    fun editComp(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int,
        @Part("boardDto") boardDto: CompWriteReqData,
        @Part images : List<MultipartBody.Part?>
    ): Call<CompWriteResData>

    // 게시글 삭제
    @DELETE("boards/{boardId}")
    fun deleteComp(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int
    ): Call<CompDetResData>

    // 게시글 차단 / 차단해제
    @POST("boards/{boardId}/block")
    fun blockComp(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int
    ): Call<CompBlockResData>

    // 게시글 신고
    @POST("boards/{boardId}/report")
    fun reportComp(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int,
        @Body req : CompReportReqData
    ): Call<CompReportResData>

    // 게시글 좋아요 등록 / 등록해제
    @POST("boards/{boardId}/like")
    fun likeComp(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int
    ): Call<CompDetResData>

    // 게시글 북마크 등록 / 등록해제
    @POST("boards/{boardId}/bookmark")
    fun bookmarkComp(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int
    ): Call<CompDetResData>

    // 게시글 랭킹 조회
    @GET("boards/ranking/weekly")
    fun getCompRanking(
        @Header("Authorization") authorization: String
    ): Call<CompRankingResData>
}