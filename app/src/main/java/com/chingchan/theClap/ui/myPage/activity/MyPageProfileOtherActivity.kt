package com.chingchan.theClap.ui.myPage.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiMypageProfileOtherBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.activity.ComplimentDetailActivity
import com.chingchan.theClap.ui.compliment.activity.ComplimentWriteActivity
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompReportReqData
import com.chingchan.theClap.ui.compliment.data.CompReportResData
import com.chingchan.theClap.ui.login.data.LoginUserResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.adapter.MyPageOtherCompVpAdapter
import com.chingchan.theClap.ui.myPage.data.BlockUserReqData
import com.chingchan.theClap.ui.myPage.data.BlockUserResData
import com.chingchan.theClap.ui.myPage.data.FollowUserReqData
import com.chingchan.theClap.ui.myPage.data.FollowUserResData
import com.chingchan.theClap.ui.myPage.data.ProfileData
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.myPage.data.ReportUserReqData
import com.chingchan.theClap.ui.myPage.data.ReportUserResData
import com.chingchan.theClap.ui.myPage.dialog.MyPageBookmarkEditDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageBookmarkReportDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageMemberUnlinkDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherUserBlockDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherUserEditDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherUserReportDetailDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherUserReportDialog
import com.chingchan.theClap.ui.toast.customToast
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class MyPageProfileOtherActivity : AppCompatActivity() {
    private lateinit var binding: ActiMypageProfileOtherBinding

    private lateinit var myPageProfileOtherCompVpAdapter: MyPageOtherCompVpAdapter

    private val otherUserEditDialog: MyPageOtherUserEditDialog by lazy { MyPageOtherUserEditDialog(this) } // 타 유저 더보기 다이얼로그
    private val otherUserReportDialog: MyPageOtherUserReportDialog by lazy { MyPageOtherUserReportDialog(this, userName) } // 타 유저 신고하기 다이얼로그
    private val otherUserReportDetailDialog: MyPageOtherUserReportDetailDialog by lazy { MyPageOtherUserReportDetailDialog(this) } // 타 유저 신고하기 상세 다이얼로그
    private val otherUserBlockDialog: MyPageOtherUserBlockDialog by lazy { MyPageOtherUserBlockDialog(this) } // 타 유저 차단하기 다이얼로그

    private lateinit var profileData: ProfileData   // 프로필 데이터

    var compCatList: ArrayList<CompCatData> = ArrayList()   // 카테고리 데이터

    var profileUserId: Int = 0  // 프로필 데이터의 유저 ID (탭 레이아웃의 하위 Fragment에서 사용하기 위한 변수)

    private val tabTitle = arrayOf("받은 칭찬", "작성한 칭찬")
    private var userName: String = ""   // 유저 닉네임

    // 받은 칭찬과 작성한 칭찬의 새로고침이 완료되었는지에 대한 Flag
    var isReceiveRefreshComplete = false
    var isWriteRefreshComplete = false

    // 게시글 작성 Intent registerForActivityResult
    private val writeResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 3000) {    // 3000일 경우 글 작성 완료
            val compWriteData = GlobalFunction.getParcelableExtra(result.data!!, "compWriteData", CompData::class.java)!!    // 게시글 데이터

            // 테스트를 위한 인텐트
            val intent = Intent(this, ComplimentDetailActivity::class.java)
            intent.putParcelableArrayListExtra("compCatData", compCatList)
            intent.putExtra("compData", compWriteData)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMypageProfileOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileData = GlobalFunction.getParcelableExtra(intent, "profileData", ProfileData::class.java)!!    // 프로필 데이터
        profileUserId = profileData.id  // 프로필 데이터의 유저 ID (탭 레이아웃의 하위 Fragment에서 사용하기 위한 변수)

        initView()  // View 및 데이터 초기화

        getCategoryList()   // 카테고리 데이터 조회

        with(binding){
            myPageProfileOtherCompVpAdapter = MyPageOtherCompVpAdapter(this@MyPageProfileOtherActivity)
            vpComp.adapter = myPageProfileOtherCompVpAdapter

            TabLayoutMediator(tabLayout, vpComp){ tab, position -> tab.text = tabTitle[position] }.attach()

            // ViewPager2의 페이지 변경 이벤트를 수신합니다.
            vpComp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // 스와이프를 감지합니다.

                    if(vpComp.currentItem == 0){
                        myPageProfileOtherCompVpAdapter.getOtherWriteFragment().eraseDraw()
                        myPageProfileOtherCompVpAdapter.getOtherReceiveFragment().redraw()
                    }else{
                        myPageProfileOtherCompVpAdapter.getOtherReceiveFragment().eraseDraw()
                        myPageProfileOtherCompVpAdapter.getOtherWriteFragment().redraw()
                    }
                }
            })

            mainScrollView.run {
                header = headerView
                userName = titleUserName
                stickListener = { _ ->
                    Log.d("LOGGER_TAG", "stickListener")
                }
                freeListener = { _ ->
                    Log.d("LOGGER_TAG", "freeListener")
                }
            }

            mainView.setOnRefreshListener {
                // 타인이 작성한 칭찬의 화면이 그려졌을 경우
                if(myPageProfileOtherCompVpAdapter.getOtherWriteFragment().isAdded){
                    // 탭 레이아웃의 Fragment에 변화 알림
                    myPageProfileOtherCompVpAdapter.getOtherReceiveFragment().refreshData()
                    myPageProfileOtherCompVpAdapter.getOtherWriteFragment().refreshData()

                    // 탭 레이아웃 화면에 따른 뷰 그리기/지우기
                    if(vpComp.currentItem == 0){
                        myPageProfileOtherCompVpAdapter.getOtherWriteFragment().eraseDraw()
                        myPageProfileOtherCompVpAdapter.getOtherReceiveFragment().redraw()
                    }else{
                        myPageProfileOtherCompVpAdapter.getOtherReceiveFragment().eraseDraw()
                        myPageProfileOtherCompVpAdapter.getOtherWriteFragment().redraw()
                    }

                    // 새로고침이 완료 될때까지 대기
                    while(!isReceiveRefreshComplete or !isWriteRefreshComplete){

                    }

                    // 새로고침 변수 초기화
                    isReceiveRefreshComplete = false
                    isWriteRefreshComplete = false
                }else{  // 타인이 작성한 칭찬의 화면이 아직 안 그려졌을 경우
                    // 탭 레이아웃의 Fragment에 변화 알림
                    myPageProfileOtherCompVpAdapter.getOtherReceiveFragment().refreshData()

                    // 새로고침이 완료 될때까지 대기
                    while(!isReceiveRefreshComplete){

                    }

                    // 새로고침 변수 초기화
                    isReceiveRefreshComplete = false
                }

                mainView.isRefreshing = false   // 새로고침 상태 종료
            }

            // 더보기 버튼 클릭 리스너
            btnMore.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", this@MyPageProfileOtherActivity)
                }else{
                    // 본인 프로필이 아닐 경우
                    if(profileData.id != UserData.getUserId(applicationContext)){
                        otherUserEditDialog.show()
                    }
                }
            })

            // 팔로우 버튼 클릭 리스너
            btnFollow.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", this@MyPageProfileOtherActivity)
                }else{
                    followUser(FollowUserReqData(user = UserData.getUserId(applicationContext), follow = profileData.id))
                }
            })

            // 칭찬글 작성 버튼 클릭 리스너
            btnWrite.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", this@MyPageProfileOtherActivity)
                }else{
                    val intent = Intent(this@MyPageProfileOtherActivity, ComplimentWriteActivity::class.java)
                    intent.putParcelableArrayListExtra("compCatData", compCatList)
                    intent.putExtra("writeType", "friend")
                    writeResult.launch(intent)
                }
            })

            // 타 유저 더보기 다이얼로그 클릭 리스너
            otherUserEditDialog.setListener(object : MyPageOtherUserEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "REPORT"){
                        otherUserEditDialog.dismiss()

                        otherUserReportDialog.show()
                    }else if(type == "BLOCK"){
                        otherUserEditDialog.dismiss()

                        otherUserBlockDialog.show()
                    }else if(type == "CANCEL"){
                        otherUserEditDialog.dismiss()
                    }
                }
            })

            // 타 유저 신고하기 다이얼로그 클릭 리스너
            otherUserReportDialog.setListener(object : MyPageOtherUserReportDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "REPORT"){
                        otherUserReportDialog.dismiss()

                        otherUserReportDetailDialog.show()
                    }else if(type == "CANCEL"){
                        otherUserReportDialog.dismiss()
                    }
                }
            })

            // 타 유저 신고하기 상세 다이얼로그 클릭 리스너
            otherUserReportDetailDialog.setListener(object : MyPageOtherUserReportDetailDialog.OnClickListener{
                override fun onClick(type: String, reportType: String, reportReason: String) {
                    if(type == "REPORT"){
                        otherUserReportDetailDialog.dismiss()

                        reportUser(ReportUserReqData(profileUserId, reportReason, reportType))  // 유저 신고
                    }else if(type == "CANCEL"){
                        otherUserReportDetailDialog.dismiss()
                    }
                }
            })

            // 타 유저 차단하기 다이얼로그 클릭 리스너
            otherUserBlockDialog.setListener(object : MyPageOtherUserBlockDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "BLOCK"){
                        otherUserBlockDialog.dismiss()

                        blockUser(BlockUserReqData(block = profileData.id))
                    }else if(type == "CANCEL"){
                        otherUserBlockDialog.dismiss()
                    }
                }
            })

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                finish()
            })
        }
    }

    // 유저 신고
    private fun reportUser(reportReqData: ReportUserReqData){
        val apiObjectCall = ApiObject.getMyPageService.reportUser(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , reportReqData)

        apiObjectCall.enqueue(object: Callback<ReportUserResData> {
            override fun onResponse(call: Call<ReportUserResData>, response: Response<ReportUserResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        customToast.showCustomToast("유저가 신고 되었습니다.", this@MyPageProfileOtherActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<ReportUserResData>(
                        ReportUserResData::class.java,
                        ReportUserResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00101.name){    // 이미 신고한 유저
                            customToast.showCustomToast(ErrorCode.S00101.message, this@MyPageProfileOtherActivity)
                        }else{
                            customToast.showCustomToast("유저가 신고 되지 못했습니다.", this@MyPageProfileOtherActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ReportUserResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageProfileOtherActivity)
            }
        })
    }

    // 유저 차단
    private fun blockUser(blockUserResData: BlockUserReqData){
        val apiObjectCall = ApiObject.getMyPageService.blockUser(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , blockUserResData)

        apiObjectCall.enqueue(object: Callback<BlockUserResData> {
            override fun onResponse(call: Call<BlockUserResData>, response: Response<BlockUserResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        customToast.showCustomToast("해당 사용자가 정상적으로 차단되었습니다.", this@MyPageProfileOtherActivity)
                    }

                }
//                else{
//                    val errorData = ApiObject.getRetrofit.responseBodyConverter<BlockUserResData>(
//                        BlockUserResData::class.java,
//                        BlockUserResData::class.java.annotations
//                    ).convert(response.errorBody()!!)
//
//                    errorData?.code.let{
//                        if(it == ErrorCode.S00028.name){    // 이미 신고한 게시글
//                            customToast.showCustomToast(ErrorCode.S00028.message, this@MyPageBookmarkActivity)
//                        }else{
//                            customToast.showCustomToast("게시물이 신고 되지 못했습니다.", this@MyPageBookmarkActivity)
//                        }
//                    }
//                }
            }

            override fun onFailure(call: Call<BlockUserResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageProfileOtherActivity)
            }
        })
    }

    // 유저 팔로우
    private fun followUser(followUserResData: FollowUserReqData){
        val apiObjectCall = ApiObject.getMyPageService.followUser(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , followUserResData)

        apiObjectCall.enqueue(object: Callback<FollowUserResData> {
            override fun onResponse(call: Call<FollowUserResData>, response: Response<FollowUserResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        with(binding){
                            val followYn = response.body()?.data?.followYN ?: false // 팔로우 적용/취소 여부

                            if(followYn){
                                btnFollow.text = "팔로잉"
                                profileData.follower += 1

                                customToast.showCustomToast("${profileData.nickname}님을 팔로우했습니다.", this@MyPageProfileOtherActivity)
                            }else{
                                btnFollow.text = "팔로우"
                                profileData.follower -= 1

                                customToast.showCustomToast("${profileData.nickname}님의 팔로우를 취소하였습니다.", this@MyPageProfileOtherActivity)
                            }

                            btnFollow.isSelected = followYn
                            profileData.follow = followYn
                            followerCount.text = profileData.follower.toString()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<FollowUserResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageProfileOtherActivity)
            }
        })
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
                    customToast.showCustomToast("카테고리를 불러오지 못했습니다.", this@MyPageProfileOtherActivity)
                }
            }

            override fun onFailure(call: Call<CompCatResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageProfileOtherActivity)
            }
        })
    }

    private fun initView(){
        with(binding){
            titleUserName.text = profileData.nickname

            userName.text = profileData.nickname
            userIntroduction.text = profileData.introduction

            followerCount.text = profileData.follower.toString()
            followingCount.text = profileData.following.toString()

            btnFollow.isSelected = profileData.follow   // 팔로우 여부

            setFollowBtn(profileData.follow)    // 팔로우 버튼 SET

            // 본인 프로필 조회 시 팔로우 버튼, 상단 더보기 버튼 비활성화
            if(profileData.id == UserData.getUserId(applicationContext)){
                btnFollow.isEnabled = false
                btnMore.visibility = View.GONE
            }
        }
    }

    // 팔로우 버튼 SET
    private fun setFollowBtn(tf: Boolean){
        with(binding){
            if(tf){
                btnFollow.text = "팔로잉"
            }else{
                btnFollow.text = "팔로우"
            }
        }
    }
}