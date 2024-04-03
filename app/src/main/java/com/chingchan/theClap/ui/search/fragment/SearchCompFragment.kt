package com.chingchan.theClap.ui.search.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chingchan.theClap.databinding.FragSearchCompBinding
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.ui.compliment.activity.ComplimentDetailActivity
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompDetResData
import com.chingchan.theClap.ui.compliment.data.CompOneResData
import com.chingchan.theClap.ui.compliment.data.CompResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.activity.MyPageCompActivity
import com.chingchan.theClap.ui.search.activity.SearchActivity
import com.chingchan.theClap.ui.search.adapter.SearchCompRecyclerAdapter
import com.chingchan.theClap.ui.search.dialog.SearchCompSortDialog
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class SearchCompFragment : Fragment() {

    private var _binding: FragSearchCompBinding? = null
    private lateinit var searchActivity: SearchActivity

    private var searchCompRecyclerAdapter: SearchCompRecyclerAdapter = SearchCompRecyclerAdapter() // 칭찬글 리사이클러뷰 어뎁터

    private val compSortDialog: SearchCompSortDialog by lazy { SearchCompSortDialog(searchActivity) } // 칭찬글 정렬 다이얼로그

    private var compDataList: ArrayList<CompData> = ArrayList()
    private var compCatList: ArrayList<CompCatData> = ArrayList()   // 카테고리 데이터

    private var curCompPosition = -1 // 현재 게시글 position

    private var curCompPage = 0 // 현재 게시글 페이지
    private var isCompPageLast = true  // 현제 게시글이 페이지가 마지막인지 여부
    private var curCompSort = ""    // 현재 게시글 정렬 조건
    private var curKeyword = "" // 현재 게시글 검색어

    override fun onAttach(context: Context) {
        super.onAttach(context)

        searchActivity = context as SearchActivity
    }

    private val binding get() = _binding!!

    // 게시글 상세화면 Intent registerForActivityResult
    private val detailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼으로 왔을 경우
            getOneCompData(result.data?.getIntExtra("boardId", 0)!!)

        } else if (result.resultCode == 1001) {    // 게시글 숨김, 삭제, 차단일 경우
            // differ의 현재 리스트를 받아와서 newList에 넣기
            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
            val newList = searchCompRecyclerAdapter.differ.currentList.toMutableList()

            newList.removeAt(curCompPosition)
            compDataList.removeAt(curCompPosition)

            // adapter의 differ.submitList()로 newList 제출
            // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
            searchCompRecyclerAdapter.differ.submitList(newList.toMutableList())

            binding.compCount.text = compDataList.size.toString() + " 개"

            setUI() // UI 변경
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragSearchCompBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCategoryList()   // 카테고리 데이터 조회

        with(binding){
            // 칭찬글 리사이클러뷰
            rvComp.adapter = searchCompRecyclerAdapter
            rvComp.itemAnimator = null

            setUI() // UI 변경

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
                            getCompData(curKeyword, curCompPage, curCompSort)   // 게시글 목록 추가 조회
                        }
                    }
                }
            })

            // 게시글 클릭 리스너
            searchCompRecyclerAdapter.clickListener = object : SearchCompRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "HEART"){  // 게시글 좋아요 버튼 클릭 시
//                        curCompPosition = position  // 현재 누른 위치의 position 갱신
//                        likeComp(compDataList[position].id)  // 좋아요 등록 / 등록 해제
                    }else if(type == "TOTAL"){  // 전체 영역 클릭 시
                        curCompPosition = position  // 현재 누른 위치의 position 갱신

                        increaseView(compDataList[position])  // 조회 수 증가
                    }
                }
            }

            // 칭찬글 정렬 다이얼로그 클릭 리스너
            compSortDialog.setListener(object : SearchCompSortDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "VIEW"){
                        compSortDialog.dismiss()

                        btnCompSort.text = "조회순"
                        curCompSort = "views,desc"
                        setKeyword(curKeyword)

                    }else if(type == "NEWEST"){
                        compSortDialog.dismiss()

                        btnCompSort.text = "최신순"
                        curCompSort = ""
                        setKeyword(curKeyword)

                    }else if(type == "CANCEL"){
                        compSortDialog.dismiss()
                    }
                }
            })

            // 정렬 버튼 클릭 리스너
            btnCompSort.setOnClickListener(View.OnClickListener {
                compSortDialog.show()
            })
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 게시글 검색
    fun getCompData(keyword: String, curPage: Int, curSort: String){
        val apiObjectCall = ApiObject.getSearchService.getSearchComp(authorization = "Bearer " + UserData.getAccessToken(searchActivity.applicationContext)
            , keyword = keyword, page = curPage, size = 10, sort = curSort)

        apiObjectCall.enqueue(object: Callback<CompResData> {
            override fun onResponse(call: Call<CompResData>, response: Response<CompResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val newCompDataList = response.body()?.data?.content!!

                    if(newCompDataList.isEmpty()){
                        binding.rvComp.visibility = View.GONE
                        binding.btnCompSort.visibility = View.GONE

                        binding.textEmpty.visibility = View.VISIBLE
                    }else{
                        binding.rvComp.visibility = View.VISIBLE
                        binding.btnCompSort.visibility = View.VISIBLE

                        binding.textEmpty.visibility = View.GONE
                    }

                    // differ의 현재 리스트를 받아와서 newList에 넣기
                    // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                    val newList = searchCompRecyclerAdapter.differ.currentList.toMutableList()


                    if(newList.isNotEmpty()){   // 게시글 있을 경우
                        searchCompRecyclerAdapter.differ.submitList(newList.toMutableList() + newCompDataList)
                    }else{  // 게시글이 없을 경우
                        searchCompRecyclerAdapter.differ.submitList(newCompDataList.toMutableList())
                    }

                    compDataList += newCompDataList

                    // 데이터가 있고 첫번째 페이지일 경우 맨 위로 스크롤
                    if(compDataList.isNotEmpty() && curPage == 0){
                        binding.rvComp.smoothScrollToPosition(0)
                    }

                    isCompPageLast = response.body()?.data!!.isLast  // 마지막 페이지인지 여부

                    // 마지막 페이지가 아닐 경우 페이지 +1
                    if(!isCompPageLast){
                        curCompPage += 1 // 다음 페이지 +1
                    }

                    // 데이터 총 개수
                    binding.compCount.text = DecimalFormat("#,###").format(response.body()?.data!!.totalElements).toString() + " 개"

                }else{
                    customToast.showCustomToast("게시글 목록을 불러오지 못했습니다.", searchActivity)
                }
            }

            override fun onFailure(call: Call<CompResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", searchActivity)
            }
        })
    }

    // 게시글 조회수 증가
    fun increaseView(compData: CompData){
        // 본인이 작성한 글일 경우 조회수 증가 X
        if(compData.userId != UserData.getUserId(searchActivity.applicationContext)){
            val apiObjectCall = ApiObject.getCompService.increaseView(authorization = "Bearer " + UserData.getAccessToken(searchActivity.applicationContext)
                , boardId = compData.id)

            apiObjectCall.enqueue(object: Callback<CompDetResData> {
                override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                    if(response.isSuccessful) {
                        val status = response.body()?.status!!
                        if(status == "success"){
                            // 게시글 상세 Activity로 화면 전환
                            val intent = Intent(searchActivity, ComplimentDetailActivity::class.java)
                            intent.putParcelableArrayListExtra("compCatData", compCatList)
                            compData.views = compData.views + 1
                            intent.putExtra("compData", compData)
                            detailResult.launch(intent)
                        }else{
                            customToast.showCustomToast("게시글 조회수 증가가 되지 못했습니다.", searchActivity)
                        }

                    }else{
                        customToast.showCustomToast("게시글 조회수 증가가 되지 못했습니다.", searchActivity)
                    }
                }

                override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                    customToast.showCustomToast("Call Failed", searchActivity)
                }
            })
        }else{
            // 게시글 상세 Activity로 화면 전환
            val intent = Intent(searchActivity, ComplimentDetailActivity::class.java)
            intent.putParcelableArrayListExtra("compCatData", compCatList)
            intent.putExtra("compData", compData)
            detailResult.launch(intent)
        }
    }

    // 게시글 단건 조회
    private fun getOneCompData(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.getOneComp(authorization = "Bearer " + UserData.getAccessToken(searchActivity.applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompOneResData> {
            override fun onResponse(call: Call<CompOneResData>, response: Response<CompOneResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = searchCompRecyclerAdapter.differ.currentList.toMutableList()

                        newList[curCompPosition] = response.body()?.data

                        compDataList[curCompPosition] = response.body()?.data!!


                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        searchCompRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 조회를 못했습니다.", searchActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 조회를 못했습니다.", searchActivity)
                }
            }

            override fun onFailure(call: Call<CompOneResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", searchActivity)
            }
        })
    }

    // 게시글 좋아요 등록 / 등록 해제
    private fun likeComp(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.likeComp(authorization = "Bearer " + UserData.getAccessToken(searchActivity.applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = searchCompRecyclerAdapter.differ.currentList.toMutableList()

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
                        searchCompRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", searchActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", searchActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", searchActivity)
            }
        })
    }

    // 카테고리 데이터 조회
    private fun getCategoryList() {
        val apiObjectCall = ApiObject.getCompService.getCompCat(authorization = "Bearer " + UserData.getAccessToken(searchActivity.applicationContext))

        apiObjectCall.enqueue(object: Callback<CompCatResData> {
            override fun onResponse(call: Call<CompCatResData>, response: Response<CompCatResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    compCatList = response.body()?.data!!

                    compCatList.sortBy { T -> T.boardTypeId }
                }else{
                    customToast.showCustomToast("카테고리를 불러오지 못했습니다.", searchActivity)
                }
            }

            override fun onFailure(call: Call<CompCatResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", searchActivity)
            }
        })
    }

    // UI 변경
    private fun setUI(){
        with(binding){
            // 데이터가 없을 경우 데이터가 없다는 텍스트 보이기
            if(compDataList.isEmpty()){
                rvComp.visibility = View.GONE
                btnCompSort.visibility = View.GONE

                textEmpty.visibility = View.VISIBLE
            }else{
                rvComp.visibility = View.VISIBLE
                btnCompSort.visibility = View.VISIBLE

                textEmpty.visibility = View.GONE
            }
        }
    }

    // 검색어 지정 및 데이터 검색
    fun setKeyword(keyword: String){
        curKeyword = keyword
        curCompPage = 0
        compDataList.clear()
        searchCompRecyclerAdapter.differ.submitList(compDataList.toMutableList())

        getCompData(keyword, curCompPage, curCompSort)
    }

    // Context Activity Set
    fun setActivity(context: Context){
        searchActivity = context as SearchActivity
    }
}