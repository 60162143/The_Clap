package com.chingchan.theClap.ui.compliment.fragment

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
import androidx.viewpager2.widget.ViewPager2
import com.chingchan.theClap.MainActivity
import com.chingchan.theClap.databinding.FragCompBinding
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.activity.ComplimentActivity
import com.chingchan.theClap.ui.compliment.activity.ComplimentDetailActivity
import com.chingchan.theClap.ui.compliment.activity.ComplimentWriteActivity
import com.chingchan.theClap.ui.compliment.adapter.CompBannerViewPagerAdapter
import com.chingchan.theClap.ui.compliment.adapter.CompFragCatRecyclerAdapter
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.compliment.data.CompData
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

class ComplimentFragment : Fragment() {

    private var _binding: FragCompBinding? = null
    private lateinit var mainActivity: MainActivity

    private var compCatList: ArrayList<CompCatData> = ArrayList()
    private var bannerList: ArrayList<String> = ArrayList()

    private lateinit var compFragCatRecyclerAdapter: CompFragCatRecyclerAdapter

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

    private var showShimmer: Job? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragCompBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            bannerList = getEventList() // 배너 데이터

            // 상단 배너 뷰페이저 설정
            bannerViewpager.adapter = CompBannerViewPagerAdapter(bannerList) // 어댑터 생성
            bannerViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
            bannerViewpager.offscreenPageLimit=1
            bannerViewpager.getChildAt(0).overScrollMode=View.OVER_SCROLL_NEVER

            // 배너 indicator 멕스 설정
            bannerProgress.max = bannerList.size

            bannerViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    bannerProgress.progress = position + 1
                    bannerCount.text = "${position + 1}/${bannerProgress.max}"
                }
            })

            // 카테고리 리사이클러뷰 어뎁터
            compFragCatRecyclerAdapter = CompFragCatRecyclerAdapter()
            binding.rvCat.adapter = compFragCatRecyclerAdapter

            loadData()  // 데이터 가져오기

            // 지인 칭찬 버튼 클릭 리스너
            btnFriendComp.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(mainActivity.applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", mainActivity)
                }else{
                    val intent = Intent(mainActivity, ComplimentWriteActivity::class.java)
                    intent.putParcelableArrayListExtra("compCatData", compCatList)
                    intent.putExtra("writeType", "friend")
                    writeResult.launch(intent)
                }
            })

//            // 셀프 칭찬 버튼 클릭 리스너
//            btnSelfComp.setOnClickListener(View.OnClickListener {
//
//                // 테스트를 위한 인텐트
//                val intent = Intent(mainActivity, ComplimentWriteActivity::class.java)
//                intent.putParcelableArrayListExtra("compCatData", compCatList)
//                intent.putExtra("writeType", "self")
//                startActivity(intent)
//
//            })

            // 카테고리 리사이클러뷰 클릭 리스너
            compFragCatRecyclerAdapter.clickListener = object : CompFragCatRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int) {
                    val intent = Intent(mainActivity, ComplimentActivity::class.java)
                    intent.putParcelableArrayListExtra("compCatData", compCatList)
                    intent.putExtra("boardTypeId", compCatList[position].boardTypeId)
                    startActivity(intent)
                }
            }

            // 검색 버튼 클릭 리스너
            schBtn.setOnClickListener(View.OnClickListener {
                val intent = Intent(mainActivity, SearchActivity::class.java)
                startActivity(intent)
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 뷰 페이저에 들어갈 아이템
    private fun getEventList(): ArrayList<String> {
        return arrayListOf<String>("칭찬으로 만드는 긍정 커뮤니티!! \n" + "더클랩과 오늘 하루도 칭찬으로 시작해요!")
    }

    // 리사이클러뷰에 들어갈 카테고리 리스트
    private fun getCategoryList() {
        val apiObjectCall = ApiObject.getCompService.getCompCat(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext))

        apiObjectCall.enqueue(object: Callback<CompCatResData> {
            override fun onResponse(call: Call<CompCatResData>, response: Response<CompCatResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    compCatList = response.body()?.data!!

                    compCatList.sortBy { T -> T.boardTypeId }

                    // DiffUtil 적용 후 데이터 추가
                    compFragCatRecyclerAdapter.differ.submitList(compCatList)

                }else{
                    customToast.showCustomToast("카테고리를 불러오지 못했습니다.", mainActivity)
                }
            }

            override fun onFailure(call: Call<CompCatResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 스켈레톤 로딩 화면 UI delay
    private fun loadData() {
        showShimmer = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                showEventData(isLoading = true)
                delay(1000)

                // 카테고리 데이터 SET
                getCategoryList()

                showEventData(isLoading = false)
            }
        }
    }

    private fun showEventData(isLoading: Boolean) {
        with(binding){
            if (isLoading) {
                fragCompShimmerLayout.startShimmer()
                fragCompShimmerLayout.visibility = View.VISIBLE
                fragCompLayout.visibility = View.GONE
                titleConstraintLayout.visibility = View.GONE
            } else {
                fragCompShimmerLayout.stopShimmer()
                fragCompShimmerLayout.visibility = View.GONE
                fragCompLayout.visibility = View.VISIBLE
                titleConstraintLayout.visibility = View.VISIBLE
            }
        }
    }
}