package com.chingchan.theClap.ui.myPage.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiMypageFaqBinding
import com.chingchan.theClap.databinding.ActiMypageProfileBinding
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompOneResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.adapter.MyPageBookmarkRecyclerAdapter
import com.chingchan.theClap.ui.myPage.adapter.MyPageFAQRecyclerAdapter
import com.chingchan.theClap.ui.myPage.data.FAQData
import com.chingchan.theClap.ui.myPage.data.FAQResData
import com.chingchan.theClap.ui.myPage.dialog.MyPageProfileImageDialog
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFAQActivity : AppCompatActivity() {
    private lateinit var binding: ActiMypageFaqBinding

    private lateinit var myPageFAQRecyclerAdapter: MyPageFAQRecyclerAdapter // FAQ 리사이클러뷰 어뎁터

    private var faqDataList: ArrayList<FAQData> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMypageFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            // FAQ 리사이클러뷰
            myPageFAQRecyclerAdapter = MyPageFAQRecyclerAdapter()
            rvFaq.adapter = myPageFAQRecyclerAdapter
            rvFaq.itemAnimator = null

            getFAQData()    // 서버에서 데이터 받아와야 함

            // FAQ 클릭 리스너
            myPageFAQRecyclerAdapter.clickListener = object : MyPageFAQRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "TOTAL"){  // 전체 영역 클릭 시
                        faqDataList[position].isFaqContVisible = !faqDataList[position].isFaqContVisible

                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageFAQRecyclerAdapter.differ.currentList.toMutableList()

                        // 객체의 주소를 바꾸기 위해 copy 사용
                        val newData = newList[position].copy(isFaqContVisible = faqDataList[position].isFaqContVisible)

                        // 새로운 객체로 저장
                        newList[position] = newData

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        myPageFAQRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }
                }
            }

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                finish()
            })
        }
    }

    // FAQ 조회
    private fun getFAQData(){
        val apiObjectCall = ApiObject.getMyPageService.getFAQ(authorization = "Bearer " + UserData.getAccessToken(applicationContext))

        apiObjectCall.enqueue(object: Callback<FAQResData> {
            override fun onResponse(call: Call<FAQResData>, response: Response<FAQResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        faqDataList = response.body()?.data!!

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        myPageFAQRecyclerAdapter.differ.submitList(faqDataList.toMutableList())
                    }else{
                        customToast.showCustomToast("FAQ 조회를 못했습니다.", this@MyPageFAQActivity)
                    }

                }else{
                    customToast.showCustomToast("FAQ 조회를 못했습니다.", this@MyPageFAQActivity)
                }
            }

            override fun onFailure(call: Call<FAQResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageFAQActivity)
            }
        })
    }
}