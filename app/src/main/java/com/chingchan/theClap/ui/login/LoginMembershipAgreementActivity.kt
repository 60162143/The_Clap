package com.chingchan.theClap.ui.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.BuildConfig
import com.chingchan.theClap.databinding.ActiLoginMemberAgreeBinding
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.login.data.LoginResult
import com.chingchan.theClap.ui.login.data.MembershipUserData
import java.io.Serializable

class LoginMembershipAgreementActivity  : AppCompatActivity() {
    private lateinit var binding: ActiLoginMemberAgreeBinding
    private lateinit var membershipUserData: MembershipUserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 로그인 데이터 받기
        membershipUserData = GlobalFunction.getParcelableExtra(intent, "membershipUserData", MembershipUserData::class.java)!!

        binding = ActiLoginMemberAgreeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == LoginResult.SUCCESS.code) {
                setResult(LoginResult.SUCCESS.code, intent)
                finish()
            }else if (result.resultCode == LoginResult.CANCEL.code) {
                setResult(LoginResult.CANCEL.code, intent)
                finish()
            }else if (result.resultCode == LoginResult.BACK.code) {

            }
        }

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){

            // 뒤로 가기 버튼
            btnBack.setOnClickListener(View.OnClickListener {
                setResult(LoginResult.BACK.code, intent)
                finish()
            })

            // 닫기 버튼
            btnClose.setOnClickListener(View.OnClickListener {
                setResult(LoginResult.CANCEL.code, intent)
                finish()
            })


            // 전체 동의
            chkTotal.setOnClickListener(View.OnClickListener {
                chkTotal.isSelected = !chkTotal.isSelected

                chkAge.isSelected = chkTotal.isSelected
                chkUtilization.isSelected = chkTotal.isSelected
                chkPersonal.isSelected = chkTotal.isSelected
                chkMarketing.isSelected = chkTotal.isSelected

                confirmBtnEnable()  // 확인 버튼 비/활성화
            })

            // 만 14세 이상 동의
            chkAge.setOnClickListener(View.OnClickListener {
                chkAge.isSelected = !chkAge.isSelected
                confirmBtnEnable()  // 확인 버튼 비/활성화
            })

            // 이용약관 동의
            chkUtilization.setOnClickListener(View.OnClickListener {
                chkUtilization.isSelected = !chkUtilization.isSelected
                confirmBtnEnable()  // 확인 버튼 비/활성화
            })

            // 개인정보수집 동의
            chkPersonal.setOnClickListener(View.OnClickListener {
                chkPersonal.isSelected = !chkPersonal.isSelected
                confirmBtnEnable()  // 확인 버튼 비/활성화
            })

            // 마케팅수신 동의
            chkMarketing.setOnClickListener(View.OnClickListener {
                chkMarketing.isSelected = !chkMarketing.isSelected
            })

            // 이용약관 보기 버튼
            btnUtilizationDetail.setOnClickListener(View.OnClickListener {
                // 이용약관 WebView Activity로 화면 전환
                val intent = Intent(this@LoginMembershipAgreementActivity, LoginWebViewActivity::class.java)
                intent.putExtra("url", BuildConfig.LOGIN_AGREEMENT_URL)
                startActivity(intent)
            })

            // 개인정보수집 보기 버튼
            btnPersonalDetail.setOnClickListener(View.OnClickListener {
                // 이용약관 WebView Activity로 화면 전환
                val intent = Intent(this@LoginMembershipAgreementActivity, LoginWebViewActivity::class.java)
                intent.putExtra("url", BuildConfig.LOGIN_POLICY_URL)
                startActivity(intent)
            })

            // 마케팅수신 보기 버튼
            btnMarketingDetail.setOnClickListener(View.OnClickListener {

            })

            // 확인 버튼
            btnConfirm.setOnClickListener(View.OnClickListener {

                // 동의 한 내용 저장 로직 구현 해야 함


                val intent = Intent(this@LoginMembershipAgreementActivity, LoginMembershipAuthorityActivity::class.java).apply {
                    putExtra("membershipUserData", membershipUserData);
                }
                startForResult.launch(intent)

            })
        }
    }

    private fun confirmBtnEnable() {
        with(binding){
            btnConfirm.isEnabled = (chkAge.isSelected and chkUtilization.isSelected and chkPersonal.isSelected)
        }
    }

    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 뒤로 버튼 이벤트 처리
            setResult(LoginResult.BACK.code, intent)
            finish()
        }
    }
}