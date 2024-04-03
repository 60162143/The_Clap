package com.chingchan.theClap.ui.myPage.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.chingchan.theClap.databinding.DlCompWriteUploadImageBinding
import com.chingchan.theClap.databinding.DlMypageOtherUserReportBinding
import com.chingchan.theClap.databinding.DlMypageRecCompBlockBinding
import com.chingchan.theClap.databinding.DlMypageRecCompEditBinding
import com.chingchan.theClap.databinding.DlMypageRecCompHideBinding


class MyPageOtherUserReportDialog(context: Context, var userName: String) : Dialog(context) {

    interface OnClickListener {
        fun onClick(type: String) {}
    }

    private var clickListener: OnClickListener? = null

    private lateinit var binding: DlMypageOtherUserReportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DlMypageOtherUserReportBinding.inflate(layoutInflater)
        setContentView(binding.root)


        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명하게
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Width 꽉차게

        setCanceledOnTouchOutside(false)    // 다이얼로그 외부 화면을 터치할 때 다이얼로그가 종료되지 않도록

        with(binding){

            // 타 유저 이름 SET
            textUserName.text = userName + "님을 정말 신고하시겠습니까?"


            // 텍스트에 밑줄 추가
            btnCancel.paintFlags = btnCancel.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            // 신고하기 버튼 클릭
            btnReport.setOnClickListener {
                clickListener?.onClick("REPORT")
            }

            // 취소 버튼 클릭
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