package com.chingchan.theClap.ui.myPage.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiMypageFaqBinding
import com.chingchan.theClap.databinding.ActiMypageProfileBinding
import com.chingchan.theClap.databinding.ActiMypageSecurityBinding
import com.chingchan.theClap.databinding.ActiMypageSettingBinding
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.login.data.LoginResult
import com.chingchan.theClap.ui.myPage.adapter.MyPageBookmarkRecyclerAdapter
import com.chingchan.theClap.ui.myPage.adapter.MyPageFAQRecyclerAdapter
import com.chingchan.theClap.ui.myPage.data.AppLock
import com.chingchan.theClap.ui.myPage.data.AppLockConst
import com.chingchan.theClap.ui.myPage.data.FAQData
import com.chingchan.theClap.ui.myPage.dialog.MyPageProfileImageDialog
import com.chingchan.theClap.ui.toast.customToast

class MyPageSecurityActivity : AppCompatActivity() {
    private lateinit var binding: ActiMypageSecurityBinding

    private var isLock = false

    private var isInProgressIntent = false  // 화면전환이 진행중인지에 대한 Flag, 스위치 변화를 구분하기 위한 변수


    // 잠금 비밀번호 설정 Intent registerForActivityResult
    private val enablePassLockResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            customToast.showCustomToast("비밀번호가 성공적으로 설정되었습니다.", this@MyPageSecurityActivity)
        }else{
            customToast.showCustomToast("비밀번호 설정이 취소되었습니다.", this@MyPageSecurityActivity)

            setLayout(false)    // 잠금 여부에 따른 스위치, 버튼 레이아웃 상태 변경
        }

        isInProgressIntent = false
    }

    // 잠금 비밀번호 삭제 Intent registerForActivityResult
    private val disablePassLockResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            customToast.showCustomToast("비밀번호가 성공적으로 삭제되었습니다.", this@MyPageSecurityActivity)
        }else{
            customToast.showCustomToast("비밀번호 삭제가 취소되었습니다.", this@MyPageSecurityActivity)

            setLayout(true)    // 잠금 여부에 따른 스위치, 버튼 레이아웃 상태 변경
        }

        isInProgressIntent = false
    }

    // 잠금 비밀번호 변경 Intent registerForActivityResult
    private val changePasswordResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            customToast.showCustomToast("비밀번호가 성공적으로 변경되었습니다.", this@MyPageSecurityActivity)
        }else{
            customToast.showCustomToast("비밀번호 변경이 취소되었습니다.", this@MyPageSecurityActivity)

            setLayout(true)    // 잠금 여부에 따른 스위치, 버튼 레이아웃 상태 변경
        }

        isInProgressIntent = false
    }

    // 잠금 비밀번호 해제 Intent registerForActivityResult
    private val unLockPasswordResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if(result.resultCode == Activity.RESULT_OK){
            customToast.showCustomToast("잠금이 성공적으로 해제되었습니다.", this@MyPageSecurityActivity)
        }else{
            customToast.showCustomToast("비밀번호 해제가 취소되었습니다.", this@MyPageSecurityActivity)
        }

        isInProgressIntent = false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMypageSecurityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){

            // 잠금 설정 여부에 따른 스위치, 버튼 상태 설정
            isLock = AppLock(applicationContext).isPassLockSet()
            setLayout(isLock)   // 잠금 여부에 따른 스위치, 버튼 레이아웃 상태 변경


            // 화면 잠금 스위치 리스너
            switchLock.setOnCheckedChangeListener { buttonView, isChecked ->
                btnPassword.isEnabled = isChecked

                if(!isInProgressIntent){    // 화면전환으로 변경된 스위치인지 사용자가 누른 스위치인지 구분
                    if (isChecked) {
                        isInProgressIntent = true
                        val intent = Intent(this@MyPageSecurityActivity, MyPageLockPasswordActivity::class.java).apply {
                            putExtra(AppLockConst.type, AppLockConst.ENABLE_PASSLOCK)
                        }
                        enablePassLockResult.launch(intent)
                    }else{
                        isInProgressIntent = true
                        val intent = Intent(this@MyPageSecurityActivity, MyPageLockPasswordActivity::class.java).apply {
                            putExtra(AppLockConst.type, AppLockConst.DISABLE_PASSLOCK)
                        }
                        disablePassLockResult.launch(intent)
                    }
                }
            }

            // 비밀번호 관리 버튼 클릭 리스너
            btnPassword.setOnClickListener(View.OnClickListener {
                val intent = Intent(this@MyPageSecurityActivity, MyPageLockPasswordActivity::class.java).apply {
                    putExtra(AppLockConst.type, AppLockConst.CHANGE_PASSWORD)
                }
                changePasswordResult.launch(intent)
            })
        }
    }


    // 잠금 여부에 따른 스위치, 버튼 레이아웃 상태 변경
    fun setLayout(tf: Boolean){
        with(binding){
            switchLock.isChecked = tf
            btnPassword.isEnabled = tf
        }
    }
}