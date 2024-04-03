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
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompDetResData
import com.chingchan.theClap.ui.compliment.data.CompOneResData
import com.chingchan.theClap.ui.compliment.data.CompResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.activity.MyPageCompActivity
import com.chingchan.theClap.ui.myPage.activity.MyPageProfileOtherActivity
import com.chingchan.theClap.ui.myPage.adapter.MyPageRecCompRecyclerAdapter
import com.chingchan.theClap.ui.myPage.adapter.MyPageWriteCompRecyclerAdapter
import com.chingchan.theClap.ui.myPage.data.DeletePostsResData
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.myPage.dialog.MyPageRecCompDeleteDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageWriteCompDeleteDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageWriteCompEditDialog
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageWriteCompFragment : Fragment() {

    private var _binding: FragMypageWriteCompBinding? = null
    private lateinit var myPageCompActivity: MyPageCompActivity

    private var myPageWriteCompRecyclerAdapter: MyPageWriteCompRecyclerAdapter = MyPageWriteCompRecyclerAdapter()   // 작성한 칭찬 리사이클러뷰 어뎁터

    private val writeCompEditDialog: MyPageWriteCompEditDialog by lazy { MyPageWriteCompEditDialog(myPageCompActivity) } // 작성한 칭찬 수정 다이얼로그
    private val writeCompDeleteDialog: MyPageWriteCompDeleteDialog by lazy { MyPageWriteCompDeleteDialog(myPageCompActivity) } // 게시물 삭제하기 다이얼로그

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
            val newList = myPageWriteCompRecyclerAdapter.differ.currentList.toMutableList()

            newList[curCompPosition] = compData.copy()
            compDataList[curCompPosition] = compData

            myPageWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())
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
            val newList = myPageWriteCompRecyclerAdapter.differ.currentList.toMutableList()

            newList.removeAt(curCompPosition)
            compDataList.removeAt(curCompPosition)

            // adapter의 differ.submitList()로 newList 제출
            // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
            myPageWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())
        } else if (result.resultCode == 1002) {    // 게시글 신고일 경우

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        myPageCompActivity = context as MyPageCompActivity
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
            rvComp.adapter = myPageWriteCompRecyclerAdapter
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
            myPageWriteCompRecyclerAdapter.clickListener = object : MyPageWriteCompRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "USER_IMAGE"){   // 유저 프로필 이미지 클릭 시
                        curCompPosition = position  // 현재 누른 위치의 position 갱신

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        getProfileData(newList[curCompPosition].userId) // 프로필 데이터 조회
                    }else if(type == "MORE"){  // 더보기 버튼 클릭 시
                        curCompPosition = position  // 현재 선택된 position

                        writeCompEditDialog.show()
                    }else if(type == "DELETE"){  // 삭제 버튼 클릭 시
                        curCompPosition = position  // 현재 선택된 position

                        writeCompDeleteDialog.show()
                    }else if(type == "HEART"){  // 게시글 좋아요 버튼 클릭 시
                        curCompPosition = position  // 현재 누른 위치의 position 갱신

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        likeComp(newList[position].id)  // 좋아요 등록 / 등록 해제
                    }else if(type == "TOTAL"){  // 전체 영역 클릭 시
                        curCompPosition = position  // 현재 누른 위치의 position 갱신

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        goToDetailActivity(newList[position])  // 게시글 상세화면으로 이동
                    }
                }
            }

            // 작성한 칭찬 수정 다이얼로그 클릭 리스너
            writeCompEditDialog.setListener(object : MyPageWriteCompEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "EDIT"){
                        writeCompEditDialog.dismiss()

                        goEditActivity()    // 게시글 수정 화면으로 이동
                    }else if(type == "DELETE"){
                        writeCompEditDialog.dismiss()

                        deletePosts(compDataList[curCompPosition].id)
                    }else if(type == "CANCEL"){
                        writeCompEditDialog.dismiss()
                    }
                }
            })

            // 게시물 삭제하기 다이얼로그 클릭 리스너
            writeCompDeleteDialog.setListener(object : MyPageWriteCompDeleteDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "DELETE") {
                        writeCompDeleteDialog.dismiss()

                        deletePosts(compDataList[curCompPosition].id)
                    }else if(type == "CANCEL"){
                        writeCompDeleteDialog.dismiss()
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
        val apiObjectCall = ApiObject.getMyPageService.getPosts(authorization = "Bearer " + UserData.getAccessToken(myPageCompActivity.applicationContext)
            , userId = UserData.getUserId(myPageCompActivity.applicationContext)
            , page = curCompPage, size = 10)

        apiObjectCall.enqueue(object: Callback<CompResData> {
            override fun onResponse(call: Call<CompResData>, response: Response<CompResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val newCompDataList = response.body()?.data?.content!!

                    setLayout(newCompDataList.isEmpty())    // 데이터가 있는지 없는지 판단 후 레이아웃 SET

                    // differ의 현재 리스트를 받아와서 newList에 넣기
                    // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                    val newList = myPageWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                    if(newList.isNotEmpty()){   // 게시글 있을 경우
                        myPageWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList() + newCompDataList.toMutableList())
                    }else{  // 게시글이 없을 경우
                        myPageWriteCompRecyclerAdapter.differ.submitList(newCompDataList.toMutableList())
                    }

                    compDataList += newCompDataList

                    isCompPageLast = response.body()?.data!!.isLast  // 마지막 페이지인지 여부

                    // 마지막 페이지가 아닐 경우 페이지 +1
                    if(!isCompPageLast){
                        curCompPage += 1 // 다음 페이지 +1
                    }

                }else{
                    customToast.showCustomToast("작성한 칭찬 내역을 불러오지 못했습니다.", myPageCompActivity)
                }
            }

            override fun onFailure(call: Call<CompResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageCompActivity)
            }
        })
    }

    // 게시글 삭제
    private fun deletePosts(boardId: Int){
        val apiObjectCall = ApiObject.getMyPageService.deletePosts(authorization = "Bearer " + UserData.getAccessToken(myPageCompActivity.applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<DeletePostsResData> {
            override fun onResponse(call: Call<DeletePostsResData>, response: Response<DeletePostsResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){

                        customToast.showCustomToast(response.body()!!.message, myPageCompActivity)

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        newList.removeAt(curCompPosition)
                        compDataList.removeAt(curCompPosition)

                        myPageWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())

                        setLayout(compDataList.isEmpty())    // 데이터가 있는지 없는지 판단 후 레이아웃 SET
                    }

                }else{
                    customToast.showCustomToast("게시글 내역이 삭제 되지 못했습니다.", myPageCompActivity)
                }
            }

            override fun onFailure(call: Call<DeletePostsResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageCompActivity)
            }
        })
    }

    // 게시글 좋아요 등록 / 등록 해제
    private fun likeComp(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.likeComp(authorization = "Bearer " + UserData.getAccessToken(myPageCompActivity.applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageWriteCompRecyclerAdapter.differ.currentList.toMutableList()

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
                        myPageWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", myPageCompActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", myPageCompActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageCompActivity)
            }
        })
    }

    // 게시글 단건 조회
    private fun getOneCompData(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.getOneComp(authorization = "Bearer " + UserData.getAccessToken(myPageCompActivity.applicationContext)
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
                        val newList = myPageWriteCompRecyclerAdapter.differ.currentList.toMutableList()

                        newList[curCompPosition] = compData?.copy()
                        compDataList[curCompPosition] = compData!!

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        myPageWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 조회를 못했습니다.", myPageCompActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 조회를 못했습니다.", myPageCompActivity)
                }
            }

            override fun onFailure(call: Call<CompOneResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageCompActivity)
            }
        })
    }

    // 내 프로필 정보 조회
    private fun getProfileData(userId: Int){
        val apiObjectCall = ApiObject.getMyPageService.getProfile(authorization = "Bearer " + UserData.getAccessToken(myPageCompActivity.applicationContext)
            , id = userId)

        apiObjectCall.enqueue(object: Callback<ProfileResData> {
            override fun onResponse(call: Call<ProfileResData>, response: Response<ProfileResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val profileData = response.body()?.data!!

                        val intent = Intent(myPageCompActivity, MyPageProfileOtherActivity::class.java)
                        intent.putExtra("profileData", profileData)

                        startActivity(intent)
                    }else{
                        customToast.showCustomToast("프로필 정보 조회를 못했습니다.", myPageCompActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<ProfileResData>(
                        ProfileResData::class.java,
                        ProfileResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00011.name){    // 프로필 데이터 조회 x ( 차단 or 삭제...?)
                            customToast.showCustomToast(ErrorCode.S00011.message, myPageCompActivity)
                        }else{
                            customToast.showCustomToast("프로필 정보 조회를 못했습니다.", myPageCompActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", myPageCompActivity)
            }
        })
    }

    // 게시글 상세화면으로 이동
    fun goToDetailActivity(compData: CompData){
        // 게시글 상세 Activity로 화면 전환
        val intent = Intent(myPageCompActivity, ComplimentDetailActivity::class.java)
        intent.putParcelableArrayListExtra("compCatData", myPageCompActivity.compCatList)
        intent.putExtra("compData", compData)
        detailResult.launch(intent)
    }

    // 게시글 수정 액티비티로 이동
    private fun goEditActivity(){
        // 테스트를 위한 인텐트
        val intent = Intent(myPageCompActivity, ComplimentWriteActivity::class.java)

        // 카테고리 값 Set
        for(i in 1 until myPageCompActivity.compCatList.size){
            myPageCompActivity.compCatList[i].isSelect = myPageCompActivity.compCatList[i].boardTypeId == compDataList[curCompPosition].typeId
        }

        intent.putParcelableArrayListExtra("compCatData", myPageCompActivity.compCatList)
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

    //
    fun editCheck(chkFlag: Boolean){
        if(compDataList.isNotEmpty()){
            // differ의 현재 리스트를 받아와서 newList에 넣기
            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
            val newList = myPageWriteCompRecyclerAdapter.differ.currentList.toMutableList()

            for((index, data) in newList.withIndex()){
                // 객체의 주소를 바꾸기 위해 copy 사용
                newList[index] = data.copy(editFlag = chkFlag)
            }

            // adapter의 differ.submitList()로 newList 제출
            // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
            myPageWriteCompRecyclerAdapter.differ.submitList(newList.toMutableList())
        }
    }
}