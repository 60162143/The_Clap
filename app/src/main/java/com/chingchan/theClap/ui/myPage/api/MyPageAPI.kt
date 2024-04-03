package com.chingchan.theClap.ui.myPage.api

import com.chingchan.theClap.ui.compliment.data.CompResData
import com.chingchan.theClap.ui.login.data.GuestLoginUserResData
import com.chingchan.theClap.ui.myPage.data.AnnounceResData
import com.chingchan.theClap.ui.myPage.data.CompBookmarkResData
import com.chingchan.theClap.ui.myPage.data.FAQResData
import com.chingchan.theClap.ui.myPage.data.FollowMatchCountResData
import com.chingchan.theClap.ui.myPage.data.NicknameDupReqData
import com.chingchan.theClap.ui.myPage.data.NicknameDupResData
import com.chingchan.theClap.ui.myPage.data.ProfileReqData
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.myPage.data.BlockUserReqData
import com.chingchan.theClap.ui.myPage.data.BlockUserResData
import com.chingchan.theClap.ui.myPage.data.DeletePostsResData
import com.chingchan.theClap.ui.myPage.data.FollowUserReqData
import com.chingchan.theClap.ui.myPage.data.FollowUserResData
import com.chingchan.theClap.ui.myPage.data.ReportUserReqData
import com.chingchan.theClap.ui.myPage.data.ReportUserResData
import com.chingchan.theClap.ui.myPage.data.UnlinkUserReqData
import com.chingchan.theClap.ui.myPage.data.UnlinkUserResData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MyPageAPI {
    // 북마크한 게시글 조회
    @GET("boards/bookmark")
    fun getBookmarkComp(
        @Header("Authorization") authorization: String
    ): Call<CompBookmarkResData>

    // FAQ 조회
    @GET("faq")
    fun getFAQ(
        @Header("Authorization") authorization: String
    ): Call<FAQResData>

    // 공지사항 조회
    @GET("notice")
    fun getAnnounce(
        @Header("Authorization") authorization: String
    ): Call<AnnounceResData>

    // 내 프로필 정보 조회
    @GET("usr")
    fun getProfile(
        @Header("Authorization") authorization: String,
        @Query("id") id: Int,
    ): Call<ProfileResData>

    // 내 프로필 수정
    @POST("usr")
    fun editProfile(
        @Header("Authorization") authorization: String,
        @Body req : ProfileReqData
    ): Call<ProfileResData>

    // 닉네임 중복 조회
    @POST("login/nicknameCheck")
    fun isNicknameDup(
        @Header("Authorization") authorization: String,
        @Body req : NicknameDupReqData
    ): Call<NicknameDupResData>

    // 작성한 칭찬 내역 조회
    @GET("posts")
    fun getPosts(
        @Header("Authorization") authorization: String,
        @Query("userId") userId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<CompResData>

    // 작성한 칭찬 내역 삭제
    @DELETE("posts/{boardId}")
    fun deletePosts(
        @Header("Authorization") authorization: String,
        @Path("boardId") boardId: Int
    ): Call<DeletePostsResData>

    // 서로 팔로잉한 수 조회
    @GET("usr/follow/matchcount")
    fun getFollowMatchCount(
        @Header("Authorization") authorization: String
    ): Call<FollowMatchCountResData>

    // 유저 차단
    @POST("usr/block")
    fun blockUser(
        @Header("Authorization") authorization: String,
        @Body req : BlockUserReqData
    ): Call<BlockUserResData>

    // 유저 신고
    @POST("usr/report")
    fun reportUser(
        @Header("Authorization") authorization: String,
        @Body req : ReportUserReqData
    ): Call<ReportUserResData>

    // 팔로우
    @POST("usr/follow")
    fun followUser(
        @Header("Authorization") authorization: String,
        @Body req : FollowUserReqData
    ): Call<FollowUserResData>

    // 계정 탈퇴
    @POST("usr/delete")
    fun unlinkUser(
        @Header("Authorization") authorization: String,
        @Body req : UnlinkUserReqData
    ): Call<UnlinkUserResData>
}