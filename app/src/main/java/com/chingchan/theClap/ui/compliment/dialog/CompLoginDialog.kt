package com.chingchan.theClap.ui.compliment.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import com.chingchan.theClap.databinding.DlCompLoginBinding


class CompLoginDialog(context: Context) : Dialog(context) {

    interface OnClickListener {
        fun onClick(type: String) {}
    }

    private var clickListener: OnClickListener? = null

    private lateinit var binding: DlCompLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DlCompLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명하게
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Width 꽉차게

        setCanceledOnTouchOutside(false)    // 다이얼로그 외부 화면을 터치할 때 다이얼로그가 종료되지 않도록

        with(binding){

            // 텍스트에 밑줄 추가
            btnCancel.paintFlags = btnCancel.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            // 카카오 로그인 버튼 클릭
            btnKakao.setOnClickListener {
                clickListener?.onClick("KAKAO")
            }

            // 네이버 로그인 버튼 클릭
            btnNaver.setOnClickListener {
                clickListener?.onClick("NAVER")
            }

            // 구글 로그인 버튼 클릭
            btnGoogle.setOnClickListener {
                clickListener?.onClick("GOOGLE")
            }

            // '우선 둘러볼래요' 버튼 클릭
            btnCancel.setOnClickListener {
                clickListener?.onClick("CANCEL")
            }
        }
    }

    // 클릭 리스너 SET
    fun setListener(clickListener: OnClickListener){
        this.clickListener = clickListener
    }
}