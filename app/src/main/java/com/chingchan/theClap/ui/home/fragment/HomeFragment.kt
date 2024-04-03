package com.chingchan.theClap.ui.home.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.chingchan.theClap.MainActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.FragHomeBinding
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlideApp
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.activity.ComplimentDetailActivity
import com.chingchan.theClap.ui.compliment.activity.ComplimentWriteActivity
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.home.activity.HomeRankingCompActivity
import com.chingchan.theClap.ui.home.adapter.HomeEventViewPagerAdapter
import com.chingchan.theClap.ui.home.data.CompRankingResData
import com.chingchan.theClap.ui.login.activity.LoginActivity
import com.chingchan.theClap.ui.login.data.LoginResult
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.search.activity.SearchActivity
import com.chingchan.theClap.ui.toast.customToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragHomeBinding? = null
    private var showShimmer: Job? = null
    private lateinit var mainActivity: MainActivity

    private var compCatList: ArrayList<CompCatData> = ArrayList()   // 카테고리 데이터
    private var compRankingList: ArrayList<CompData> = ArrayList()  // 게시글 랭킹 데이터
    private lateinit var accessToken: String

    private var isUserWriteComp = false  // 유저가 작성한 게시글이 있는지 여부
    private var curTodayCompRank = 0    // 오늘의 칭찬 게시글의 순위

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // 게시글 작성 Intent registerForActivityResult
    private val writeResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 3000) {    // 3000일 경우 글 작성 완료
            val compWriteData = GlobalFunction.getParcelableExtra(result.data!!, "compWriteData", CompData::class.java)!!    // 게시글 데이터

            // 테스트를 위한 인텐트
            val intent = Intent(mainActivity, ComplimentDetailActivity::class.java)
            intent.putParcelableArrayListExtra("compCatData", compCatList)
            intent.putExtra("compData", compWriteData)
            startActivity(intent)
        }
    }

    // 게시글 상세화면 Intent registerForActivityResult
    private val detailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼으로 왔을 경우

//            // 게시글을 클릭해서 들어갔을 경우만 동작
//            // 게시글 작성 시에는 게시글이 추가되므로 Position이 없음 -> 단건 조회 필요 X
//            if(curCompPosition != -1){
//                getOneCompData(result.data?.getIntExtra("boardId", 0)!!)
//            }else{
//                clearAndSetData(curCategoryPosition)   // 데이터 초기화
//            }

        }else if(result.resultCode == 1001){    // 게시글 숨김, 삭제, 차단일 경우

//            clearAndSetData(curCategoryPosition)   // 데이터 초기화
//
////            // 게시글을 클릭해서 들어갔을 경우만 동작
////            if(curCompPosition != -1){
////                // differ의 현재 리스트를 받아와서 newList에 넣기
////                // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
////                val newList = compRecyclerAdapter.differ.currentList.toMutableList()
////
////                newList.removeAt(curCompPosition)
////
////                // adapter의 differ.submitList()로 newList 제출
////                // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
////                compRecyclerAdapter.differ.submitList(newList.toMutableList())
////
////                compDataList.removeAt(curCompPosition)
////            }else{
////                clearAndSetData(curCategoryPosition)   // 데이터 초기화
////            }

        }
    }

    // 랭킹 게시글 전체보기 화면 Intent registerForActivityResult
    private val rankingCompResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼으로 왔을 경우

        }else if (result.resultCode == 2000) {    // 로그인 팝업으로 왔을 경우
            doLogin()   // 로그인 화면으로 이동
        }else if (result.resultCode == 3000) {    // 글쓰기 팝업으로 왔을 경우
            goWriteActivity()   // 게시글 작성 화면으로 화면전환
        }
    }

    // 회원가입 Intent registerForActivityResult
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LoginResult.SUCCESS.code) {

        }else if (result.resultCode == LoginResult.CANCEL.code) {

        } else if (result.resultCode == LoginResult.BACK.code) {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        accessToken = UserData.getAccessToken(mainActivity.applicationContext)

        getCategoryList()   // 카테고리 데이터 조회

        with(binding){
            eventViewpager.adapter = HomeEventViewPagerAdapter(getEventList()) // 어댑터 생성
            eventViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
            eventViewpager.offscreenPageLimit=3
            eventViewpager.getChildAt(0).overScrollMode=View.OVER_SCROLL_NEVER

            val transform = CompositePageTransformer()
            transform.addTransformer(MarginPageTransformer(30))

            transform.addTransformer(ViewPager2.PageTransformer{ view: View, fl: Float ->
                val v = 1 - abs(fl)
                view.alpha = 0.5f + v * 0.5f
                view.scaleY = 0.8f + v * 0.2f
            })

            eventViewpager.setPageTransformer(transform)

            loadData()  // 데이터 가져오기

            // 검색 버튼 클릭 리스너
            schBtn.setOnClickListener(View.OnClickListener {
                val intent = Intent(mainActivity, SearchActivity::class.java)
                startActivity(intent)
            })

            // 글쓰기 플로팅 버튼 클릭 리스너
            btnWrite.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(mainActivity.applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", mainActivity)
                }else{
                    goWriteActivity()
                }
            })

            // '전체 보기' 버튼 클릭 리스너
            btnAllView.setOnClickListener(View.OnClickListener {
                val intent = Intent(mainActivity, HomeRankingCompActivity::class.java)
                intent.putParcelableArrayListExtra("compCatList", compCatList)
                intent.putParcelableArrayListExtra("compRankingList", compRankingList)
                rankingCompResult.launch(intent)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()

        if(UserData.getNickName(mainActivity.applicationContext) != ""){
            binding.loginUserName.text = UserData.getNickName(mainActivity.applicationContext) + " 님!!"
        }else{
            binding.loginUserName.text = "The Clap 님!!"
        }
    }

    // 뷰 페이저에 들어갈 아이템
    private fun getEventList(): ArrayList<Int> {
        return arrayListOf<Int>(R.drawable.test_image_1, R.drawable.test_image_1, R.drawable.test_image_1)
    }

    // 스켈레톤 로딩 화면 UI delay
    private fun loadData() {
        showShimmer = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                showEventData(isLoading = true)

                getCompRanking()

                delay(1000)

                showEventData(isLoading = false)
            }
        }
    }

    private fun showEventData(isLoading: Boolean) {
        with(binding){
            if (isLoading) {
                fragHomeShimmerLayout.startShimmer()
                fragHomeShimmerLayout.visibility = View.VISIBLE
                fragHomeLayout.visibility = View.GONE
            } else {
                fragHomeShimmerLayout.stopShimmer()
                fragHomeShimmerLayout.visibility = View.GONE
                fragHomeLayout.visibility = View.VISIBLE
            }
        }
    }

    // 카테고리 데이터 조회
    private fun getCategoryList() {
        val apiObjectCall = ApiObject.getCompService.getCompCat(authorization = "Bearer $accessToken")

        apiObjectCall.enqueue(object: Callback<CompCatResData> {
            override fun onResponse(call: Call<CompCatResData>, response: Response<CompCatResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    compCatList = response.body()?.data!!

                    compCatList.sortBy { T -> T.boardTypeId }
                }
//                else{
//                    customToast.showCustomToast("카테고리를 불러오지 못했습니다.", mainActivity)
//                }
            }

            override fun onFailure(call: Call<CompCatResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 랭킹 데이터 조회
    private fun getCompRanking(){
        val apiObjectCall = ApiObject.getCompService.getCompRanking(authorization = "Bearer $accessToken")

        apiObjectCall.enqueue(object: Callback<CompRankingResData> {
            override fun onResponse(call: Call<CompRankingResData>, response: Response<CompRankingResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    compRankingList = response.body()?.data!!

                    setData(compRankingList)

                    if(compRankingList.isEmpty()){
                        customToast.showCustomToast("랭킹 데이터가 없습니다.", mainActivity)
                    }

                }
//                else{
//                    customToast.showCustomToast("랭킹 데이터를 불러오지 못했습니다.", mainActivity)
//                }
            }

            override fun onFailure(call: Call<CompRankingResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    private fun setData(rankingList: ArrayList<CompData>){
        val shuffledRankingList = rankingList.shuffled()

        with(binding){

            initUI()    // View 초기화

            for(rankingData in shuffledRankingList){
                when(rankingData.rank){
                    1 -> {
                        textRankEmpty.visibility = View.GONE
                        layoutRankTotal.visibility = View.VISIBLE
                        layoutRank1.visibility = View.VISIBLE
                        btnAllView.visibility = View.VISIBLE

                        rank1CompContent.text = rankingData.content
                        rank1UserName.text = rankingData.nickname
                        rank1CompHeart.text = rankingData.likes.toString()
                        rank1CompCmt.text = rankingData.comments.toString()
                        rank1CompView.text = rankingData.views.toString()

                        rank1CompHeart.isSelected = rankingData.like // 좋아요 여부

                        // 이미지가 있을 경우 1개만 보이기
                        if(rankingData.image.size > 0){
                            rank1CompImage.visibility = View.VISIBLE

                            GlideApp
                                .with(rank1CompImage.context) //context가 어댑터에 없다 -> 뷰에 있겠죠?
                                .load(rankingData.image[0])
                                .into(rank1CompImage)
                        }
                    }
                    2 -> {
                        textRankEmpty.visibility = View.GONE
                        layoutRank2.visibility = View.VISIBLE
                        btnAllView.visibility = View.VISIBLE

                        rank2CompContent.text = rankingData.content
                        rank2UserName.text = rankingData.nickname
                        rank2CompHeart.text = rankingData.likes.toString()
                        rank2CompCmt.text = rankingData.comments.toString()
                        rank2CompView.text = rankingData.views.toString()

                        rank2CompHeart.isSelected = rankingData.like // 좋아요 여부

                        // 이미지가 있을 경우 1개만 보이기
                        if(rankingData.image.size > 0){
                            rank2CompImage.visibility = View.VISIBLE

                            GlideApp
                                .with(rank2CompImage.context) //context가 어댑터에 없다 -> 뷰에 있겠죠?
                                .load(rankingData.image[0])
                                .into(rank2CompImage)
                        }
                    }
                    3 -> {
                        textRankEmpty.visibility = View.GONE
                        layoutRank3.visibility = View.VISIBLE
                        btnAllView.visibility = View.VISIBLE

                        rank3CompContent.text = rankingData.content
                        rank3UserName.text = rankingData.nickname
                        rank3CompHeart.text = rankingData.likes.toString()
                        rank3CompCmt.text = rankingData.comments.toString()
                        rank3CompView.text = rankingData.views.toString()

                        rank3CompHeart.isSelected = rankingData.like // 좋아요 여부

                        // 이미지가 있을 경우 1개만 보이기
                        if(rankingData.image.size > 0){
                            rank3CompImage.visibility = View.VISIBLE

                            GlideApp
                                .with(rank3CompImage.context) //context가 어댑터에 없다 -> 뷰에 있겠죠?
                                .load(rankingData.image[0])
                                .into(rank3CompImage)
                        }
                    }
                    else -> {
                        if(layoutTodayComp.visibility == View.GONE){
                            layoutTodayComp.visibility = View.VISIBLE

                            todayCompTitle.text = rankingData.title
                            todayCompUserName.text = "by " + rankingData.nickname
                            todayCompContent.text = rankingData.content

                            curTodayCompRank = rankingData.rank // 오늘의 칭찬 게시글의 순위
                        }
                    }
                }
            }
        }
    }

    // 게시글 작성 화면으로 화면전환
    private fun goWriteActivity(){
        val intent = Intent(mainActivity, ComplimentWriteActivity::class.java)
        intent.putParcelableArrayListExtra("compCatData", compCatList)
        intent.putExtra("writeType", "friend")
        writeResult.launch(intent)
    }

    // UI 초기화
    private fun initUI(){
        with(binding){
            textRankEmpty.visibility = View.VISIBLE
            layoutRankTotal.visibility = View.GONE
            layoutRank1.visibility = View.GONE
            layoutRank2.visibility = View.GONE
            layoutRank3.visibility = View.GONE
            btnAllView.visibility = View.GONE
            layoutTodayComp.visibility = View.GONE
        }
    }

    // 로그인 화면으로 이동
    private fun doLogin(){
        val intent = Intent(mainActivity, LoginActivity::class.java)
        startForResult.launch(intent)
    }
}