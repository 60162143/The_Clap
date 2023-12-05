package com.chingchan.theClap.ui.login

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiLoginMemberBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.login.data.LoginResult
import com.chingchan.theClap.ui.login.data.LoginUserResData
import com.chingchan.theClap.ui.login.data.MembershipUserData
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginMembershipActivity  : AppCompatActivity() {
    private lateinit var binding: ActiLoginMemberBinding
    private lateinit var membershipUserData: MembershipUserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiLoginMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 데이터 받기
        membershipUserData = GlobalFunction.getParcelableExtra(intent, "membershipUserData", MembershipUserData::class.java)!!

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

            // '정해주세요' 버튼 클릭 리스너
            btnRandomName.setOnClickListener(View.OnClickListener {
                editName.setText(getRandomString(6))

                btnNameChg.visibility = View.VISIBLE
                textNameExplain.visibility = View.GONE
                textNameEnable.visibility = View.GONE
                editName.inputType = InputType.TYPE_NULL
            })

            // '랜덤닉네임이에요. 원하는 닉네임을 정하시겠어요?' 버튼 클릭 리스너
            btnNameChg.setOnClickListener(View.OnClickListener {
                editName.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                btnNameChg.visibility = View.GONE
                textNameExplain.visibility = View.VISIBLE
            })

            // 닉네임 입력 칸 입력 텍스트 변화 확인 리스너
            editName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {

                    // 중복 확인 초기화
                    editName.setBackgroundResource(R.drawable.border_round_gray3_14dp)
                    btnNameChg.visibility = View.GONE

                    btnConfirm.isEnabled = isValidNickname(editName.text.toString())    // 유효성 체크

                    if(btnConfirm.isEnabled){
                        textNameExplain.visibility = View.GONE
                        textNameEnable.visibility = View.VISIBLE

                        textNameEnable.isEnabled = true
                        textNameEnable.text = "사용 가능한 닉네임입니다."
                    }else{
                        textNameExplain.visibility = View.VISIBLE
                        textNameEnable.visibility = View.GONE
                    }

                    return
                }
            })

            // 확인 버튼 클릭 리스너
            btnConfirm.setOnClickListener(View.OnClickListener {
                // 닉네임 등록
                membershipUserData.nickname = editName.text.toString()

                signUp(membershipUserData)

            })
        }
    }

    // 랜덤 닉네임 생성
    fun getRandomString(length: Int) : String {
        val charset = ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    // 닉네임 유효성 체크
    private fun isValidNickname(nickname: String?): Boolean {
        val trimmedNickname = nickname?.trim().toString()
        val exp = Regex("^[가-힣a-zA-Z0-9._-]{2,}\$")
        return trimmedNickname.isNotEmpty() && exp.matches(trimmedNickname)
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 뒤로 버튼 이벤트 처리
            setResult(LoginResult.BACK.code, intent)
            finish()
        }
    }

    // 회원 가입
    private fun signUp(userData: MembershipUserData) {
        val apiObjectCall = ApiObject.getLoginService.getSignUp(userData)

        apiObjectCall.enqueue(object: Callback<LoginUserResData> {
            override fun onResponse(call: Call<LoginUserResData>, response: Response<LoginUserResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    customToast.showCustomToast("회원가입에 성공했습니다.", this@LoginMembershipActivity)

                    setResult(LoginResult.SUCCESS.code, intent)
                    finish()

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<LoginUserResData>(
                        LoginUserResData::class.java,
                        LoginUserResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00005.name){    // 닉네임 중복
                            with(binding){
                                textNameExplain.visibility = View.GONE
                                textNameEnable.visibility = View.VISIBLE
                                editName.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                                btnNameChg.visibility = View.GONE

                                textNameEnable.isEnabled = false
                                textNameEnable.text = "사용중인 닉네임입니다."
                                editName.setBackgroundResource(R.drawable.border_round_error_14dp)

                                btnConfirm.isEnabled = false
                            }
                        }else{
                            customToast.showCustomToast("회원가입에 실패했습니다.", this@LoginMembershipActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<LoginUserResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@LoginMembershipActivity)
            }
        })
    }
}