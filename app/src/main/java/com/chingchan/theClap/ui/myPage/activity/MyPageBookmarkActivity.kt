package com.chingchan.theClap.ui.myPage.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.databinding.ActiMypageBookmarkBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.activity.ComplimentDetailActivity
import com.chingchan.theClap.ui.compliment.data.CompBlockResData
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompDetResData
import com.chingchan.theClap.ui.compliment.data.CompOneResData
import com.chingchan.theClap.ui.compliment.data.CompReportReqData
import com.chingchan.theClap.ui.compliment.data.CompReportResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.adapter.MyPageBookmarkRecyclerAdapter
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.myPage.dialog.MyPageBookmarkBlockDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageBookmarkEditDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageBookmarkReportDialog
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageBookmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActiMypageBookmarkBinding

    private var myPageBookmarkRecyclerAdapter: MyPageBookmarkRecyclerAdapter = MyPageBookmarkRecyclerAdapter()// 북마크 게시물 리사이클러뷰 어뎁터

    private val bookmarkEditDialog: MyPageBookmarkEditDialog by lazy { MyPageBookmarkEditDialog(this) } // 북마크 수정 다이얼로그
    private val bookmarkReportDialog: MyPageBookmarkReportDialog by lazy { MyPageBookmarkReportDialog(this) } // 북마크 신고 다이얼로그
    private val bookmarkBlockDialog: MyPageBookmarkBlockDialog by lazy { MyPageBookmarkBlockDialog(this) } // 북마크 차단 다이얼로그

    private var bookmarkCompList: ArrayList<CompData> = ArrayList()
    private var compCatList: ArrayList<CompCatData> = ArrayList()   // 카테고리 데이터

    private var curCompPosition = -1 // 현재 북마크한 게시글 position

    // 게시글 상세화면 Intent registerForActivityResult
    private val detailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼으로 왔을 경우
            getOneCompData(result.data?.getIntExtra("boardId", 0)!!)

        } else if (result.resultCode == 1001) {    // 게시글 숨김, 삭제, 차단일 경우
            // differ의 현재 리스트를 받아와서 newList에 넣기
            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
            val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

            newList.removeAt(curCompPosition)

            // adapter의 differ.submitList()로 newList 제출
            // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
            myPageBookmarkRecyclerAdapter.differ.submitList(newList.toMutableList())

            setUI() // UI 변경
        } else if (result.resultCode == 1002) {    // 게시글 신고일 경우

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMypageBookmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){
            // 받은 칭찬 게시글 리사이클러뷰
            rvComp.adapter = myPageBookmarkRecyclerAdapter
            rvComp.itemAnimator = null

            bookmarkCompList = GlobalFunction.getSerializableExtra(intent, "bookmarkCompList", ArrayList<CompData>()::class.java)!!
            myPageBookmarkRecyclerAdapter.differ.submitList(bookmarkCompList.toMutableList())

            setUI() // UI 변경

            getCategoryList()   // 카테고리 데이터 조회

            // 게시글 클릭 리스너
            myPageBookmarkRecyclerAdapter.clickListener = object : MyPageBookmarkRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "USER_IMAGE"){   // 유저 프로필 이미지 클릭 시
                        curCompPosition = position  // 현재 누른 위치의 position 갱신

                        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()    // 게시글 데이터

                        getProfileData(newList[curCompPosition].userId) // 프로필 데이터 조회
                    }else if(type == "MORE"){  // 더보기 버튼 클릭 시
                        curCompPosition = position  // 현재 선택한 북마크 게시물의 position

                        bookmarkEditDialog.show()
                    }else if(type == "HEART"){  // 게시글 좋아요 버튼 클릭 시
                        curCompPosition = position  // 현재 누른 위치의 position 갱신

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

                        likeComp(newList[position].id)  // 좋아요 등록 / 등록 해제
                    }else if(type == "TOTAL"){  // 전체 영역 클릭 시
                        curCompPosition = position  // 현재 누른 위치의 position 갱신

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

                        increaseView(newList[position])  // 조회 수 증가
                    }
                }
            }

            // 북마크 수정 다이얼로그 클릭 리스너
            bookmarkEditDialog.setListener(object : MyPageBookmarkEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "DELETE"){
                        bookmarkEditDialog.dismiss()

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

                        bookmarkComp(newList[curCompPosition].id)
                    }else if(type == "REPORT"){
                        bookmarkEditDialog.dismiss()

                        bookmarkReportDialog.show()
                    }else if(type == "BLOCK"){
                        bookmarkEditDialog.dismiss()

                        bookmarkBlockDialog.show()
                    }else if(type == "CANCEL"){
                        bookmarkEditDialog.dismiss()
                    }
                }
            })

            // 북마크 신고 다이얼로그 클릭 리스너
            bookmarkReportDialog.setListener(object : MyPageBookmarkReportDialog.OnClickListener{
                override fun onClick(type: String, reportType: String, reportReason: String) {
                    if(type == "REPORT"){
                        bookmarkReportDialog.dismiss()

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

                        reportCompliment(newList[curCompPosition].id, CompReportReqData(newList[curCompPosition].userId, reportType, reportReason))

                    }else if(type == "CANCEL"){
                        bookmarkReportDialog.dismiss()
                    }
                }
            })

            // 북마크 차단 다이얼로그 클릭 리스너
            bookmarkBlockDialog.setListener(object : MyPageBookmarkBlockDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "BLOCK"){
                        bookmarkBlockDialog.dismiss()

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

                        blockCompliment(newList[curCompPosition].id)

                    }else if(type == "CANCEL"){
                        bookmarkBlockDialog.dismiss()
                    }
                }
            })

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                // differ의 현재 리스트를 받아와서 newList에 넣기
                // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

                val bookmarkCompList = newList as ArrayList<CompData>

                intent.putParcelableArrayListExtra("bookmarkCompList", bookmarkCompList)
                setResult(1000, intent)
                finish()
            })
        }
    }

    override fun onResume() {
        super.onResume()

    }

    // 게시글 북마크 등록 / 등록 해제
    private fun bookmarkComp(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.bookmarkComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        customToast.showCustomToast("북마크가 취소되었습니다.", this@MyPageBookmarkActivity)

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()
                        newList.removeAt(curCompPosition)

                        myPageBookmarkRecyclerAdapter.differ.submitList(newList.toMutableList())

                        setUI() // UI 변경
                    }else{
                        customToast.showCustomToast("게시물 북마크 등록/해제 하지 못했습니다.", this@MyPageBookmarkActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 북마크 등록/해제 하지 못했습니다.", this@MyPageBookmarkActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageBookmarkActivity)
            }
        })
    }

    // 게시글 차단 / 차단 해제
    private fun blockCompliment(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.blockComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompBlockResData> {
            override fun onResponse(call: Call<CompBlockResData>, response: Response<CompBlockResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        customToast.showCustomToast("게시물이 차단 되었습니다.", this@MyPageBookmarkActivity)

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()
                        newList.removeAt(curCompPosition)

                        myPageBookmarkRecyclerAdapter.differ.submitList(newList.toMutableList())

                        setUI() // UI 변경
                    }

                }else{
                    customToast.showCustomToast("게시물이 차단 되지 못했습니다.", this@MyPageBookmarkActivity)
                }
            }

            override fun onFailure(call: Call<CompBlockResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageBookmarkActivity)
            }
        })
    }

    // 게시글 신고
    private fun reportCompliment(boardId: Int, reportResData: CompReportReqData){
        val apiObjectCall = ApiObject.getCompService.reportComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = boardId, reportResData)

        apiObjectCall.enqueue(object: Callback<CompReportResData> {
            override fun onResponse(call: Call<CompReportResData>, response: Response<CompReportResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        customToast.showCustomToast("게시물이 신고 되었습니다.", this@MyPageBookmarkActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<CompReportResData>(
                        CompReportResData::class.java,
                        CompReportResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00028.name){    // 이미 신고한 게시글
                            customToast.showCustomToast(ErrorCode.S00028.message, this@MyPageBookmarkActivity)
                        }else{
                            customToast.showCustomToast("게시물이 신고 되지 못했습니다.", this@MyPageBookmarkActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CompReportResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageBookmarkActivity)
            }
        })
    }

    // 게시글 단건 조회
    private fun getOneCompData(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.getOneComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompOneResData> {
            override fun onResponse(call: Call<CompOneResData>, response: Response<CompOneResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val bookmarkData = response.body()?.data

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

                        // 북마크가 해제되었을 경우 목록에서 제거
                        if(bookmarkData?.bookmark!!){
                            newList[curCompPosition] = response.body()?.data
                        }else{
                            newList.removeAt(curCompPosition)

                            setUI() // UI 변경
                        }

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        myPageBookmarkRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 조회를 못했습니다.", this@MyPageBookmarkActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 조회를 못했습니다.", this@MyPageBookmarkActivity)
                }
            }

            override fun onFailure(call: Call<CompOneResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageBookmarkActivity)
            }
        })
    }

    // 게시글 조회수 증가
    fun increaseView(compData: CompData){
        // 본인이 작성한 글일 경우 조회수 증가 X
        if(compData.userId != UserData.getUserId(applicationContext)){
            val apiObjectCall = ApiObject.getCompService.increaseView(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
                , boardId = compData.id)

            apiObjectCall.enqueue(object: Callback<CompDetResData> {
                override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                    if(response.isSuccessful) {
                        val status = response.body()?.status!!
                        if(status == "success"){
                            // 게시글 상세 Activity로 화면 전환
                            val intent = Intent(this@MyPageBookmarkActivity, ComplimentDetailActivity::class.java)

                            intent.putParcelableArrayListExtra("compCatData", compCatList)
                            compData.views = compData.views + 1
                            intent.putExtra("compData", compData)
                            detailResult.launch(intent)
                        }else{
                            customToast.showCustomToast("게시글 조회수 증가가 되지 못했습니다.", this@MyPageBookmarkActivity)
                        }

                    }else{
                        customToast.showCustomToast("게시글 조회수 증가가 되지 못했습니다.", this@MyPageBookmarkActivity)
                    }
                }

                override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                    customToast.showCustomToast("Call Failed", this@MyPageBookmarkActivity)
                }
            })
        }else{
            // 게시글 상세 Activity로 화면 전환
            val intent = Intent(this@MyPageBookmarkActivity, ComplimentDetailActivity::class.java)
            intent.putParcelableArrayListExtra("compCatData", compCatList)
            intent.putExtra("compData", compData)
            detailResult.launch(intent)
        }

    }

    // 게시글 좋아요 등록 / 등록 해제
    private fun likeComp(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.likeComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

                        var isLikeComp = !newList[curCompPosition].like
                        var heartCnt = newList[curCompPosition].likes

                        if(isLikeComp){
                            heartCnt += 1
                        }else{
                            heartCnt -= 1
                        }

                        var newData = newList[curCompPosition].copy(like = isLikeComp, likes = heartCnt)

                        newList[curCompPosition] = newData

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        myPageBookmarkRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", this@MyPageBookmarkActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", this@MyPageBookmarkActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageBookmarkActivity)
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

                        val intent = Intent(this@MyPageBookmarkActivity, MyPageProfileOtherActivity::class.java)
                        intent.putExtra("profileData", profileData)

                        startActivity(intent)
                    }else{
                        customToast.showCustomToast("프로필 정보 조회를 못했습니다.", this@MyPageBookmarkActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<ProfileResData>(
                        ProfileResData::class.java,
                        ProfileResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00011.name){    // 프로필 데이터 조회 x ( 차단 or 삭제...?)
                            customToast.showCustomToast(ErrorCode.S00011.message, this@MyPageBookmarkActivity)
                        }else{
                            customToast.showCustomToast("프로필 정보 조회를 못했습니다.", this@MyPageBookmarkActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageBookmarkActivity)
            }
        })
    }

    // 카테고리 데이터 조회
    private fun getCategoryList() {
        val apiObjectCall = ApiObject.getCompService.getCompCat(authorization = "Bearer " + UserData.getAccessToken(applicationContext))

        apiObjectCall.enqueue(object: Callback<CompCatResData> {
            override fun onResponse(call: Call<CompCatResData>, response: Response<CompCatResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    compCatList = response.body()?.data!!

                    compCatList.sortBy { T -> T.boardTypeId }
                }else{
                    customToast.showCustomToast("카테고리를 불러오지 못했습니다.", this@MyPageBookmarkActivity)
                }
            }

            override fun onFailure(call: Call<CompCatResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageBookmarkActivity)
            }
        })
    }

    // UI 변경
    private fun setUI(){
        // differ의 현재 리스트를 받아와서 newList에 넣기
        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
        val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

        with(binding){
            if(newList.isEmpty()){
                rvComp.visibility = View.GONE
                textEmpty.visibility = View.VISIBLE
            }else{
                rvComp.visibility = View.VISIBLE
                textEmpty.visibility = View.GONE
            }
        }
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // differ의 현재 리스트를 받아와서 newList에 넣기
            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
            val newList = myPageBookmarkRecyclerAdapter.differ.currentList.toMutableList()

            val bookmarkCompList = newList as ArrayList<CompData>

            intent.putParcelableArrayListExtra("bookmarkCompList", bookmarkCompList)
            setResult(1000, intent)
            finish()
        }
    }
}