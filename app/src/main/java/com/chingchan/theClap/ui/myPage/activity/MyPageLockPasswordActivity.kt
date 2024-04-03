package com.chingchan.theClap.ui.myPage.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiMypageLockPasswordBinding
import com.chingchan.theClap.ui.myPage.data.AppLock
import com.chingchan.theClap.ui.myPage.data.AppLockConst


class MyPageLockPasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActiMypageLockPasswordBinding

    private var oldPwd = ""
    private var changePwdUnlock = false

    private var inputPwd = Array<String>(4) { "" }

    private var intentType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMypageLockPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 잠금 타입
        intentType = intent.getIntExtra("type", 0)

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){
            val buttonArray = arrayListOf<Button>(btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7 ,btn8, btn9, btnClear)
            for (button in buttonArray){
                button.setOnClickListener(btnListener)
            }

            // 입력한 비밀번호 1개 제거
            btnErase.setOnClickListener(View.OnClickListener {
                onDeleteKey()
            })

            // 초기 멘트 설정 및 레이아웃 설정
            when(intentType){
                AppLockConst.ENABLE_PASSLOCK ->{ // 잠금 설정
                    textInfoDetail.text = "새로운 비밀번호를 입력해 주세요."
                }

                AppLockConst.DISABLE_PASSLOCK ->{ // 잠금 삭제
                    textInfoDetail.text = "사용중인 비밀번호를 입력해 주세요."
                }

                AppLockConst.UNLOCK_PASSWORD -> {   // 잠금 해제
                    textInfoDetail.text = "사용중인 비밀번호를 입력해 주세요."
                    layoutTitle.visibility = View.INVISIBLE
                }

                AppLockConst.CHANGE_PASSWORD -> { // 비밀번호 변경
                    textInfoDetail.text = "사용중인 비밀번호를 입력해 주세요."
                }
            }
        }
    }

    // 버튼 클릭 했을때
    private val btnListener = View.OnClickListener { view ->
        with(binding) {
            var currentValue = -1
            when (view.id) {
                R.id.btn0 -> currentValue = 0
                R.id.btn1 -> currentValue = 1
                R.id.btn2 -> currentValue = 2
                R.id.btn3 -> currentValue = 3
                R.id.btn4 -> currentValue = 4
                R.id.btn5 -> currentValue = 5
                R.id.btn6 -> currentValue = 6
                R.id.btn7 -> currentValue = 7
                R.id.btn8 -> currentValue = 8
                R.id.btn9 -> currentValue = 9
                R.id.btn_clear -> onClear()
            }

            val strCurrentValue = currentValue.toString() // 현재 입력된 번호 String으로 변경
            if (currentValue != -1) {

                when {
                    editPass1.isFocused -> {
                        setWrongLayout(false)

                        setEditText(editPass1, editPass2, strCurrentValue, 0)
                    }

                    editPass2.isFocused -> {
                        setEditText(editPass2, editPass3, strCurrentValue, 1)
                    }

                    editPass3.isFocused -> {
                        setEditText(editPass3, editPass4, strCurrentValue, 2)
                    }

                    editPass4.isFocused -> {
                        inputPwd[3] = strCurrentValue
                        editPass4.setBackgroundResource(R.drawable.round_gray1_center_dot_10dp)
                    }
                }
            }

            // 비밀번호를 4자리 모두 입력시
            if (inputPwd[3].isNotEmpty() && inputPwd[2].isNotEmpty() && inputPwd[1].isNotEmpty() && inputPwd[0].isNotEmpty()) {
                inputType(intent.getIntExtra("type", 0))
            }
        }
    }

    // 한 칸 지우기를 눌렀을때
    private fun onDeleteKey() {
        with(binding){
            when {
                editPass1.isFocused -> {
                    inputPwd[0] = ""
                }
                editPass2.isFocused -> {
                    inputPwd[1] = ""
                    editPass1.setBackgroundResource(R.drawable.round_gray1_10dp)
                    editPass1.requestFocus()
                }
                editPass3.isFocused -> {
                    inputPwd[2] = ""
                    editPass2.setBackgroundResource(R.drawable.round_gray1_10dp)
                    editPass2.requestFocus()
                }
                editPass4.isFocused -> {
                    inputPwd[3] = ""
                    editPass3.setBackgroundResource(R.drawable.round_gray1_10dp)
                    editPass3.requestFocus()
                }

                else -> {}
            }
        }
    }

    // 모두 지우기
    private fun onClear(){
        with(binding){
            editPass1.setBackgroundResource(R.drawable.round_gray1_10dp)
            editPass2.setBackgroundResource(R.drawable.round_gray1_10dp)
            editPass3.setBackgroundResource(R.drawable.round_gray1_10dp)
            editPass4.setBackgroundResource(R.drawable.round_gray1_10dp)

            for(i in 0..3){
                inputPwd[i] = ""
            }

            editPass1.requestFocus()
        }
    }

    // 입력된 비밀번호를 합치기
    private fun inputedPassword():String {

        Log.e("password", "${inputPwd[0]}${inputPwd[1]}${inputPwd[2]}${inputPwd[3]}")
        return "${inputPwd[0]}${inputPwd[1]}${inputPwd[2]}${inputPwd[3]}"
    }

    // EditText 설정
    private fun setEditText(currentEditText : TextView, nextEditText: TextView, strCurrentValue : String, idx: Int){
//        currentEditText.text = strCurrentValue
        inputPwd[idx] = strCurrentValue
        currentEditText.setBackgroundResource(R.drawable.round_gray1_center_dot_10dp)
        nextEditText.requestFocus()
    }

    // Intent Type 분류
    private fun inputType(type : Int){
        with(binding){
            when(type){
                AppLockConst.ENABLE_PASSLOCK ->{ // 잠금 설정
                    if(oldPwd.isEmpty()){
                        oldPwd = inputedPassword()
                        onClear()
                        textInfoTitle.text = "비밀번호 재입력"
                        textInfoDetail.text = "사용하실 비밀번호를 한번 더 입력해주세요."
                    }else{
                        if(oldPwd == inputedPassword()){
                            AppLock(applicationContext).setPassLock(inputedPassword())
                            setResult(Activity.RESULT_OK)
                            finish()
                        }else{
                            onClear()
                            setWrongLayout(true)
                        }
                    }
                }

                AppLockConst.DISABLE_PASSLOCK ->{ // 잠금 삭제
                    if(AppLock(applicationContext).isPassLockSet()){
                        if(AppLock(applicationContext).checkPassLock(inputedPassword())) {
                            AppLock(applicationContext).removePassLock()
                            setResult(Activity.RESULT_OK)
                            finish()
                        }else {
                            onClear()
                            setWrongLayout(true)
                        }
                    }else{
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                }

                AppLockConst.UNLOCK_PASSWORD -> // 잠금 해제
                    if(AppLock(applicationContext).checkPassLock(inputedPassword())) {
                        AppLock(applicationContext).setDoingPassLock(false)  // 잠금 진행중 여부 해제
                        setResult(Activity.RESULT_OK)
                        finish()
                    }else{
                        onClear()
                        setWrongLayout(true)
                    }

                AppLockConst.CHANGE_PASSWORD -> { // 비밀번호 변경
                    if (AppLock(applicationContext).checkPassLock(inputedPassword()) && !changePwdUnlock) {
                        onClear()
                        changePwdUnlock = true
                        textInfoTitle.text = "새로운 비밀번호 입력"
                        textInfoDetail.text = "새로운 비밀번호를 입력해주세요."
                    }else if (changePwdUnlock) {
                        if (oldPwd.isEmpty()) {
                            oldPwd = inputedPassword()
                            onClear()
                            textInfoTitle.text = "새로운 비밀번호 재입력"
                            textInfoDetail.text = "새로운 비밀번호를 한번 더 입력해주세요."
                        }else {
                            if (oldPwd == inputedPassword()) {
                                AppLock(applicationContext).setPassLock(inputedPassword())
                                setResult(Activity.RESULT_OK)
                                finish()
                            }else {
                                onClear()
                                setWrongLayout(true)
                            }
                        }
                    }else {
                        onClear()
                        setWrongLayout(true)
                    }
                }
            }
        }
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if(intentType == AppLockConst.UNLOCK_PASSWORD){
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(homeIntent)
                finish()
            }else{
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    private fun setWrongLayout(isWrong: Boolean){
        with(binding){
            if(isWrong){    // 비밀번호 입력이 틀렸을 경우
                editPass1.setBackgroundResource(R.drawable.round_gray1_border_error_10dp)
                editPass2.setBackgroundResource(R.drawable.round_gray1_border_error_10dp)
                editPass3.setBackgroundResource(R.drawable.round_gray1_border_error_10dp)
                editPass4.setBackgroundResource(R.drawable.round_gray1_border_error_10dp)

                textInfoDetail.visibility = View.GONE
                textInfoDetailWrong.visibility = View.VISIBLE
            }else{
                editPass1.setBackgroundResource(R.drawable.round_gray1_10dp)
                editPass2.setBackgroundResource(R.drawable.round_gray1_10dp)
                editPass3.setBackgroundResource(R.drawable.round_gray1_10dp)
                editPass4.setBackgroundResource(R.drawable.round_gray1_10dp)

                textInfoDetail.visibility = View.VISIBLE
                textInfoDetailWrong.visibility = View.GONE
            }

        }
    }
}