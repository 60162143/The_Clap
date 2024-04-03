package com.chingchan.theClap.ui.myPage.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chingchan.theClap.databinding.FragMypageWriteCompBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.activity.ComplimentDetailActivity
import com.chingchan.theClap.ui.compliment.activity.ComplimentWriteActivity
import com.chingchan.theClap.ui.compliment.data.CompBlockResData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompDetResData
import com.chingchan.theClap.ui.compliment.data.CompOneResData
import com.chingchan.theClap.ui.compliment.data.CompReportReqData
import com.chingchan.theClap.ui.compliment.data.CompReportResData
import com.chingchan.theClap.ui.compliment.data.CompResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.activity.MyPageProfileOtherActivity
import com.chingchan.theClap.ui.myPage.adapter.MyPageOtherWriteCompRecyclerAdapter
import com.chingchan.theClap.ui.myPage.data.DeletePostsResData
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherWriteCompBlockDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherWriteCompDeleteDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherWriteCompEditDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherWriteCompEditOtherDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherWriteCompHideDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherWriteCompReportDialog
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageOtherWriteCompFragment : Fragment() {

    private var _binding: FragMypageWriteCompBinding? = null
    private lateinit var myPageProfileOtherActivity: MyPageProfileOtherActivity

    // 타인이 작성한 칭찬 리사이클러뷰 어뎁터
    private var myPageOtherWriteCompRecyclerAdapter: MyPageOtherWriteCompRecyclerAdapter = MyPageOtherWriteCompRecyclerAdapter()

    private val writeOtherCompEditOtherDialog: MyPageOtherWriteCompEditOtherDialog by lazy { MyPageOtherWriteCompEditOtherDialog(myPageProfileOtherActivity) } // 타인이 작성한 칭찬 수정 다이얼로그
    private val writeOtherCompEditDialog: MyPageOtherWriteCompEditDialog by lazy { MyPageOtherWriteCompEditDialog(myPageProfileOtherActivity) } // 내가 작성한 칭찬 수정 다이얼로그

    private val writeOtherCompBlockDialog: MyPageOtherWriteCompBlockDialog by lazy { MyPageOtherWriteCompBlockDialog(myPageProfileOtherActivity) } // 타인이 작성한 칭찬 게시물 차단 다이얼로그
    private val writeOtherCompReportDialog: MyPageOtherWriteCompReportDialog by lazy { MyPageOtherWriteCompReportDialog(myPageProfileOtherActivity) } // 타인이 작성한 칭찬 게시물 신고 다이얼로그

    private val writeOtherCompDeleteDialog: MyPageOtherWriteCompDeleteDialog by lazy { MyPageOtherWriteCompDeleteDialog(myPageProfileOtherActivity) } // 내가 작성한 칭찬 게시물 삭제 다이얼로그
    private val writeOtherCompHideDialog: MyPageOtherWriteCompHideDialog by lazy { MyPageOtherWriteCompHideDialog(myPageProfileOtherActivity) } // 내가 작성한 칭찬 게시물 숨기기 다이얼로그

    private var compDataList: ArrayList<CompData> = ArrayList()

    private var curCompPosition: Int = -1   // 현재 선택된 게시글의 position
    private var curCompPage = 0 // 현재 게시글 페이지
    private var isCompPageLast = true  // 현제 게시글이 페이지가 마지막인지 여부

    private val binding get() = _binding!!

    // 게시글 수정 Intent registerForActivityResult
    private val editResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 2000) {    // 수정 완료
            val compData = GlobalFunction.getParcelableExtra(result.data!!, "compEditData", CompData::class.java)!!    // 수정된 게시글 데이터

            // differ의 현재 리스트를 받아와서 newList에 넣기
            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
            val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

            newList[curCompPosition] = compData.copy()
            compDataList[curCompPosition] = compData

            myPageOtherWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())
        }
    }

    // 게시글 상세화면 Intent registerForActivityResult
    private val detailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼으로 왔을 경우
            getOneCompData(result.data?.getIntExtra("boardId", 0)!!)

        } else if (result.resultCode == 1001) {    // 게시글 숨김, 삭제, 차단일 경우
            // differ의 현재 리스트를 받아와서 newList에 넣기
            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
            val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

            newList.removeAt(curCompPosition)
            compDataList.removeAt(curCompPosition)

            // adapter의 differ.submitList()로 newList 제출
            // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
            myPageOtherWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())
        } else if (result.resultCode == 1002) {    // 게시글 신고일 경우

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        myPageProfileOtherActivity = context as MyPageProfileOtherActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragMypageWriteCompBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getPosts()   // 작성한 칭찬 내역 조회

        with(binding){

            // 받은 칭찬 게시글 리사이클러뷰
            rvComp.adapter = myPageOtherWriteCompRecyclerAdapter
            rvComp.itemAnimator = null

            // 리사이클러뷰의 마지막 Item이 완전히 보일 떄 호출
            rvComp.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // 리사이클러뷰 아이템 위치 찾기, 아이템 위치가 완전히 보일때 호출됨
                    val rvPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                    // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                    val totalCount =
                        recyclerView.adapter?.itemCount?.minus(1)

                    // 페이징이 가능한 데이터 수일 경우 페이징 처리
                    if (totalCount != null) {
                        if(curCompPage > 0 && rvPosition == totalCount && !isCompPageLast){
                            getPosts()   // 작성한 칭찬 목록 추가 조회
                        }
                    }
                }
            })

            // 게시글 클릭 리스너
            myPageOtherWriteCompRecyclerAdapter.clickListener = object : MyPageOtherWriteCompRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "USER_IMAGE"){   // 유저 프로필 이미지 클릭 시
                        curCompPosition = position  // 현재 누른 위치의 position 갱신

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        getProfileData(newList[curCompPosition].userId) // 프로필 데이터 조회
                    }else if(type == "MORE"){  // 더보기 버튼 클릭 시
                        curCompPosition = position  // 현재 선택된 position

                        // 본인 프로필인지 확인
                        if(myPageProfileOtherActivity.profileUserId == UserData.getUserId(myPageProfileOtherActivity.applicationContext)){
                            writeOtherCompEditDialog.show()
                        }else{
                            writeOtherCompEditOtherDialog.show()
                        }
                    }else if(type == "HEART"){  // 게시글 좋아요 버튼 클릭 시
                        curCompPosition = position  // 현재 누른 위치의 position 갱신

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        likeComp(newList[position].id)  // 좋아요 등록 / 등록 해제
                    }else if(type == "TOTAL"){  // 전체 영역 클릭 시
                        curCompPosition = position  // 현재 누른 위치의 position 갱신

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        increaseView(newList[position])  // 조회 수 증가
                    }
                }
            }

            // 타인이 작성한 칭찬 수정 다이얼로그 클릭 리스너
            writeOtherCompEditOtherDialog.setListener(object : MyPageOtherWriteCompEditOtherDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "BLOCK"){
                        writeOtherCompEditOtherDialog.dismiss()

                        writeOtherCompBlockDialog.show()
                    }else if(type == "REPORT"){
                        writeOtherCompEditOtherDialog.dismiss()

                        writeOtherCompReportDialog.show()
                    }else if(type == "CANCEL"){
                        writeOtherCompEditOtherDialog.dismiss()
                    }
                }
            })

            // 내가 작성한 칭찬 수정 다이얼로그 클릭 리스너
            writeOtherCompEditDialog.setListener(object : MyPageOtherWriteCompEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "DELETE"){
                        writeOtherCompEditDialog.dismiss()

                        writeOtherCompDeleteDialog.show()
                    }else if(type == "HIDE"){
                        writeOtherCompEditDialog.dismiss()

                        writeOtherCompHideDialog.show()
                    }else if(type == "EDIT"){
                        writeOtherCompEditDialog.dismiss()

                        goEditActivity()    // 게시글 수정 화면으로 이동
                    }else if(type == "CANCEL"){
                        writeOtherCompEditDialog.dismiss()
                    }
                }
            })

            // 타인이 작성한 칭찬 게시물 차단 다이얼로그 클릭 리스너
            writeOtherCompBlockDialog.setListener(object : MyPageOtherWriteCompBlockDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "BLOCK") {
                        writeOtherCompBlockDialog.dismiss()

                        blockCompliment(compDataList[curCompPosition].id)
                    }else if(type == "CANCEL"){
                        writeOtherCompBlockDialog.dismiss()
                    }
                }
            })

            // 타인이 작성한 칭찬 게시물 신고 다이얼로그 클릭 리스너
            writeOtherCompReportDialog.setListener(object : MyPageOtherWriteCompReportDialog.OnClickListener{
                override fun onClick(type: String, reportType: String, reportReason: String) {
                    if(type == "REPORT") {
                        writeOtherCompReportDialog.dismiss()

                        reportCompliment(CompReportReqData(compDataList[curCompPosition].userId, reportType, reportReason), compDataList[curCompPosition].id)
                    }else if(type == "CANCEL"){
                        writeOtherCompReportDialog.dismiss()
                    }
                }
            })

            // 내가 작성한 칭찬 게시물 삭제 다이얼로그 클릭 리스너
            writeOtherCompDeleteDialog.setListener(object : MyPageOtherWriteCompDeleteDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "DELETE") {
                        writeOtherCompDeleteDialog.dismiss()

                        deletePosts(compDataList[curCompPosition].id)
                    }else if(type == "CANCEL"){
                        writeOtherCompDeleteDialog.dismiss()
                    }
                }
            })

            // 내가 작성한 칭찬 게시물 숨기기 다이얼로그 클릭 리스너
            writeOtherCompHideDialog.setListener(object : MyPageOtherWriteCompHideDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "HIDE") {
                        writeOtherCompHideDialog.dismiss()

                        hideCompliment(compDataList[curCompPosition].id)
                    }else if(type == "CANCEL"){
                        writeOtherCompHideDialog.dismiss()
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 작성한 칭찬 데이터 GET
    private fun getPosts() {
        val apiObjectCall = ApiObject.getMyPageService.getPosts(authorization = "Bearer " + UserData.getAccessToken(myPageProfileOtherActivity.applicationContext)
            , userId = myPageProfileOtherActivity.profileUserId, page = curCompPage, size = 10)

        apiObjectCall.enqueue(object: Callback<CompResData> {
            override fun onResponse(call: Call<CompResData>, response: Response<CompResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val newCompDataList = response.body()?.data?.content!!

                    setLayout(newCompDataList.isEmpty())    // 데이터가 있는지 없는지 판단 후 레이아웃 SET

                    // differ의 현재 리스트를 받아와서 newList에 넣기
                    // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                    val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                    if(newList.isNotEmpty()){   // 게시글 있을 경우
                        myPageOtherWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList() + newCompDataList.toMutableList())
                    }else{  // 게시글이 없을 경우
                        myPageOtherWriteCompRecyclerAdapter.differ.submitList(newCompDataList.toMutableList())
                    }

                    compDataList += newCompDataList

                    isCompPageLast = response.body()?.data!!.isLast  // 마지막 페이지인지 여부

                    // 마지막 페이지가 아닐 경우 페이지 +1
                    if(!isCompPageLast){
                        curCompPage += 1 // 다음 페이지 +1
                    }

                }else{
                    customToast.showCustomToast("작성한 칭찬 내역을 불러오지 못했습니다.", myPageProfileOtherActivity)
                }
            }

            override fun onFailure(call: Call<CompResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageProfileOtherActivity)
            }
        })
    }

    fun refreshData(){
        // 서버에서 데이터 받기

        // 받은 데이터 리사이클러뷰에 넣기

        // adapter의 differ.submitList()로 newList 제출
        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
        myPageOtherWriteCompRecyclerAdapter.differ.submitList(null)
        compDataList.clear()

        curCompPage = 0 // 작성한 칭찬 내역 페이지 초기화

        getPosts()  // 작성한 칭찬 내역 조회

        // 상위 엑티비티에 새로고침이 완료된 Flag SET
        myPageProfileOtherActivity.isWriteRefreshComplete = true
    }

    fun redraw(){
        myPageOtherWriteCompRecyclerAdapter.differ.submitList(null)
        // adapter의 differ.submitList()로 newList 제출
        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
        myPageOtherWriteCompRecyclerAdapter.differ.submitList(compDataList.toMutableList())

        _binding?.let {
            binding.rvComp.visibility = View.VISIBLE

            if(compDataList.isNotEmpty()){
                binding.rvComp.smoothScrollToPosition(0)
            }
        }
    }

    fun eraseDraw(){
        // adapter의 differ.submitList()로 newList 제출
        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
        myPageOtherWriteCompRecyclerAdapter.differ.submitList(null)

        _binding?.let{
            binding.rvComp.visibility = View.GONE
        }
    }

    // 게시글 차단 / 차단 해제
    private fun blockCompliment(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.blockComp(authorization = "Bearer " + UserData.getAccessToken(myPageProfileOtherActivity.applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompBlockResData> {
            override fun onResponse(call: Call<CompBlockResData>, response: Response<CompBlockResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        customToast.showCustomToast("게시물이 차단 되었습니다.", myPageProfileOtherActivity)

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        newList.removeAt(curCompPosition)
                        compDataList.removeAt(curCompPosition)

                        myPageOtherWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())

                        setLayout(compDataList.isEmpty())    // 데이터가 있는지 없는지 판단 후 레이아웃 SET
                    }

                }else{
                    customToast.showCustomToast("게시물이 차단 되지 못했습니다.", myPageProfileOtherActivity)
                }
            }

            override fun onFailure(call: Call<CompBlockResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageProfileOtherActivity)
            }
        })
    }

    // 게시글 신고
    private fun reportCompliment(reportResData: CompReportReqData, boardId: Int){
        val apiObjectCall = ApiObject.getCompService.reportComp(authorization = "Bearer " + UserData.getAccessToken(myPageProfileOtherActivity.applicationContext)
            , boardId = boardId, reportResData)

        apiObjectCall.enqueue(object: Callback<CompReportResData> {
            override fun onResponse(call: Call<CompReportResData>, response: Response<CompReportResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        customToast.showCustomToast("게시물이 신고 되었습니다.", myPageProfileOtherActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<CompReportResData>(
                        CompReportResData::class.java,
                        CompReportResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00028.name){    // 이미 신고한 게시글
                            customToast.showCustomToast(ErrorCode.S00028.message, myPageProfileOtherActivity)
                        }else{
                            customToast.showCustomToast("게시물이 신고 되지 못했습니다.", myPageProfileOtherActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<CompReportResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageProfileOtherActivity)
            }
        })
    }

    // 게시글 삭제
    private fun deletePosts(boardId: Int){
        val apiObjectCall = ApiObject.getMyPageService.deletePosts(authorization = "Bearer " + UserData.getAccessToken(myPageProfileOtherActivity.applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<DeletePostsResData> {
            override fun onResponse(call: Call<DeletePostsResData>, response: Response<DeletePostsResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        customToast.showCustomToast(response.body()!!.message, myPageProfileOtherActivity)

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        newList.removeAt(curCompPosition)
                        compDataList.removeAt(curCompPosition)

                        myPageOtherWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())

                        setLayout(compDataList.isEmpty())    // 데이터가 있는지 없는지 판단 후 레이아웃 SET
                    }

                }else{
                    customToast.showCustomToast("게시글 내역이 삭제 되지 못했습니다.", myPageProfileOtherActivity)
                }
            }

            override fun onFailure(call: Call<DeletePostsResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageProfileOtherActivity)
            }
        })
    }

    // 게시글 숨김
    private fun hideCompliment(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.hideComp(authorization = "Bearer " + UserData.getAccessToken(myPageProfileOtherActivity.applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        customToast.showCustomToast("게시물이 숨김처리 되었습니다.", myPageProfileOtherActivity)

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        newList.removeAt(curCompPosition)
                        compDataList.removeAt(curCompPosition)

                        myPageOtherWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())

                        setLayout(compDataList.isEmpty())    // 데이터가 있는지 없는지 판단 후 레이아웃 SET
                    }

                }else{
                    customToast.showCustomToast("게시물이 숨김처리 되지 못했습니다.", myPageProfileOtherActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageProfileOtherActivity)
            }
        })
    }

    // 게시글 좋아요 등록 / 등록 해제
    private fun likeComp(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.likeComp(authorization = "Bearer " + UserData.getAccessToken(myPageProfileOtherActivity.applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        var isLikeComp = !newList[curCompPosition].like
                        var heartCnt = newList[curCompPosition].likes

                        if(isLikeComp){
                            heartCnt += 1
                        }else{
                            heartCnt -= 1
                        }

                        var newData = newList[curCompPosition].copy(like = isLikeComp, likes = heartCnt)

                        newList[curCompPosition] = newData
                        compDataList[curCompPosition] = newData

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        myPageOtherWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", myPageProfileOtherActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", myPageProfileOtherActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageProfileOtherActivity)
            }
        })
    }

    // 게시글 조회수 증가
    fun increaseView(compData: CompData){
        // 본인이 작성한 글일 경우 조회수 증가 X
        if(compData.userId != UserData.getUserId(myPageProfileOtherActivity.applicationContext)){
            val apiObjectCall = ApiObject.getCompService.increaseView(authorization = "Bearer " + UserData.getAccessToken(myPageProfileOtherActivity.applicationContext)
                , boardId = compData.id)

            apiObjectCall.enqueue(object: Callback<CompDetResData> {
                override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                    if(response.isSuccessful) {
                        val status = response.body()?.status!!
                        if(status == "success"){
                            // 게시글 상세 Activity로 화면 전환
                            val intent = Intent(myPageProfileOtherActivity, ComplimentDetailActivity::class.java)

                            intent.putParcelableArrayListExtra("compCatData", myPageProfileOtherActivity.compCatList)
                            compData.views = compData.views + 1
                            intent.putExtra("compData", compData)
                            detailResult.launch(intent)
                        }else{
                            customToast.showCustomToast("게시글 조회수 증가가 되지 못했습니다.", myPageProfileOtherActivity)
                        }

                    }else{
                        customToast.showCustomToast("게시글 조회수 증가가 되지 못했습니다.", myPageProfileOtherActivity)
                    }
                }

                override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                    customToast.showCustomToast("Call Failed", myPageProfileOtherActivity)
                }
            })
        }else{
            // 게시글 상세 Activity로 화면 전환
            val intent = Intent(myPageProfileOtherActivity, ComplimentDetailActivity::class.java)
            intent.putParcelableArrayListExtra("compCatData", myPageProfileOtherActivity.compCatList)
            intent.putExtra("compData", compData)
            detailResult.launch(intent)
        }
    }

    // 게시글 단건 조회
    private fun getOneCompData(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.getOneComp(authorization = "Bearer " + UserData.getAccessToken(myPageProfileOtherActivity.applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompOneResData> {
            override fun onResponse(call: Call<CompOneResData>, response: Response<CompOneResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val compData = response.body()?.data

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageOtherWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        newList[curCompPosition] = compData?.copy()
                        compDataList[curCompPosition] = compData!!

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        myPageOtherWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 조회를 못했습니다.", myPageProfileOtherActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 조회를 못했습니다.", myPageProfileOtherActivity)
                }
            }

            override fun onFailure(call: Call<CompOneResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageProfileOtherActivity)
            }
        })
    }

    // 내 프로필 정보 조회
    private fun getProfileData(userId: Int){
        val apiObjectCall = ApiObject.getMyPageService.getProfile(authorization = "Bearer " + UserData.getAccessToken(myPageProfileOtherActivity.applicationContext)
            , id = userId)

        apiObjectCall.enqueue(object: Callback<ProfileResData> {
            override fun onResponse(call: Call<ProfileResData>, response: Response<ProfileResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val profileData = response.body()?.data!!

                        val intent = Intent(myPageProfileOtherActivity, MyPageProfileOtherActivity::class.java)
                        intent.putExtra("profileData", profileData)

                        startActivity(intent)
                    }else{
                        customToast.showCustomToast("프로필 정보 조회를 못했습니다.", myPageProfileOtherActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<ProfileResData>(
                        ProfileResData::class.java,
                        ProfileResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00011.name){    // 프로필 데이터 조회 x ( 차단 or 삭제...?)
                            customToast.showCustomToast(ErrorCode.S00011.message, myPageProfileOtherActivity)
                        }else{
                            customToast.showCustomToast("프로필 정보 조회를 못했습니다.", myPageProfileOtherActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageProfileOtherActivity)
            }
        })
    }

    // 게시글 수정 액티비티로 이동
    private fun goEditActivity(){
        // 테스트를 위한 인텐트
        val intent = Intent(myPageProfileOtherActivity, ComplimentWriteActivity::class.java)

        // 카테고리 값 Set
        for(i in 1 until myPageProfileOtherActivity.compCatList.size){
            myPageProfileOtherActivity.compCatList[i].isSelect = myPageProfileOtherActivity.compCatList[i].boardTypeId == compDataList[curCompPosition].typeId
        }

        intent.putParcelableArrayListExtra("compCatData", myPageProfileOtherActivity.compCatList)
        intent.putExtra("compData", compDataList[curCompPosition])
        intent.putExtra("writeType", "edit")

        editResult.launch(intent)
    }

    // 데이터가 있는지 없는지 판단 후 레이아웃 SET
    private fun setLayout(isEmptyData: Boolean){
        if(isEmptyData){
            binding.rvComp.visibility = View.GONE
            binding.textEmpty.visibility = View.VISIBLE
        }else{
            binding.rvComp.visibility = View.VISIBLE
            binding.textEmpty.visibility = View.GONE
        }
    }
}