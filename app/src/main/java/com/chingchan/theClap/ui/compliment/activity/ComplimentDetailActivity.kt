package com.chingchan.theClap.ui.compliment.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiCompDetailBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.adapter.CompCmtRecyclerAdapter
import com.chingchan.theClap.ui.compliment.adapter.CompDetailImageRecyclerAdapter
import com.chingchan.theClap.ui.compliment.adapter.CompImageRecyclerAdapter
import com.chingchan.theClap.ui.compliment.data.CompBlockResData
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCmtData
import com.chingchan.theClap.ui.compliment.data.CompCmtResData
import com.chingchan.theClap.ui.compliment.data.CompCmtWriteReqData
import com.chingchan.theClap.ui.compliment.data.CompCmtWriteResData
import com.chingchan.theClap.ui.compliment.data.CompCommentResData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompDetResData
import com.chingchan.theClap.ui.compliment.data.CompImageData
import com.chingchan.theClap.ui.compliment.data.CompReportReqData
import com.chingchan.theClap.ui.compliment.data.CompReportResData
import com.chingchan.theClap.ui.compliment.data.PageResData
import com.chingchan.theClap.ui.compliment.dialog.CompBlockDialog
import com.chingchan.theClap.ui.compliment.dialog.CompCmtEditDialog
import com.chingchan.theClap.ui.compliment.dialog.CompCmtEditOtherDialog
import com.chingchan.theClap.ui.compliment.dialog.CompCmtReportDialog
import com.chingchan.theClap.ui.compliment.dialog.CompCmtSortDialog
import com.chingchan.theClap.ui.compliment.dialog.CompDeleteDialog
import com.chingchan.theClap.ui.compliment.dialog.CompEditDialog
import com.chingchan.theClap.ui.compliment.dialog.CompEditOtherDialog
import com.chingchan.theClap.ui.compliment.dialog.CompHideDialog
import com.chingchan.theClap.ui.compliment.dialog.CompReportDialog
import com.chingchan.theClap.ui.login.data.LoginResult
import com.chingchan.theClap.ui.login.data.LoginUserResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.activity.MyPageProfileOtherActivity
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class ComplimentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActiCompDetailBinding

    private lateinit var compCmtRecyclerAdapter: CompCmtRecyclerAdapter
    private lateinit var compDetailImageRecyclerAdapter: CompDetailImageRecyclerAdapter   // 게시글 이미지 리사이클러뷰 어뎁터

    private val editDialog: CompEditDialog by lazy { CompEditDialog(this) } // 게시글 수정(본인) 다이얼로그
    private val editOtherDialog: CompEditOtherDialog by lazy { CompEditOtherDialog(this) } // 게시글 수정(다른 사람) 다이얼로그
    private val deleteDialog: CompDeleteDialog by lazy { CompDeleteDialog(this) } // 삭제하기 다이얼로그
    private val hideDialog: CompHideDialog by lazy { CompHideDialog(this) } // 숨김 처리하기 다이얼로그
    private val blockDialog: CompBlockDialog by lazy { CompBlockDialog(this) } // 차단하기 다이얼로그
    private val reportDialog: CompReportDialog by lazy { CompReportDialog(this) } // 신고하기 다이얼로그
    private val cmtSortDialog: CompCmtSortDialog by lazy { CompCmtSortDialog(this) } // 댓글 정렬 다이얼로그
    private val editCmtDialog: CompCmtEditDialog by lazy { CompCmtEditDialog(this) } // 댓글 수정(본인) 다이얼로그
    private val editCmtOtherDialog: CompCmtEditOtherDialog by lazy { CompCmtEditOtherDialog(this) } // 댓글 수정(다른 사람) 다이얼로그
    private val reportCmtDialog: CompCmtReportDialog by lazy { CompCmtReportDialog(this) } // 댓글 신고하기 다이얼로그

    private lateinit var compData: CompData
    private lateinit var compCatDataList: ArrayList<CompCatData>

    private var curCmtPosition = -1 // 현재 선택된 댓글의 position
    private var curCmtPage = 0
    private var isCmtPageLast = false
    private var curCmtSort = "" // 정렬 방법 -> 최신순 : "", 오래된순 : createTs



    // 게시글 수정 Intent registerForActivityResult
    private val editResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 2000) {    // 수정 완료
            compData = GlobalFunction.getParcelableExtra(result.data!!, "compEditData", CompData::class.java)!!    // 수정된 게시글 데이터

            with(binding){
                catType.text = compData.typeName
                compTitle.text = compData.title
                compContent.text = compData.content
            }
        }
    }

    // 댓글 상세화면 Intent registerForActivityResult
    private val cmtDetailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼 클릭
            clearAndSetCmtData()    // 댓글 데이터 초기화
        }
    }

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

        binding = ActiCompDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compData = GlobalFunction.getParcelableExtra(intent, "compData", CompData::class.java)!!    // 게시글 데이터
        compCatDataList = GlobalFunction.getSerializableExtra(intent, "compCatData", ArrayList<CompCatData>()::class.java)!!    // 카테고리 데이터

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){

            userName.text = compData.nickname

            var writeTime = LocalDateTime.parse(compData.modifyTs.replace("Z", ""))
            val currentTime = LocalDateTime.now()

            if(writeTime.until(currentTime, ChronoUnit.DAYS) >= 365){
                userWriteTime.text = writeTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            }else if(writeTime.until(currentTime, ChronoUnit.HOURS) >= 24){
                userWriteTime.text = writeTime.format(DateTimeFormatter.ofPattern("MM월 dd일"))
            }else if(writeTime.until(currentTime, ChronoUnit.MINUTES) >= 60){
                userWriteTime.text = writeTime.until(currentTime, ChronoUnit.HOURS).toString() + "시간"
            }else if(writeTime.until(currentTime, ChronoUnit.MINUTES) >= 1){
                userWriteTime.text = writeTime.until(currentTime, ChronoUnit.MINUTES).toString() + "분"
            }else{
                userWriteTime.text = "방금"
            }

            catType.text = compData.typeName
            compTitle.text = compData.title
            compContent.text = compData.content
            btnCompHeart.text = compData.likes.toString()
            btnCompCmt.text = compData.comments.toString()
            btnCompView.text = compData.views.toString()

            btnCompHeart.isSelected = compData.like // 좋아요 여부
            btnBookmark.isSelected = compData.bookmark  // 북마크 여부

            // 닉네임 옆 'BEST"
            if(compData.best){
                compBest.visibility = View.VISIBLE
            }else{
                compBest.visibility = View.GONE
            }

            // 게시글 이미지 리사이클러뷰
            compDetailImageRecyclerAdapter = CompDetailImageRecyclerAdapter()
            rvImage.adapter = compDetailImageRecyclerAdapter
            rvImage.itemAnimator = null  // 깜빡거리는 애니메이션 제거

            val compImageDataList: ArrayList<CompImageData> = ArrayList()

            for(uri in compData.image){
                compImageDataList.add(CompImageData(uri = uri))
            }

            compDetailImageRecyclerAdapter.differ.submitList(compImageDataList.toMutableList())

            compCmtRecyclerAdapter = CompCmtRecyclerAdapter()
            rvComment.adapter = compCmtRecyclerAdapter
            rvComment.itemAnimator = null

            getCompCmt(compData.id, curCmtPage, curCmtSort)    // 댓글 데이터 GET

            // 댓글 클릭 리스너
            compCmtRecyclerAdapter.clickListener = object : CompCmtRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "USER_IMAGE"){
                        curCmtPosition = position  // 현재 누른 위치의 position 갱신

                        val newList = compCmtRecyclerAdapter.differ.currentList.toMutableList()    // 댓글 데이터

                        getProfileData(newList[curCmtPosition].userId) // 프로필 데이터 조회
                    }else if(type == "MORE"){
                        if(UserData.getLoginType(applicationContext) == "GUEST"){
                            customToast.showCustomToast("로그인을 진행해주세요.", this@ComplimentDetailActivity)
                        }else{
                            // differ의 현재 리스트를 받아와서 newList에 넣기
                            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                            val newList = compCmtRecyclerAdapter.differ.currentList.toMutableList()

                            curCmtPosition = position   // 현재 선택한 위치

                            if(newList[position].userId == UserData.getUserId(applicationContext)){
                                editCmtDialog.show()
                            }else{
                                editCmtOtherDialog.show()
                            }
                        }
                    }else if(type == "HEART"){
                        if(UserData.getLoginType(applicationContext) == "GUEST"){
                            customToast.showCustomToast("로그인을 진행해주세요.", this@ComplimentDetailActivity)
                        }else{
                            curCmtPosition = position   // 현재 선택한 위치

                            // differ의 현재 리스트를 받아와서 newList에 넣기
                            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                            val newList = compCmtRecyclerAdapter.differ.currentList.toMutableList()

                            likeCompCmt(newList[position].commentId)
                        }
                    }
                }
            }

            // 게시글 수정(본인) 다이얼로그 클릭 리스너
            editDialog.setListener(object : CompEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "EDIT"){
                        editDialog.dismiss()
                        goEditActivity()
                    }else if(type == "HIDE"){
                        editDialog.dismiss()
                        hideDialog.show()
                    }else if(type == "DELETE"){
                        editDialog.dismiss()
                        deleteDialog.show()
                    }else if(type == "CANCEL"){
                        editDialog.dismiss()
                    }
                }
            })

            // 게시글 수정(다른 사람) 다이얼로그 클릭 리스너
            editOtherDialog.setListener(object : CompEditOtherDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "BLOCK"){
                        editOtherDialog.dismiss()
                        blockDialog.show()
                    }else if(type == "REPORT"){
                        editOtherDialog.dismiss()
                        reportDialog.show()
                    }else if(type == "CANCEL"){
                        editOtherDialog.dismiss()
                    }
                }
            })

            // 삭제하기 다이얼로그 클릭 리스너
            deleteDialog.setListener(object : CompDeleteDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "DELETE"){
                        deleteDialog.dismiss()

                        deleteCompliment()
                    }else if(type == "CANCEL"){
                        deleteDialog.dismiss()
                    }
                }
            })

            // 숨김 처리하기 다이얼로그 클릭 리스너
            hideDialog.setListener(object : CompHideDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "HIDE"){
                        hideDialog.dismiss()

                        hideCompliment()
                    }else if(type == "CANCEL"){
                        hideDialog.dismiss()
                    }
                }
            })

            // 차단하기 다이얼로그 클릭 리스너
            blockDialog.setListener(object : CompBlockDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "BLOCK"){
                        blockDialog.dismiss()

                        blockCompliment()
                    }else if(type == "CANCEL"){
                        blockDialog.dismiss()
                    }
                }
            })

            // 신고하기 다이얼로그 클릭 리스너
            reportDialog.setListener(object : CompReportDialog.OnClickListener{
                override fun onClick(type: String, reportType: String, reportReason: String) {
                    if(type == "REPORT") {
                        reportDialog.dismiss()

                        reportCompliment(CompReportReqData(compData.userId, reportType, reportReason))
                    }else if(type == "CANCEL"){
                        reportDialog.dismiss()
                    }
                }
            })

            // 댓글 정렬 다이얼로그 클릭 리스너
            cmtSortDialog.setListener(object : CompCmtSortDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "NEWEST"){
                        cmtSortDialog.dismiss()

                        curCmtSort = ""
                        btnCmtSort.text = "최신순"
                        clearAndSetCmtData()
                    }else if(type == "OLD"){
                        cmtSortDialog.dismiss()

                        curCmtSort = "createTs"
                        btnCmtSort.text = "오래된순"
                        clearAndSetCmtData()
                    }else if(type == "CANCEL"){
                        cmtSortDialog.dismiss()
                    }
                }
            })

            // 댓글 수정(본인) 다이얼로그 클릭 리스너
            editCmtDialog.setListener(object : CompCmtEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "EDIT"){
                        editCmtDialog.dismiss()

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtRecyclerAdapter.differ.currentList.toMutableList()

                        // 게시글 상세 Activity로 화면 전환
                        val intent = Intent(this@ComplimentDetailActivity, ComplimentCommentEditActivity::class.java)
                        intent.putExtra("compCmtData", newList[curCmtPosition])
                        cmtEditResult.launch(intent)

                    }else if(type == "DELETE"){
                        editCmtDialog.dismiss()

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtRecyclerAdapter.differ.currentList.toMutableList()

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
            reportCmtDialog.setListener(object : CompCmtReportDialog.OnClickListener{
                override fun onClick(type: String, reportType: String, reportReason: String) {
                    if(type == "REPORT") {
                        reportCmtDialog.dismiss()

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtRecyclerAdapter.differ.currentList.toMutableList()

                        // 댓글 신고
                        reportComplimentComment(CompReportReqData(newList[curCmtPosition].userId, reportType, reportReason), newList[curCmtPosition].commentId)

                    }else if(type == "CANCEL"){
                        reportCmtDialog.dismiss()
                    }
                }
            })

            // 북마크 버튼 클릭 리스너
            btnBookmark.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", this@ComplimentDetailActivity)
                }else{
                    bookmarkComp()
                }
            })

            // 더보기 버튼 클릭 리스너
            btnMore.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", this@ComplimentDetailActivity)
                }else{
                    if(UserData.getUserId(applicationContext) == compData.userId){
                        editDialog.show()
                    }else{
                        editOtherDialog.show()
                    }
                }
            })

            // 댓글 정렬 버튼 클릭 리스너
            btnCmtSort.setOnClickListener(View.OnClickListener {
                cmtSortDialog.show()
            })

            // 댓글 작성 버튼 클릭 리스너
            btnConfirm.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", this@ComplimentDetailActivity)
                }else{
                    if(editCmt.text.isNotEmpty()){
                        writeComment(CompCmtWriteReqData(
                            content = editCmt.text.toString()
                        ))

                        editCmt.setText("")

                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                    }else{
                        customToast.showCustomToast("댓글을 입력해 주세요.", this@ComplimentDetailActivity)
                    }
                }
            })

            // 유저 프로필 이미지 클릭 리스너
            userImage.setOnClickListener(View.OnClickListener {
                getProfileData(compData.userId) // 프로필 데이터 조회
            })

            // 좋아요 버튼 클릭 리스너
            btnCompHeart.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", this@ComplimentDetailActivity)
                }else{
                    likeComp()
                }
            })

            // 댓글 버튼 클릭 리스너
            btnCompCmt.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", this@ComplimentDetailActivity)
                }else{
                    // 게시글 상세 Activity로 화면 전환
                    val intent = Intent(this@ComplimentDetailActivity, ComplimentCommentDetailActivity::class.java)
                    intent.putExtra("compData", compData)
                    cmtDetailResult.launch(intent)
                }
            })

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                intent.putExtra("boardId", compData.id)
                setResult(1000, intent)
                finish()
            })

            // '이전 댓글 더보기' 버튼 클릭 리스너
            btnCmtMore.setOnClickListener(View.OnClickListener {
                getCompCmt(compData.id, curCmtPage, curCmtSort)    // 댓글 데이터 GET
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
                        val newList = compCmtRecyclerAdapter.differ.currentList.toMutableList()


                        if(newList.isNotEmpty()){   // 댓글이 있을 경우
                            compCmtRecyclerAdapter.differ.submitList(newList.toMutableList() + compCmtDataList)
                        }else{  // 댓글이 없을 경우
                            compCmtRecyclerAdapter.differ.submitList(compCmtDataList.toMutableList())
                        }

                        isCmtPageLast = response.body()?.data!!.isLast  // 마지막 페이지인지 여부

                        if(!isCmtPageLast){
                            binding.btnCmtMore.visibility = View.VISIBLE    // 댓글 더보기 버튼 보이기
                            curCmtPage += 1 // 다음 페이지 +1
                        }else{
                            binding.btnCmtMore.visibility = View.GONE   // 댓글 더보기 버튼 숨기기
                        }

                    }else{
                        customToast.showCustomToast("댓글을 불러오지 못했습니다.", this@ComplimentDetailActivity)
                    }

                }else{
                    customToast.showCustomToast("댓글을 불러오지 못했습니다.", this@ComplimentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompCmtResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
            }
        })
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            intent.putExtra("boardId", compData.id)
            setResult(1000, intent)
            finish()
        }
    }

    // 댓글 작성
    private fun writeComment(compCmtWriteReqData: CompCmtWriteReqData){

        val apiObjectCall = ApiObject.getCompService.writeCompCmt(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id , req = compCmtWriteReqData)

        apiObjectCall.enqueue(object: Callback<CompCmtWriteResData> {
            override fun onResponse(call: Call<CompCmtWriteResData>, response: Response<CompCmtWriteResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!

                    if(status == "success"){

                        curCmtSort = ""
                        binding.btnCmtSort.text = "최신순"

                        clearAndSetCmtData()
                    }else{
                        customToast.showCustomToast("댓글을 작성하지 못했습니다.", this@ComplimentDetailActivity)
                    }

                }else{
                    customToast.showCustomToast("댓글을 작성하지 못했습니다.", this@ComplimentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompCmtWriteResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
            }
        })
    }

    // 게시글 숨김
    private fun hideCompliment(){
        val apiObjectCall = ApiObject.getCompService.hideComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        customToast.showCustomToast("게시물이 숨김처리 되었습니다.", this@ComplimentDetailActivity)

                        setResult(1001, intent)
                        finish()
                    }

                }else{
                    customToast.showCustomToast("게시물이 숨김처리 되지 못했습니다.", this@ComplimentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
            }
        })
    }

    // 게시글 삭제
    private fun deleteCompliment(){
        val apiObjectCall = ApiObject.getCompService.deleteComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        customToast.showCustomToast("게시물이 삭제 되었습니다.", this@ComplimentDetailActivity)

                        setResult(1001, intent)
                        finish()
                    }

                }else{
                    customToast.showCustomToast("게시물이 삭제 되지 못했습니다.", this@ComplimentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
            }
        })
    }

    // 게시글 차단 / 차단 해제
    private fun blockCompliment(){
        val apiObjectCall = ApiObject.getCompService.blockComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id)

        apiObjectCall.enqueue(object: Callback<CompBlockResData> {
            override fun onResponse(call: Call<CompBlockResData>, response: Response<CompBlockResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        customToast.showCustomToast("게시물이 차단 되었습니다.", this@ComplimentDetailActivity)

                        setResult(1001, intent)
                        finish()
                    }

                }else{
                    customToast.showCustomToast("게시물이 차단 되지 못했습니다.", this@ComplimentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompBlockResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
            }
        })
    }

    // 게시글 신고
    private fun reportCompliment(reportResData: CompReportReqData){
        val apiObjectCall = ApiObject.getCompService.reportComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id, reportResData)

        apiObjectCall.enqueue(object: Callback<CompReportResData> {
            override fun onResponse(call: Call<CompReportResData>, response: Response<CompReportResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        customToast.showCustomToast("게시물이 신고 되었습니다.", this@ComplimentDetailActivity)

                        intent.putExtra("boardId", compData.id)
                        setResult(1002, intent)
                        finish()
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<CompReportResData>(
                        CompReportResData::class.java,
                        CompReportResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00028.name){    // 이미 신고한 게시글
                            customToast.showCustomToast(ErrorCode.S00028.message, this@ComplimentDetailActivity)
                        }else{
                            customToast.showCustomToast("게시물이 신고 되지 못했습니다.", this@ComplimentDetailActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CompReportResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
            }
        })
    }


    // 게시글 좋아요 등록 / 등록 해제
    private fun likeComp(){
        val apiObjectCall = ApiObject.getCompService.likeComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        compData.like = !compData.like

                        if(compData.like){
                            compData.likes += 1
                        }else{
                            compData.likes -= 1
                        }

                        binding.btnCompHeart.isSelected = compData.like
                        binding.btnCompHeart.text = compData.likes.toString()

                    }else{
                        customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", this@ComplimentDetailActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", this@ComplimentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
            }
        })
    }

    // 게시글 북마크 등록 / 등록 해제
    private fun bookmarkComp(){
        val apiObjectCall = ApiObject.getCompService.bookmarkComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        compData.bookmark = !compData.bookmark

                        binding.btnBookmark.isSelected = compData.bookmark

                    }else{
                        customToast.showCustomToast("게시물 북마크 등록/해제 하지 못했습니다.", this@ComplimentDetailActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 북마크 등록/해제 하지 못했습니다.", this@ComplimentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
            }
        })
    }

    // 댓글 삭제
    private fun deleteCompCmt(cmtId: Int){
        val apiObjectCall = ApiObject.getCompService.deleteCompCmt(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compData.id, commentId = cmtId)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        customToast.showCustomToast("댓글이 삭제 되었습니다.", this@ComplimentDetailActivity)

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compCmtRecyclerAdapter.differ.currentList.toMutableList()
                        newList.removeAt(curCmtPosition)
                        compCmtRecyclerAdapter.differ.submitList(newList.toMutableList())

                    }else{
                        customToast.showCustomToast("댓글을 삭제 하지 못했습니다.", this@ComplimentDetailActivity)
                    }

                }else{
                    customToast.showCustomToast("댓글을 삭제 하지 못했습니다.", this@ComplimentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
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
                        val newList = compCmtRecyclerAdapter.differ.currentList.toMutableList()

                        var isLikeCompCmt = !newList[curCmtPosition].like
                        var heartCnt = newList[curCmtPosition].likes

                        if(isLikeCompCmt){
                            heartCnt += 1
                        }else{
                            heartCnt -= 1
                        }

                        var newData = newList[curCmtPosition].copy(like = isLikeCompCmt, likes = heartCnt)

                        newList[curCmtPosition] = newData

                        compCmtRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("댓글 좋아요 등록/해제 하지 못했습니다.", this@ComplimentDetailActivity)
                    }

                }else{
                    customToast.showCustomToast("댓글 좋아요 등록/해제 하지 못했습니다.", this@ComplimentDetailActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
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

                        customToast.showCustomToast("댓글이 신고 되었습니다.", this@ComplimentDetailActivity)

                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<CompReportResData>(
                        CompReportResData::class.java,
                        CompReportResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00035.name){    // 이미 신고한 댓글
                            customToast.showCustomToast(ErrorCode.S00035.message, this@ComplimentDetailActivity)
                        }else{
                            customToast.showCustomToast("게시물이 신고 되지 못했습니다.", this@ComplimentDetailActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CompReportResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
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

                        val intent = Intent(this@ComplimentDetailActivity, MyPageProfileOtherActivity::class.java)
                        intent.putExtra("profileData", profileData)

                        startActivity(intent)
                    }else{
                        customToast.showCustomToast("프로필 정보 조회를 못했습니다.", this@ComplimentDetailActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<ProfileResData>(
                        ProfileResData::class.java,
                        ProfileResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00011.name){    // 프로필 데이터 조회 x ( 차단 or 삭제...?)
                            customToast.showCustomToast(ErrorCode.S00011.message, this@ComplimentDetailActivity)
                        }else{
                            customToast.showCustomToast("프로필 정보 조회를 못했습니다.", this@ComplimentDetailActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentDetailActivity)
            }
        })
    }

    // 게시글 수정 액티비티로 이동
    private fun goEditActivity(){
        // 테스트를 위한 인텐트
        val intent = Intent(this, ComplimentWriteActivity::class.java)
        // 카테고리 값 Set
        for(i in 1 until compCatDataList.size){
            compCatDataList[i].isSelect = compCatDataList[i].boardTypeId == compData.typeId
        }

        intent.putParcelableArrayListExtra("compCatData", compCatDataList)
        intent.putExtra("compData", compData)
        intent.putExtra("writeType", "edit")

        editResult.launch(intent)
    }

    private fun clearAndSetCmtData(){
        // 댓글 초기화
        curCmtPage = 0

        compCmtRecyclerAdapter.differ.submitList(null)

        getCompCmt(compData.id, curCmtPage, curCmtSort)    // 댓글 데이터 GET
    }
}