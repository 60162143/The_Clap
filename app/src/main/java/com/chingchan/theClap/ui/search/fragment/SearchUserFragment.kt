package com.chingchan.theClap.ui.search.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chingchan.theClap.databinding.FragSearchUserBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.ui.compliment.data.CompResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.activity.MyPageCompActivity
import com.chingchan.theClap.ui.myPage.activity.MyPageProfileOtherActivity
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.search.activity.SearchActivity
import com.chingchan.theClap.ui.search.adapter.SearchUserRecyclerAdapter
import com.chingchan.theClap.ui.search.data.SearchUserData
import com.chingchan.theClap.ui.search.data.SearchUserResData
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class SearchUserFragment : Fragment() {

    private var _binding: FragSearchUserBinding? = null
    private lateinit var searchActivity: SearchActivity

    private var searchUserRecyclerAdapter: SearchUserRecyclerAdapter = SearchUserRecyclerAdapter() // 유저 리사이클러뷰 어뎁터

    private var userDataList: ArrayList<SearchUserData> = ArrayList()

    private var curUserPosition = -1 // 현재 유저 position
    private var curUserPage = 0 // 현재 조회 페이지
    private var isUserPageLast = true  // 현제 조회 페이지가 마지막인지 여부
    private var curKeyword = "" // 현재 검색어

    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        searchActivity = context as SearchActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragSearchUserBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            // 유저 리사이클러뷰
            rvUser.adapter = searchUserRecyclerAdapter
            rvUser.itemAnimator = null

            // 리사이클러뷰의 마지막 Item이 완전히 보일 떄 호출
            rvUser.addOnScrollListener(object: RecyclerView.OnScrollListener(){
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
                        if(curUserPage > 0 && rvPosition == totalCount && !isUserPageLast){
                            getUserData(curKeyword, curUserPage)   // 유저 목록 추가 조회
                        }
                    }
                }
            })

            // 유저 목록 클릭 리스너
            searchUserRecyclerAdapter.clickListener = object : SearchUserRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "TOTAL"){  // 전체 영역 클릭 시
                        curUserPosition = position  // 현재 누른 위치의 position 갱신

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = searchUserRecyclerAdapter.differ.currentList.toMutableList()

                        getProfileData(newList[curUserPosition].id) // 프로필 데이터 조회
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 유저 목록 검색
    fun getUserData(keyword: String, curPage: Int){
        val apiObjectCall = ApiObject.getSearchService.getSearchUser(authorization = "Bearer " + UserData.getAccessToken(searchActivity.applicationContext)
            , keyword = keyword, page = curPage, size = 10)

        apiObjectCall.enqueue(object: Callback<SearchUserResData> {
            override fun onResponse(call: Call<SearchUserResData>, response: Response<SearchUserResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val newUserDataList = response.body()?.data?.content!!

                    // UI 변경
                    if(newUserDataList.isEmpty()){
                        binding.rvUser.visibility = View.GONE
                        binding.textEmpty.visibility = View.VISIBLE
                    }else{
                        binding.rvUser.visibility = View.VISIBLE
                        binding.textEmpty.visibility = View.GONE
                    }

                    // differ의 현재 리스트를 받아와서 newList에 넣기
                    // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                    val newList = searchUserRecyclerAdapter.differ.currentList.toMutableList()


                    if(newList.isNotEmpty()){   // 게시글 있을 경우
                        searchUserRecyclerAdapter.differ.submitList(newList.toMutableList() + newUserDataList)
                    }else{  // 게시글이 없을 경우
                        searchUserRecyclerAdapter.differ.submitList(newUserDataList.toMutableList())
                    }

                    userDataList += newUserDataList

                    isUserPageLast = response.body()?.data!!.isLast  // 마지막 페이지인지 여부

                    // 마지막 페이지가 아닐 경우 페이지 +1
                    if(!isUserPageLast){
                        curUserPage += 1 // 다음 페이지 +1
                    }

                    // 데이터 총 개수
                    binding.userCount.text = DecimalFormat("#,###").format(response.body()?.data!!.totalElements).toString() + " 명"

                }else{
                    customToast.showCustomToast("유저 목록을 불러오지 못했습니다.", searchActivity)
                }
            }

            override fun onFailure(call: Call<SearchUserResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", searchActivity)
            }
        })
    }

    // 내 프로필 정보 조회
    private fun getProfileData(userId: Int){
        val apiObjectCall = ApiObject.getMyPageService.getProfile(authorization = "Bearer " + UserData.getAccessToken(searchActivity.applicationContext)
            , id = userId)

        apiObjectCall.enqueue(object: Callback<ProfileResData> {
            override fun onResponse(call: Call<ProfileResData>, response: Response<ProfileResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val profileData = response.body()?.data!!

                        val intent = Intent(searchActivity, MyPageProfileOtherActivity::class.java)
                        intent.putExtra("profileData", profileData)

                        startActivity(intent)
                    }else{
                        customToast.showCustomToast("프로필 정보 조회를 못했습니다.", searchActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<ProfileResData>(
                        ProfileResData::class.java,
                        ProfileResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00011.name){    // 프로필 데이터 조회 x ( 차단 or 삭제...?)
                            customToast.showCustomToast(ErrorCode.S00011.message, searchActivity)
                        }else{
                            customToast.showCustomToast("프로필 정보 조회를 못했습니다.", searchActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", searchActivity)
            }
        })
    }

    // 검색어 지정 및 데이터 검색
    fun setKeyword(keyword: String){
        curKeyword = keyword
        curUserPage = 0
        userDataList.clear()
        searchUserRecyclerAdapter.differ.submitList(userDataList.toMutableList())

        getUserData(keyword, curUserPage)
    }

    // Context Activity Set
    fun setActivity(context: Context){
        searchActivity = context as SearchActivity
    }
}