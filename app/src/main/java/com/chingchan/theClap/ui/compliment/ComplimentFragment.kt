package com.chingchan.theClap.ui.compliment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.chingchan.theClap.MainActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiMainBinding
import com.chingchan.theClap.databinding.FragCompBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.ui.compliment.CompBannerViewPagerAdapter
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.login.LoginMembershipAgreementActivity
import com.chingchan.theClap.ui.login.data.LoginResult
import com.chingchan.theClap.ui.login.data.LoginUserReqData
import com.chingchan.theClap.ui.login.data.LoginUserResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplimentFragment : Fragment() {

    private var _binding: FragCompBinding? = null
    private lateinit var mainActivity: MainActivity
    private var compCatList: ArrayList<CompCatData> = ArrayList()
    private lateinit var compFragCatRecyclerAdapter: CompFragCatRecyclerAdapter

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
            // 상단 배너 뷰페이저 설정
            bannerViewpager.adapter = CompBannerViewPagerAdapter(getEventList()) // 어댑터 생성
            bannerViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
            bannerViewpager.offscreenPageLimit=1
            bannerViewpager.getChildAt(0).overScrollMode=View.OVER_SCROLL_NEVER

            // 배너 indicator 멕스 설정
            bannerProgress.max = 2

            bannerViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    bannerProgress.progress = position + 1
                    bannerCount.text = "${position + 1}/${bannerProgress.max}"
                }
            })

            // 카테고리 데이터 SET
            getCategoryList()

            // 지인 칭찬 버튼 클릭 리스너
            btnFriendComp.setOnClickListener(View.OnClickListener {

                // 테스트를 위한 인텐트
                val intent = Intent(mainActivity, ComplimentWriteActivity::class.java)
                intent.putParcelableArrayListExtra("compCatData", compCatList)
                intent.putExtra("writeType", "friend")
                startActivity(intent)

            })

            // 셀프 칭찬 버튼 클릭 리스너
            btnSelfComp.setOnClickListener(View.OnClickListener {

                // 테스트를 위한 인텐트
                val intent = Intent(mainActivity, ComplimentWriteActivity::class.java)
                intent.putParcelableArrayListExtra("compCatData", compCatList)
                intent.putExtra("writeType", "self")
                startActivity(intent)

            })

            compFragCatRecyclerAdapter.clickListener = object : CompFragCatRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int) {
                    Log.e("compFrag", "Intent 시작 1")
                    val intent = Intent(mainActivity, ComplimentActivity::class.java)
                    intent.putParcelableArrayListExtra("compCatData", compCatList)
                    startActivity(intent)
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 뷰 페이저에 들어갈 아이템
    private fun getEventList(): ArrayList<String> {
        return arrayListOf<String>("테스트1", "칭찬으로 만드는 긍정 커뮤니티!! \n" + "더클랩과 오늘 하루도 칭찬으로 시작해요!")
    }

    // 리사이클러뷰에 들어갈 카테고리 리스트
    // 추후 데이터 받아오기
    private fun getCategoryList(): ArrayList<CompCatData> {

        compFragCatRecyclerAdapter = CompFragCatRecyclerAdapter()
        binding.rvCat.adapter = compFragCatRecyclerAdapter

        val apiObjectCall = ApiObject.getCompService.getCompCat()

        apiObjectCall.enqueue(object: Callback<CompCatResData> {
            override fun onResponse(call: Call<CompCatResData>, response: Response<CompCatResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    compCatList = response.body()?.data!!

                    Log.e("comp result success", compCatList.toString())

                    compCatList.add(0, CompCatData(isSelect = true))

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

        return compCatList
    }
}