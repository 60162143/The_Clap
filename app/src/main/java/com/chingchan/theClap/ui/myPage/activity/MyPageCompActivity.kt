package com.chingchan.theClap.ui.myPage.activity

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiMypageCompBinding
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.ui.compliment.adapter.CompRecyclerAdapter
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.adapter.MyPageCompVpAdapter
import com.chingchan.theClap.ui.myPage.fragment.MyPageWriteCompFragment
import com.chingchan.theClap.ui.toast.customToast
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageCompActivity : AppCompatActivity() {
    private lateinit var binding: ActiMypageCompBinding

    private lateinit var myPageCompVpAdapter: MyPageCompVpAdapter

    var compCatList: ArrayList<CompCatData> = ArrayList()   // 카테고리 데이터

    private var isEditCheck = false

    private val tabTitle = arrayOf("받은 칭찬", "작성한 칭찬")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMypageCompBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        getCategoryList()   // 카테고리 데이터 조회

        with(binding){

            myPageCompVpAdapter = MyPageCompVpAdapter(this@MyPageCompActivity)
            vpComp.adapter = myPageCompVpAdapter

            TabLayoutMediator(tabLayout, vpComp){ tab, position -> tab.text = tabTitle[position] }.attach()

            // 편집 버튼 클릭 리스너
            btnEdit.setOnClickListener(View.OnClickListener {

                isEditCheck = !isEditCheck  // 편집 버튼 활성화 여부 변경

                // 편집 버튼 활성화 여부에 따른 텍스트 변화
                if(isEditCheck){
                    btnEdit.text = "완료"
                    btnEdit.setTextColor(ContextCompat.getColor(applicationContext!!, R.color.brand))
                }else{
                    btnEdit.text = "편집"
                    btnEdit.setTextColor(ContextCompat.getColor(applicationContext!!, R.color.gray6))
                }

                // 탭 레이아웃의 Fragment에 변화 알림
                myPageCompVpAdapter.getReceiveFragment().editCheck(isEditCheck)
                myPageCompVpAdapter.getWriteFragment().editCheck(isEditCheck)

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

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(1000, intent)
            finish()
        }
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
                    customToast.showCustomToast("카테고리를 불러오지 못했습니다.", this@MyPageCompActivity)
                }
            }

            override fun onFailure(call: Call<CompCatResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageCompActivity)
            }
        })
    }
}