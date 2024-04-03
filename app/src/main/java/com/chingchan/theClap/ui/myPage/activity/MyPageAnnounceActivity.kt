package com.chingchan.theClap.ui.myPage.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiMypageAnnounceBinding
import com.chingchan.theClap.databinding.ActiMypageFaqBinding
import com.chingchan.theClap.databinding.ActiMypageProfileBinding
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.adapter.MyPageAnnounceRecyclerAdapter
import com.chingchan.theClap.ui.myPage.adapter.MyPageBookmarkRecyclerAdapter
import com.chingchan.theClap.ui.myPage.adapter.MyPageFAQRecyclerAdapter
import com.chingchan.theClap.ui.myPage.data.AnnounceData
import com.chingchan.theClap.ui.myPage.data.AnnounceResData
import com.chingchan.theClap.ui.myPage.data.FAQData
import com.chingchan.theClap.ui.myPage.data.FAQResData
import com.chingchan.theClap.ui.myPage.dialog.MyPageProfileImageDialog
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageAnnounceActivity : AppCompatActivity() {
    private lateinit var binding: ActiMypageAnnounceBinding

    private var myPageAnnounceRecyclerAdapter: MyPageAnnounceRecyclerAdapter = MyPageAnnounceRecyclerAdapter()  // 공지사항 리사이클러뷰 어뎁터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMypageAnnounceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){
            // 공지사항 리사이클러뷰
            rvAnnounce.adapter = myPageAnnounceRecyclerAdapter
            rvAnnounce.itemAnimator = null

            getAnnounceData()    // 서버에서 데이터 받아와야 함

            // 공지사항 클릭 리스너
            myPageAnnounceRecyclerAdapter.clickListener = object : MyPageAnnounceRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "TOTAL"){  // 전체 영역 클릭 시
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = myPageAnnounceRecyclerAdapter.differ.currentList.toMutableList()

                        // 객체의 주소를 바꾸기 위해 copy 사용
                        val newData = newList[position].copy(isAnnounceContVisible = !newList[position].isAnnounceContVisible)

                        // 새로운 객체로 저장
                        newList[position] = newData

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        myPageAnnounceRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }
                }
            }

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                finish()
            })
        }
    }

    private fun getAnnounceData(){
        val apiObjectCall = ApiObject.getMyPageService.getAnnounce(authorization = "Bearer " + UserData.getAccessToken(applicationContext))

        apiObjectCall.enqueue(object: Callback<AnnounceResData> {
            override fun onResponse(call: Call<AnnounceResData>, response: Response<AnnounceResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val announceDataList = response.body()?.data!!

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        myPageAnnounceRecyclerAdapter.differ.submitList(announceDataList.toMutableList())
                    }else{
                        customToast.showCustomToast("공지사항 조회를 못했습니다.", this@MyPageAnnounceActivity)
                    }

                }else{
                    customToast.showCustomToast("공지사항 조회를 못했습니다.", this@MyPageAnnounceActivity)
                }
            }

            override fun onFailure(call: Call<AnnounceResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageAnnounceActivity)
            }
        })
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }
}