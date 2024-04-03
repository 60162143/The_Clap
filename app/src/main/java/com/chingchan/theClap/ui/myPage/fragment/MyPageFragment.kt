package com.chingchan.theClap.ui.myPage.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.chingchan.theClap.MainActivity
import com.chingchan.theClap.databinding.FragMypageBinding
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.globalFunction.InitApplication
import com.chingchan.theClap.ui.compliment.activity.ComplimentDetailActivity
import com.chingchan.theClap.ui.compliment.activity.ComplimentWriteActivity
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompResData
import com.chingchan.theClap.ui.login.activity.LoginActivity
import com.chingchan.theClap.ui.login.data.LoginType
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.activity.MyPageBookmarkActivity
import com.chingchan.theClap.ui.myPage.activity.MyPageCompActivity
import com.chingchan.theClap.ui.myPage.activity.MyPageFAQActivity
import com.chingchan.theClap.ui.myPage.activity.MyPageNotificationActivity
import com.chingchan.theClap.ui.myPage.activity.MyPageProfileActivity
import com.chingchan.theClap.ui.myPage.activity.MyPageSettingActivity
import com.chingchan.theClap.ui.myPage.data.CompBookmarkResData
import com.chingchan.theClap.ui.myPage.data.ProfileData
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.myPage.dialog.MyPageLogoutDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageRecCompEditDialog
import com.chingchan.theClap.ui.toast.customToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {

    private var _binding: FragMypageBinding? = null
    private lateinit var mainActivity: MainActivity

    private val logoutDialog: MyPageLogoutDialog by lazy { MyPageLogoutDialog(mainActivity) } // 로그아웃 다이얼로그

    private var bookmarkCompList: ArrayList<CompData> = ArrayList()

    private lateinit var profileData: ProfileData   // 프로필 데이터

    private val binding get() = _binding!!

    // 칭찬내역 게시물 Intent registerForActivityResult
    private val postsResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼으로 왔을 경우
            getPosts()  // 작성한 칭찬내역 수 조회
        }
    }

    // 북마크 게시물 Intent registerForActivityResult
    private val bookmarkResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼으로 왔을 경우
            bookmarkCompList = GlobalFunction.getSerializableExtra(result.data!!, "bookmarkCompList", ArrayList<CompData>()::class.java)!!

            binding.bookmarkCount.text = bookmarkCompList.size.toString()
        }
    }

    // 내활동 Intent registerForActivityResult
    private val notificationResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1001) {    // 인기글 관련 알림 클릭으로 왔을 경우
            mainActivity.moveToHome()   // 홈 화면으로 이동
        }
    }

    // 설정 Intent registerForActivityResult
    private val settingResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 1000일 경우 계정 탈퇴
            val intent = Intent(mainActivity, LoginActivity::class.java)
            mainActivity.startForResult.launch(intent)
        }
    }

    // 프로필 수정 Intent registerForActivityResult
    private val profileResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 1000일 경우 닉네임 수정
            val editProfileData = GlobalFunction.getParcelableExtra(result.data!!, "profileData", ProfileData::class.java)!!    // 수정된 프로필 데이터

            profileData.nickname = editProfileData.nickname
            profileData.introduction = editProfileData.introduction

            binding.userName.text = profileData.nickname
            binding.userIntroduction.text = profileData.introduction
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
        _binding = FragMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){

            userName.text = UserData.getNickName(mainActivity.applicationContext)   // 유저명 SET

            getPosts()  // 작성한 칭찬내역 수 조회

            getBookmarkData()   // 북마크 데이터 수 조회

            getProfileData()    // 프로필 정보 조회

            // 프로필 수정 버튼 클릭 리스너
            btnProfileEdit.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(mainActivity.applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", mainActivity)
                }else{
                    val intent = Intent(mainActivity, MyPageProfileActivity::class.java)
                    intent.putExtra("profileData", profileData)

                    profileResult.launch(intent)
                }
            })

            // 칭찬 내역 버튼 클릭 리스너
            btnComp.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(mainActivity.applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", mainActivity)
                }else{
                    val intent = Intent(mainActivity, MyPageCompActivity::class.java)

                    postsResult.launch(intent)
                }
            })

            // 북마크 버튼 클릭 리스너
            btnBookmark.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(mainActivity.applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", mainActivity)
                }else{
                    val intent = Intent(mainActivity, MyPageBookmarkActivity::class.java)
                    intent.putParcelableArrayListExtra("bookmarkCompList", bookmarkCompList)

                    bookmarkResult.launch(intent)
                }
            })

            // 내활동 버튼 클릭 리스너
            btnNotification.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(mainActivity.applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", mainActivity)
                }else{
                    val intent = Intent(mainActivity, MyPageNotificationActivity::class.java)

                    notificationResult.launch(intent)
                }
            })

            // FAQ 버튼 클릭 리스너
            btnFaq.setOnClickListener(View.OnClickListener {
                val intent = Intent(mainActivity, MyPageFAQActivity::class.java)
                startActivity(intent)
            })

            // 설정 버튼 클릭 리스너
            btnSetting.setOnClickListener(View.OnClickListener {
                val intent = Intent(mainActivity, MyPageSettingActivity::class.java)
                intent.putExtra("email", profileData.email)

                settingResult.launch(intent)
            })


            // 로그아웃 버튼 클릭 리스너
            btnLogout.setOnClickListener(View.OnClickListener {
                logoutDialog.show()
            })


            // 로그아웃 다이얼로그 클릭 리스너
            logoutDialog.setListener(object : MyPageLogoutDialog.OnClickListener{
                override fun onClick(type: String) {

                    logoutDialog.dismiss()

                    if(type == "LOGOUT"){
                        if(UserData.getLoginType(mainActivity.applicationContext) == LoginType.KAKAO.name){ // 카카오 로그아웃
                            UserApiClient.instance.logout { error ->
                                if (error != null) {
                                    customToast.showCustomToast("로그아웃이 실패 했습니다.", mainActivity)
                                }else {
                                    customToast.showCustomToast("정상적으로 로그아웃되었습니다.", mainActivity)

                                    UserData.removeAll(mainActivity.applicationContext)

                                    val intent = Intent(mainActivity, LoginActivity::class.java)
                                    mainActivity.startForResult.launch(intent)
                                }
                            }
                        }else if(UserData.getLoginType(mainActivity.applicationContext) == LoginType.NAVER.name){   // 네이버 로그아웃
                            NaverIdLoginSDK.logout()
                            customToast.showCustomToast("정상적으로 로그아웃되었습니다.", mainActivity)

                            UserData.removeAll(mainActivity.applicationContext)

                            val intent = Intent(mainActivity, LoginActivity::class.java)
                            mainActivity.startForResult.launch(intent)

                        }else if(UserData.getLoginType(mainActivity.applicationContext) == LoginType.GOOGLE.name){  // 구글 로그아웃
                            GoogleSignIn.getClient(mainActivity.applicationContext, InitApplication.getInstance()).signOut()
                            customToast.showCustomToast("정상적으로 로그아웃되었습니다.", mainActivity)

                            UserData.removeAll(mainActivity.applicationContext)

                            val intent = Intent(mainActivity, LoginActivity::class.java)
                            mainActivity.startForResult.launch(intent)
                        }else if(UserData.getLoginType(mainActivity.applicationContext) == LoginType.GUEST.name){  // 게스트 로그아웃
                            customToast.showCustomToast("정상적으로 로그아웃되었습니다.", mainActivity)

                            UserData.removeAll(mainActivity.applicationContext)

                            val intent = Intent(mainActivity, LoginActivity::class.java)
                            mainActivity.startForResult.launch(intent)
                        }
                    }else if(type == "CANCEL"){
                        logoutDialog.dismiss()
                    }
                }
            })

        }
    }

    // 북마크한 게시글 조회
    private fun getBookmarkData(){
        val apiObjectCall = ApiObject.getMyPageService.getBookmarkComp(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext))

        apiObjectCall.enqueue(object: Callback<CompBookmarkResData> {
            override fun onResponse(call: Call<CompBookmarkResData>, response: Response<CompBookmarkResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    bookmarkCompList = response.body()?.data!!

                    binding.bookmarkCount.text = bookmarkCompList.size.toString()
                }else{
                    customToast.showCustomToast("북마크 목록을 불러오지 못했습니다.", mainActivity)
                }
            }

            override fun onFailure(call: Call<CompBookmarkResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 내 프로필 정보 조회
    private fun getProfileData(){
        val apiObjectCall = ApiObject.getMyPageService.getProfile(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext)
            , id = UserData.getUserId(mainActivity.applicationContext))

        apiObjectCall.enqueue(object: Callback<ProfileResData> {
            override fun onResponse(call: Call<ProfileResData>, response: Response<ProfileResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        profileData = response.body()?.data!!

                        with(binding){
                            userName.text = profileData.nickname
                            userIntroduction.text = profileData.introduction

                            followerCount.text = profileData.follower.toString()
                            followingCount.text = profileData.following.toString()

                        }

                    }else{
                        customToast.showCustomToast("프로필 정보 조회를 못했습니다.", mainActivity)
                    }

                }else{
                    customToast.showCustomToast("프로필 정보 조회를 못했습니다.", mainActivity)
                }
            }

            override fun onFailure(call: Call<ProfileResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 작성한 칭찬 데이터 조회
    private fun getPosts() {
        val apiObjectCall = ApiObject.getMyPageService.getPosts(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext)
            , userId = UserData.getUserId(mainActivity.applicationContext)
            , page = 0, size = 1000000)

        apiObjectCall.enqueue(object: Callback<CompResData> {
            override fun onResponse(call: Call<CompResData>, response: Response<CompResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val newCompDataList = response.body()?.data?.content!!

                    binding.compCount.text = newCompDataList.size.toString()
                }else{
                    customToast.showCustomToast("작성한 칭찬 내역을 불러오지 못했습니다.", mainActivity)
                }
            }

            override fun onFailure(call: Call<CompResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}