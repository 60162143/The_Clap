package com.chingchan.theClap.ui.compliment.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.databinding.ActiCompCmtDetailBinding
import com.chingchan.theClap.databinding.ActiCompCmtEditBinding
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.adapter.CompCmtDetailRecyclerAdapter
import com.chingchan.theClap.ui.compliment.data.CompCmtData
import com.chingchan.theClap.ui.compliment.data.CompCmtResData
import com.chingchan.theClap.ui.compliment.data.CompCmtWriteReqData
import com.chingchan.theClap.ui.compliment.data.CompCmtWriteResData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompDetResData
import com.chingchan.theClap.ui.compliment.dialog.CompCmtEditDialog
import com.chingchan.theClap.ui.compliment.dialog.CompCmtEditOtherDialog
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.toast.customToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplimentCommentEditActivity : AppCompatActivity() {
    private lateinit var binding: ActiCompCmtEditBinding

    private lateinit var compCmtData: CompCmtData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiCompCmtEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compCmtData = GlobalFunction.getParcelableExtra(intent, "compCmtData", CompCmtData::class.java)!!    // 댓글 데이터

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){

            cmtContent.setText(compCmtData.content) // 댓글 내용 SET

            // 등록 버튼 클릭 리스너
            btnSave.setOnClickListener(View.OnClickListener {
                if(cmtContent.text.isNotEmpty()){
                    editCompCmt(cmtContent.text.toString())
                }else{
                    customToast.showCustomToast("댓글을 입력해 주세요.", this@ComplimentCommentEditActivity)
                }
            })

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                setResult(1000, intent)
                finish()
            })
        }
    }

    override fun onResume() {
        super.onResume()

    }

    // 게시글 댓글 수정
    private fun editCompCmt(editContent: String){
        val apiObjectCall = ApiObject.getCompService.editCompCmt(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = compCmtData.boardId, commentId = compCmtData.commentId, CompCmtWriteReqData(content = editContent))

        apiObjectCall.enqueue(object: Callback<CompCmtWriteResData> {
            override fun onResponse(call: Call<CompCmtWriteResData>, response: Response<CompCmtWriteResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!

                    if(status == "success"){
                        customToast.showCustomToast("댓글이 수정되었습니다.", this@ComplimentCommentEditActivity)

                        setResult(1000, intent)
                        finish()

                    }else{
                        customToast.showCustomToast("댓글을 수정하지 못했습니다.", this@ComplimentCommentEditActivity)
                    }

                }else{
                    customToast.showCustomToast("댓글을 수정하지 못했습니다.", this@ComplimentCommentEditActivity)
                }
            }

            override fun onFailure(call: Call<CompCmtWriteResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentCommentEditActivity)
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

    // 화면 터치시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }
}