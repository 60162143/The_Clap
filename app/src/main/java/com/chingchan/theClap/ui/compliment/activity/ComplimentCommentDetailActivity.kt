package com.chingchan.theClap.ui.compliment.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.databinding.ActiCompCmtDetailBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.adapter.CompCmtDetailRecyclerAdapter
import com.chingchan.theClap.ui.compliment.data.CompCmtData
import com.chingchan.theClap.ui.compliment.data.CompCmtResData
import com.chingchan.theClap.ui.compliment.data.CompCmtWriteReqData
import com.chingchan.theClap.ui.compliment.data.CompCmtWriteResData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompDetResData
import com.chingchan.theClap.ui.compliment.data.CompReportReqData
import com.chingchan.theClap.ui.compliment.data.CompReportResData
import com.chingchan.theClap.ui.compliment.dialog.CompCmtDetailReportDialog
import com.chingchan.theClap.ui.compliment.dialog.CompCmtEditDialog
import com.chingchan.theClap.ui.compliment.dialog.CompCmtEditOtherDialog
import com.chingchan.theClap.ui.compliment.dialog.CompCmtReportDialog
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.activity.MyPageProfileOtherActivity
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplimentCommentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActiCompCmtDetailBinding

    private lateinit var compCmtDetailRecyclerAdapter: CompCmtDetailRecyclerAdapter

    private val editCmtDialog: CompCmtEditDialog by lazy { CompCmtEditDialog(this) } // 댓글 수정(본인) 다이얼로그
    private val editCmtOtherDialog: CompCmtEditOtherDialog by lazy { CompCmtEditOtherDialog(this) } // 댓글 수정(다른 사람) 다이얼로그
    private val reportCmtDialog: CompCmtDetailReportDialog by lazy { CompCmtDetailReportDialog(this) } // 댓글 신고하기 다이얼로그

    private lateinit var compData: CompData // 게시글 데이터

    private var curCmtPosition = -1 // 현재 선택된 댓글의 position
    private var curCmtPage = 0
    private var isCmtPageLast = false
    private var curCmtSort = "" // 정렬 방법 -> 최신순 : "", 오래된순 : createTs

    // 댓글 수정화면 Intent registerForActivityResult
    private val cmtEditResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼 클릭
            clearAndSetCmtData()    // 댓글 데이터 초기화
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiCompCmtDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compData = GlobalFunction.getParcelableExtra(intent, "compData", CompData::class.java)!!    // 게시글 데이터

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){
            compCmtDetailRecyclerAdapter = CompCmtDetailRecyclerAdapter()
            rvComment.adapter = compCmtDetailRecyclerAdapter

            getCompCmt(compData.id, curCmtPage, curCmtSort)    // 댓글 데이터 GET

            // 댓글 클릭 리스너
            compCmtDetailRecyclerAdapter.clickListener = object : CompCmtDetailRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "USER_IMAGE"){
                        curCmtPosition = position  // 현재 누른 위치의 position 갱신

                        val newList = compCmtDetailRecyclerAdapter.differ.currentList.toMutableList()    // 게시글 데이터

                        getProfileData(newList[curCmtPosition].userId) // 프로필 데이터 조회
                    }else if(type == "MORE"){
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtDetailRecyclerAdapter.differ.currentList.toMutableList()

                        curCmtPosition = position   // 현재 선택한 위치

                        if(newList[position].userId == UserData.getUserId(applicationContext)){
                            editCmtDialog.show()
                        }else{
                            editCmtOtherDialog.show()
                        }
                    }else if(type == "HEART"){
                        curCmtPosition = position   // 현재 선택한 위치

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtDetailRecyclerAdapter.differ.currentList.toMutableList()

                        likeCompCmt(newList[position].commentId)
                    }
                }
            }

            // 댓글 수정(본인) 다이얼로그 클릭 리스너
            editCmtDialog.setListener(object : CompCmtEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "EDIT"){
                        editCmtDialog.dismiss()

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtDetailRecyclerAdapter.differ.currentList.toMutableList()

                        // 게시글 상세 Activity로 화면 전환
                        val intent = Intent(this@ComplimentCommentDetailActivity, ComplimentCommentEditActivity::class.java)
                        intent.putExtra("compCmtData", newList[curCmtPosition])
                        cmtEditResult.launch(intent)


                    }else if(type == "DELETE"){
                        editCmtDialog.dismiss()

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtDetailRecyclerAdapter.differ.currentList.toMutableList()

                        deleteCompCmt(newList[curCmtPosition].commentId)
                    }else if(type == "CANCEL"){
                        editCmtDialog.dismiss()
                    }
                }
            })

            // 댓글 수정(다른 사람) 다이얼로그 클릭 리스너
            editCmtOtherDialog.setListener(object : CompCmtEditOtherDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "REPORT"){
                        editCmtOtherDialog.dismiss()

                        reportCmtDialog.show()
                    }else if(type == "CANCEL"){
                        editCmtOtherDialog.dismiss()
                    }
                }
            })

            // 댓글 신고하기 다이얼로그 클릭 리스너
            reportCmtDialog.setListener(object : CompCmtDetailReportDialog.OnClickListener{
                override fun onClick(type: String, reportType: String, reportReason: String) {
                    if(type == "REPORT") {
                        reportCmtDialog.dismiss()

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtDetailRecyclerAdapter.differ.currentList.toMutableList()

                        // 댓글 신고
                        reportComplimentComment(CompReportReqData(compData.userId, reportType, reportReason), newList[curCmtPosition].commentId)

                    }else if(type == "CANCEL"){
                        reportCmtDialog.dismiss()
                    }
                }
            })

            // 댓글 작성 버튼 클릭 리스너
            btnConfirm.setOnClickListener(View.OnClickListener {
                if(editCmt.text.isNotEmpty()){
                    writeComment(CompCmtWriteReqData(
                        content = editCmt.text.toString()
                    ))

                    editCmt.setText("")

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }else{
                    customToast.showCustomToast("댓글을 입력해 주세요.", this@ComplimentCommentDetailActivity)
                }
            })

            // '이전 댓글 더보기' 버튼 클릭 리스너
            btnCmtMore.setOnClickListener(View.OnClickListener {
                getCompCmt(compData.id, curCmtPage, curCmtSort)    // 댓글 데이터 GET
            })

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                setResult(1000, intent)
                finish()
            })
        }
    }

    override fun onResume() {
        super.onResume()

    }

    // 게시글 댓글 조회
    private fun getCompCmt(boardId: Int, cmtPage: Int, cmtSort: String){
        val apiObjectCall = ApiObject.getCompService.getCompCmt(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = boardId, page = cmtPage, size = 10, sort = cmtSort)

        apiObjectCall.enqueue(object: Callback<CompCmtResData> {
            override fun onResponse(call: Call<CompCmtResData>, response: Response<CompCmtResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val compCmtDataList = response.body()?.data?.content!!

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtDetailRecyclerAdapter.differ.currentList.toMutableList()


                        if(newList.isNotEmpty()){   // 댓글이 있을 경우
                            compCmtDetailRecyclerAdapter.differ.submitList(newList.toMutableList() + compCmtDataList)
                        }else{  // 댓글이 없을 경우
                            compCmtDetailRecyclerAdapter.differ.submitList(compCmtDataList.toMutableList())
                        }

                        isCmtPageLast = response.body()?.data!!.isLast  // 마지막 페이지인지 여부

                        if(!isCmtPageLast){
                            binding.btnCmtMore.visibility = View.VISIBLE    // 댓글 더보기 버튼 보이기
                            curCmtPage += 1 // 다음 페이지 +1
                        }else{
                            binding.btnCmtMore.visibility = View.GONE   // 댓글 더보기 버튼 숨기기
                        }

                    }else{
                        customToast.showCustomToast("댓글을 불러오지 못했습니다.", this@ComplimentCommentDetailActivity)
                    }

                }else{
                    customToast.showCustomToast("댓글을 불러오지 못했습니다.", this@ComplimentCommentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompCmtResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentCommentDetailActivity)
            }
        })
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(1000, intent)
            finish()
        }
    }

    // 댓글 작성
    private fun writeComment(compCmtWriteReqData: CompCmtWriteReqData){
        val apiObjectCall = ApiObject.getCompService.writeCompCmt(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id, req = compCmtWriteReqData)

        apiObjectCall.enqueue(object: Callback<CompCmtWriteResData> {
            override fun onResponse(call: Call<CompCmtWriteResData>, response: Response<CompCmtWriteResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        clearAndSetCmtData()
                    }else{
                        customToast.showCustomToast("댓글을 작성하지 못했습니다.", this@ComplimentCommentDetailActivity)
                    }

                }else{
                    customToast.showCustomToast("댓글을 작성하지 못했습니다.", this@ComplimentCommentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompCmtWriteResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentCommentDetailActivity)
            }
        })
    }

    // 댓글 삭제
    private fun deleteCompCmt(cmtId: Int){
        val apiObjectCall = ApiObject.getCompService.deleteCompCmt(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id , commentId = cmtId)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        customToast.showCustomToast("댓글이 삭제 되었습니다.", this@ComplimentCommentDetailActivity)

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtDetailRecyclerAdapter.differ.currentList.toMutableList()
                        newList.removeAt(curCmtPosition)
                        compCmtDetailRecyclerAdapter.differ.submitList(newList.toMutableList())

                    }else{
                        customToast.showCustomToast("댓글을 삭제 하지 못했습니다.", this@ComplimentCommentDetailActivity)
                    }

                }else{
                    customToast.showCustomToast("댓글을 삭제 하지 못했습니다.", this@ComplimentCommentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentCommentDetailActivity)
            }
        })
    }

    // 댓글 좋아요 등록 / 등록 해제
    private fun likeCompCmt(cmtId: Int){
        val apiObjectCall = ApiObject.getCompService.likeCompCmt(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id , commentId = cmtId)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtDetailRecyclerAdapter.differ.currentList.toMutableList()

                        var isLikeCompCmt = !newList[curCmtPosition].like
                        var heartCnt = newList[curCmtPosition].likes

                        if(isLikeCompCmt){
                            heartCnt += 1
                        }else{
                            heartCnt -= 1
                        }

                        var newData = newList[curCmtPosition].copy(like = isLikeCompCmt, likes = heartCnt)

                        newList[curCmtPosition] = newData

                        compCmtDetailRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("댓글 좋아요 등록/해제 하지 못했습니다.", this@ComplimentCommentDetailActivity)
                    }

                }else{
                    customToast.showCustomToast("댓글 좋아요 등록/해제 하지 못했습니다.", this@ComplimentCommentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentCommentDetailActivity)
            }
        })
    }

    // 게시글 댓글 신고
    private fun reportComplimentComment(reportResData: CompReportReqData, commentId: Int){
        val apiObjectCall = ApiObject.getCompService.reportCompCmt(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id, commentId = commentId, reportResData)

        apiObjectCall.enqueue(object: Callback<CompReportResData> {
            override fun onResponse(call: Call<CompReportResData>, response: Response<CompReportResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        customToast.showCustomToast("댓글이 신고 되었습니다.", this@ComplimentCommentDetailActivity)
                    }
                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<CompReportResData>(
                        CompReportResData::class.java,
                        CompReportResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00035.name){    // 이미 신고한 댓글
                            customToast.showCustomToast(ErrorCode.S00035.message, this@ComplimentCommentDetailActivity)
                        }else{
                            customToast.showCustomToast("게시물이 신고 되지 못했습니다.", this@ComplimentCommentDetailActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CompReportResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentCommentDetailActivity)
            }
        })
    }

    // 내 프로필 정보 조회
    private fun getProfileData(userId: Int){
        val apiObjectCall = ApiObject.getMyPageService.getProfile(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , id = userId)

        apiObjectCall.enqueue(object: Callback<ProfileResData> {
            override fun onResponse(call: Call<ProfileResData>, response: Response<ProfileResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val profileData = response.body()?.data!!

                        val intent = Intent(this@ComplimentCommentDetailActivity, MyPageProfileOtherActivity::class.java)
                        intent.putExtra("profileData", profileData)

                        startActivity(intent)
                    }else{
                        customToast.showCustomToast("프로필 정보 조회를 못했습니다.", this@ComplimentCommentDetailActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<ProfileResData>(
                        ProfileResData::class.java,
                        ProfileResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00011.name){    // 프로필 데이터 조회 x ( 차단 or 삭제...?)
                            customToast.showCustomToast(ErrorCode.S00011.message, this@ComplimentCommentDetailActivity)
                        }else{
                            customToast.showCustomToast("프로필 정보 조회를 못했습니다.", this@ComplimentCommentDetailActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentCommentDetailActivity)
            }
        })
    }

    private fun clearAndSetCmtData(){
        // 댓글 초기화
        curCmtPage = 0

        compCmtDetailRecyclerAdapter.differ.submitList(null)

        getCompCmt(compData.id, curCmtPage, curCmtSort)    // 댓글 데이터 GET
    }
}