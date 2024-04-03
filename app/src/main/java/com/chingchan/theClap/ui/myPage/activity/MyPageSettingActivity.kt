package com.chingchan.theClap.ui.myPage.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiMypageFaqBinding
import com.chingchan.theClap.databinding.ActiMypageProfileBinding
import com.chingchan.theClap.databinding.ActiMypageSettingBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.InitApplication
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.login.activity.LoginActivity
import com.chingchan.theClap.ui.login.data.LoginType
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.adapter.MyPageBookmarkRecyclerAdapter
import com.chingchan.theClap.ui.myPage.adapter.MyPageFAQRecyclerAdapter
import com.chingchan.theClap.ui.myPage.data.FAQData
import com.chingchan.theClap.ui.myPage.data.ReportUserReqData
import com.chingchan.theClap.ui.myPage.data.ReportUserResData
import com.chingchan.theClap.ui.myPage.data.UnlinkUserReqData
import com.chingchan.theClap.ui.myPage.data.UnlinkUserResData
import com.chingchan.theClap.ui.myPage.dialog.MyPageMemberUnlinkDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageProfileImageDialog
import com.chingchan.theClap.ui.toast.customToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.tasks.OnCompleteListener
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActiMypageSettingBinding
    private val memberUnlinkDialog: MyPageMemberUnlinkDialog by lazy { MyPageMemberUnlinkDialog(this) } // 계정 탈퇴 다이얼로그

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMypageSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            userEmail.text = intent.getStringExtra("email") // 이메일 계정

            // 보안 버튼 클릭 리스너
//            btnSecurity.setOnClickListener(View.OnClickListener {
//
//                // 테스트를 위한 인텐트
//                val intent = Intent(this@MyPageSettingActivity, MyPageSecurityActivity::class.java)
//                startActivity(intent)
//
//                Log.e("click!!", "security")
//            })

//            // 알림 버튼 클릭 리스너
//            btnNoti.setOnClickListener(View.OnClickListener {
//
//                // 테스트를 위한 인텐트
//                val intent = Intent(this@MyPageSettingActivity, MyPagePushActivity::class.java)
//                startActivity(intent)
//
//                Log.e("click!!", "notification")
//            })

            // 공지사항 버튼 클릭 리스너
            btnAnnounce.setOnClickListener(View.OnClickListener {
                val intent = Intent(this@MyPageSettingActivity, MyPageAnnounceActivity::class.java)
                startActivity(intent)
            })

            // 고객센터/운영정책 버튼 클릭 리스너
            btnHelp.setOnClickListener(View.OnClickListener {

                // 테스트를 위한 인텐트
                val intent = Intent(this@MyPageSettingActivity, MyPageHelpActivity::class.java)
                startActivity(intent)
            })

            // 계정 탈퇴 버튼 클릭 리스너
            btnUnlink.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", this@MyPageSettingActivity)
                }else{
                    memberUnlinkDialog.show()
                }
            })

            // 계정 탈퇴 다이얼로그 클릭 리스너
            memberUnlinkDialog.setListener(object : MyPageMemberUnlinkDialog.OnClickListener {
                override fun onClick(type: String, reportType: String, reportReason: String) {
                    if(type == "UNLINK"){
                        memberUnlinkDialog.dismiss()

                        if(UserData.getLoginType(applicationContext) == LoginType.KAKAO.name){ // 카카오 계정 탈퇴
                            UserApiClient.instance.unlink { error ->
                                if (error != null) {
                                    customToast.showCustomToast("계정 탈퇴가 실패 했습니다.", this@MyPageSettingActivity)
                                }else {
                                    customToast.showCustomToast("정상적으로 탈퇴 처리되었습니다.", this@MyPageSettingActivity)

                                    unlinkUser(UnlinkUserReqData(reportType, reportReason))  // 계정 탈퇴
                                }
                            }
                        }else if(UserData.getLoginType(applicationContext) == LoginType.NAVER.name){   // 네이버 계정 탈퇴

                            NidOAuthLogin().callDeleteTokenApi(applicationContext, object : OAuthLoginCallback {
                                override fun onSuccess() {
                                    customToast.showCustomToast("정상적으로 탈퇴 처리되었습니다.", this@MyPageSettingActivity)

                                    unlinkUser(UnlinkUserReqData(reportType, reportReason))  // 계정 탈퇴
                                }
                                override fun onFailure(httpStatus: Int, message: String) {
                                    // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                                    // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                                    customToast.showCustomToast("정상적으로 탈퇴 처리되었습니다.", this@MyPageSettingActivity)

                                    unlinkUser(UnlinkUserReqData(reportType, reportReason))  // 계정 탈퇴
                                }
                                override fun onError(errorCode: Int, message: String) {
                                    // 서버에서 토큰 삭제에 실패했어도 클라이언트에 있는 토큰은 삭제되어 로그아웃된 상태입니다.
                                    // 클라이언트에 토큰 정보가 없기 때문에 추가로 처리할 수 있는 작업은 없습니다.
                                    onFailure(errorCode, message)
                                }
                            })
                        }else if(UserData.getLoginType(applicationContext) == LoginType.GOOGLE.name){  // 구글 계정 탈퇴
                            GoogleSignIn.getClient(applicationContext, InitApplication.getInstance()).revokeAccess()
                                .addOnCompleteListener(this@MyPageSettingActivity){
                                    customToast.showCustomToast("정상적으로 탈퇴 처리되었습니다.", this@MyPageSettingActivity)

                                    unlinkUser(UnlinkUserReqData(reportType, reportReason))  // 계정 탈퇴
                                }
                        }
                    }else if(type == "CANCEL"){
                        memberUnlinkDialog.dismiss()
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
    private fun unlinkUser(unlinkUserData: UnlinkUserReqData){
        val apiObjectCall = ApiObject.getMyPageService.unlinkUser(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , unlinkUserData)

        apiObjectCall.enqueue(object: Callback<UnlinkUserResData> {
            override fun onResponse(call: Call<UnlinkUserResData>, response: Response<UnlinkUserResData>) {

                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        customToast.showCustomToast("정상적으로 탈퇴 처리되었습니다.", this@MyPageSettingActivity)

                        // 서버에서 토큰 삭제에 성공한 상태입니다.
                        UserData.removeAll(applicationContext)

                        setResult(1000, intent) // 1000번 코드 -> 계정 탈퇴
                        finish()
                    }

                }else{
                    customToast.showCustomToast("정상적으로 탈퇴 처리 되지 못했습니다.", this@MyPageSettingActivity)
                }
            }

            override fun onFailure(call: Call<UnlinkUserResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageSettingActivity)
            }
        })
    }
}