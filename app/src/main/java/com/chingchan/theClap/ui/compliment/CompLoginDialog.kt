package com.chingchan.theClap.ui.compliment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.chingchan.theClap.databinding.DlCompLoginBinding
import com.chingchan.theClap.databinding.DlCompWriteUploadImageBinding


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

        with(binding){
            // 카카오 로그인 버튼 클릭
            kakaoBtn.setOnClickListener {
                clickListener?.onClick("KAKAO")
            }

            // 네이버 로그인 버튼 클릭
            naverBtn.setOnClickListener {
                clickListener?.onClick("NAVER")
            }

            // 구글 로그인 버튼 클릭
            googleBtn.setOnClickListener {
                clickListener?.onClick("GOOGLE")
            }
        }


    }
}