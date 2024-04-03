package com.chingchan.theClap.ui.home.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiHomeRankingCompBinding
import com.chingchan.theClap.databinding.ActiMypageAnnounceBinding
import com.chingchan.theClap.databinding.ActiMypageFaqBinding
import com.chingchan.theClap.databinding.ActiMypageProfileBinding
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.activity.ComplimentDetailActivity
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompDetResData
import com.chingchan.theClap.ui.compliment.data.CompOneResData
import com.chingchan.theClap.ui.compliment.data.CompResData
import com.chingchan.theClap.ui.home.adapter.CompRankingRecyclerAdapter
import com.chingchan.theClap.ui.home.dialog.CompRankingLoginDialog
import com.chingchan.theClap.ui.home.dialog.CompRankingNewWriteDialog
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

class HomeRankingCompActivity : AppCompatActivity() {
    private lateinit var binding: ActiHomeRankingCompBinding

    private var compRankingRecyclerAdapter: CompRankingRecyclerAdapter = CompRankingRecyclerAdapter()  // 랭킹 게시글 리사이클러뷰 어뎁터

    private val loginDialog: CompRankingLoginDialog by lazy { CompRankingLoginDialog(this) }
    private val newWriteDialog: CompRankingNewWriteDialog by lazy { CompRankingNewWriteDialog(this) }

    private var compCatList: ArrayList<CompCatData> = ArrayList()   // 카테고리 데이터
    private var compRankingList: ArrayList<CompData> = ArrayList()  // 게시글 랭킹 데이터

    private var curCompPosition = -1 // 현재 게시글 position
    private var isUserWriteComp = false  // 유저가 작성한 게시글이 있는지 여부

    // 게시글 상세화면 Intent registerForActivityResult
    private val detailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼으로 왔을 경우

            getOneCompData(result.data?.getIntExtra("boardId", 0)!!)

        }else if(result.resultCode == 1001){    // 게시글 숨김, 삭제, 차단일 경우
            // differ의 현재 리스트를 받아와서 newList에 넣기
            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
            val newList = compRankingRecyclerAdapter.differ.currentList.toMutableList()

            newList.removeAt(curCompPosition)

            // adapter의 differ.submitList()로 newList 제출
            // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
            compRankingRecyclerAdapter.differ.submitList(newList.toMutableList())

        }else if(result.resultCode == 1002){    // 게시글 신고일 경우

            getOneCompData(result.data?.getIntExtra("boardId", 0)!!)

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiHomeRankingCompBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compCatList = GlobalFunction.getSerializableExtra(intent, "compCatList", ArrayList<CompCatData>()::class.java)!!
        compRankingList = GlobalFunction.getSerializableExtra(intent, "compRankingList", ArrayList<CompData>()::class.java)!!

        // 로그인 되어 있으면 유저가 작성한 게시글이 있는지 확인
        if(isLoginCheck()){
            isUserWriteComp(UserData.getUserId(applicationContext))
        }

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){
            // 랭킹 게시글 리사이클러뷰
            rvRankingComp.adapter = compRankingRecyclerAdapter
            rvRankingComp.itemAnimator = null

            // DiffUtil 적용 후 데이터 추가
            compRankingRecyclerAdapter.differ.submitList(compRankingList.toMutableList())

            // 랭킹 게시글 클릭 리스너
            compRankingRecyclerAdapter.clickListener = object : CompRankingRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "TOTAL"){  // 전체 영역 클릭 시
                        if(!isLoginCheck()){
                            loginDialog.show()
                        }else if(!isUserWriteComp){
                            newWriteDialog.show()
                        }else{
                            curCompPosition = position  // 현재 누른 위치의 position 갱신

                            // differ의 현재 리스트를 받아와서 newList에 넣기
                            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                            val newList = compRankingRecyclerAdapter.differ.currentList.toMutableList()

                            increaseView(newList[position])  // 조회 수 증가
                        }
                    }
                }
            }

            // 로그인 다이얼로그 클릭 리스너
            loginDialog.setListener(object : CompRankingLoginDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "KAKAO" || type == "NAVER" || type == "GOOGLE"){
                        loginDialog.dismiss()

                        setResult(2000)
                        finish()
                    }else if(type == "CANCEL"){
                        loginDialog.dismiss()
                    }
                }
            })

            // 칭찬글 새로 작성 다이얼로그 클릭 리스너
            newWriteDialog.setListener(object : CompRankingNewWriteDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "FRIEND"){
                        newWriteDialog.dismiss()

                        setResult(3000)
                        finish()
                    }else if(type == "CANCEL"){
                        newWriteDialog.dismiss()
                    }
                }
            })

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                setResult(1000, intent)
                finish()
            })
        }
    }

    // 게시글 조회수 증가
    private fun increaseView(compData: CompData){
        // 본인이 작성한 글일 경우 조회수 증가 X
        if(compData.userId != UserData.getUserId(applicationContext)){
            val apiObjectCall = ApiObject.getCompService.increaseView(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
                , boardId = compData.id)

            apiObjectCall.enqueue(object: Callback<CompDetResData> {
                override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                    if(response.isSuccessful) {
                        val status = response.body()?.status!!
                        if(status == "success"){
                            // 게시글 상세 Activity로 화면 전환
                            val intent = Intent(this@HomeRankingCompActivity, ComplimentDetailActivity::class.java)
                            intent.putParcelableArrayListExtra("compCatData", compCatList)
                            compData.views = compData.views + 1
                            intent.putExtra("compData", compData)
                            detailResult.launch(intent)
                        }else{
                            customToast.showCustomToast("게시글 조회수 증가가 되지 못했습니다.", this@HomeRankingCompActivity)
                        }

                    }else{
                        customToast.showCustomToast("게시글 조회수 증가가 되지 못했습니다.", this@HomeRankingCompActivity)
                    }
                }

                override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                    customToast.showCustomToast("Call Failed", this@HomeRankingCompActivity)
                }
            })
        }else{
            // 게시글 상세 Activity로 화면 전환
            val intent = Intent(this@HomeRankingCompActivity, ComplimentDetailActivity::class.java)
            intent.putParcelableArrayListExtra("compCatData", compCatList)
            intent.putExtra("compData", compData)
            detailResult.launch(intent)
        }
    }

    // 게시글 단건 조회
    private fun getOneCompData(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.getOneComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompOneResData> {
            override fun onResponse(call: Call<CompOneResData>, response: Response<CompOneResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compRankingRecyclerAdapter.differ.currentList.toMutableList()

                        val rank = newList[curCompPosition].rank    // 게시글 순위

                        newList[curCompPosition] = response.body()?.data
                        newList[curCompPosition].rank = rank

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        compRankingRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 조회를 못했습니다.", this@HomeRankingCompActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 조회를 못했습니다.", this@HomeRankingCompActivity)
                }
            }

            override fun onFailure(call: Call<CompOneResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@HomeRankingCompActivity)
            }
        })
    }

    // 로그인 되어 있는지 체크
    private fun isLoginCheck(): Boolean{
        return ((UserData.getUserId(applicationContext) != 0))
    }

    // 게시글을 1개라도 작성했는지 체크
    private fun isUserWriteComp(userId: Int){
        val apiObjectCall = ApiObject.getCompService.getComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , userId = userId, page = 0, size = 1)

        apiObjectCall.enqueue(object: Callback<CompResData> {
            override fun onResponse(call: Call<CompResData>, response: Response<CompResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    isUserWriteComp = response.body()?.data?.content!!.isNotEmpty()     // 유저가 작성한 게시글이 있는지 확인

                }else{
                    customToast.showCustomToast("게시글 작성여부를 불러오지 못했습니다.", this@HomeRankingCompActivity)
                }
            }

            override fun onFailure(call: Call<CompResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@HomeRankingCompActivity)
            }
        })
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            setResult(1000, intent)
            finish()
        }
    }
}