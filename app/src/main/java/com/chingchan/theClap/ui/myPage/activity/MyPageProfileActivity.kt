package com.chingchan.theClap.ui.myPage.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.ActiMypageProfileBinding
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.data.FAQData
import com.chingchan.theClap.ui.myPage.data.FAQResData
import com.chingchan.theClap.ui.myPage.data.NicknameDupReqData
import com.chingchan.theClap.ui.myPage.data.NicknameDupResData
import com.chingchan.theClap.ui.myPage.data.ProfileData
import com.chingchan.theClap.ui.myPage.data.ProfileReqData
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.myPage.dialog.MyPageProfileImageDialog
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActiMypageProfileBinding

    private val profileImageDialog: MyPageProfileImageDialog by lazy { MyPageProfileImageDialog(this) } // 프로필 이미지 수정 다이얼로그

    private lateinit var profileData: ProfileData   // 프로필 데이터

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMypageProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        profileData = GlobalFunction.getParcelableExtra(intent, "profileData", ProfileData::class.java)!!    // 프로필 데이터

        with(binding){

            // 데이터 SET
            editName.setText(profileData.nickname)
            editIntro.setText(profileData.introduction)


            // 프로필 이미지 변경 버튼 클릭 리스너
            btnProfileImage.setOnClickListener(View.OnClickListener {
                profileImageDialog.show()
            })

            // 닉네임 입력 칸 입력 텍스트 변화 확인 리스너
            editName.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {

                    // 중복 확인 초기화
                    editName.setBackgroundResource(R.drawable.border_round_gray3_14dp)
                    textNameExplain.visibility = View.VISIBLE
                    textNameEnable.visibility = View.GONE
                    btnConfirm.isEnabled = true

                    // 기존 닉네임과 달라져야 변경 가능
                    btnNameDup.isEnabled = editName.text.isNotEmpty()
                            && profileData.nickname != editName.text.toString()
                            && isValidNickname(editName.text.toString())

                    return
                }
            })

            // 중복 확인 버튼 클릭 리스너
            btnNameDup.setOnClickListener(View.OnClickListener {
                checkDuplicate(NicknameDupReqData(nickname = editName.text.toString()))    // 중복 확인

                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            })

            // 수정하기 버튼 클릭 리스너
            btnConfirm.setOnClickListener(View.OnClickListener {
                if(binding.editIntro.text.isEmpty()){
                    customToast.showCustomToast("소개글을 작성해주세요.", this@MyPageProfileActivity)
                }else if(binding.editName.text.isEmpty()){
                    customToast.showCustomToast("닉네임을 입력해주세요.", this@MyPageProfileActivity)
                }else if(binding.btnNameDup.isEnabled){
                    customToast.showCustomToast("중복확인을 진행해주세요.", this@MyPageProfileActivity)
                }else{
                    editProfileData()   // 프로필 수정
                }
            })

            // 프로필 이미지 수정 다이얼로그 클릭 리스너
            profileImageDialog.setListener(object : MyPageProfileImageDialog.OnClickListener {
                override fun onClick(type: String) {
                    if(type == "CAMERA"){
                        Log.e("profileImageDialog", "CAMERA")
                    }else if(type == "STORAGE"){
                        Log.e("profileImageDialog", "STORAGE")
                    }else if(type == "DELETE"){
                        Log.e("profileImageDialog", "DELETE")
                    }
                }
            })

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                finish()
            })
        }
    }

    override fun onResume() {
        super.onResume()
    }

    // 닉네임 유효성 체크
    private fun isValidNickname(nickname: String?): Boolean {
        val trimmedNickname = nickname?.trim().toString()
        val exp = Regex("^[가-힣a-zA-Z0-9._-]{2,}\$")
        return trimmedNickname.isNotEmpty() && exp.matches(trimmedNickname)
    }

    // 닉네임 중복 체크
    private fun checkDuplicate(nicknameDupReqData: NicknameDupReqData){
        val apiObjectCall = ApiObject.getMyPageService.isNicknameDup(authorization = "Bearer " + UserData.getAccessToken(applicationContext),
            req = nicknameDupReqData)

        apiObjectCall.enqueue(object: Callback<NicknameDupResData> {
            override fun onResponse(call: Call<NicknameDupResData>, response: Response<NicknameDupResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val nicknameDupData = response.body()?.data!!

                        if(nicknameDupData.checkResult){
                            // 중복확인 성공 시
                            binding.btnConfirm.isEnabled = true
                            binding.textNameExplain.visibility = View.GONE
                            binding.textNameEnable.visibility = View.VISIBLE
                            binding.textNameEnable.isEnabled = true
                            binding.textNameEnable.text = "사용 가능한 닉네임입니다."
                            binding.btnNameDup.isEnabled = false
                        }else{
                            // 중복확인 실패 시
                            binding.btnConfirm.isEnabled = false
                            binding.textNameExplain.visibility = View.GONE
                            binding.textNameEnable.visibility = View.VISIBLE
                            binding.textNameEnable.isEnabled = false
                            binding.textNameEnable.text = "사용중인 닉네임입니다."
                            binding.editName.setBackgroundResource(R.drawable.border_round_error_14dp)
                        }

                    }else{
                        customToast.showCustomToast("닉네임 중복 정보 조회를 못했습니다.", this@MyPageProfileActivity)
                    }

                }else{
                    customToast.showCustomToast("닉네임 중복 정보 조회를 못했습니다.", this@MyPageProfileActivity)
                }
            }

            override fun onFailure(call: Call<NicknameDupResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageProfileActivity)
            }
        })
    }

    // 내 프로필 수정
    private fun editProfileData(){
        val apiObjectCall = ApiObject.getMyPageService.editProfile(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
        , ProfileReqData(nickname = binding.editName.text.toString(), introduction = binding.editIntro.text.toString()))

        apiObjectCall.enqueue(object: Callback<ProfileResData> {
            override fun onResponse(call: Call<ProfileResData>, response: Response<ProfileResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        customToast.showCustomToast("프로필이 수정되었습니다.", this@MyPageProfileActivity)

                        UserData.setNickName(applicationContext, response.body()!!.data.nickname)
                        intent.putExtra("profileData", response.body()!!.data)

                        setResult(1000, intent)
                        finish()

                    }else{
                        customToast.showCustomToast("프로필 수정을 못했습니다.", this@MyPageProfileActivity)
                    }

                }else{
                    customToast.showCustomToast("프로필 수정을 못했습니다.", this@MyPageProfileActivity)
                }
            }

            override fun onFailure(call: Call<ProfileResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@MyPageProfileActivity)
            }
        })
    }

    // 화면 터치시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }
}