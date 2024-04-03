package com.chingchan.theClap.ui.home.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ViewGroup
import com.chingchan.theClap.databinding.DlCompBlockBinding
import com.chingchan.theClap.databinding.DlCompNewWriteBinding


class CompRankingNewWriteDialog(context: Context) : Dialog(context) {
    interface OnClickListener {
        fun onClick(type: String) {}
    }

    private var clickListener: OnClickListener? = null

    private lateinit var binding: DlCompNewWriteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DlCompNewWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명하게
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Width 꽉차게

        setCanceledOnTouchOutside(false)    // 다이얼로그 외부 화면을 터치할 때 다이얼로그가 종료되지 않도록

        with(binding){

            // 텍스트에 밑줄 추가
            btnCancel.paintFlags = btnCancel.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            // 지인칭찬 버튼 클릭
            btnFriend.setOnClickListener {
                clickListener?.onClick("FRIEND")
            }

//            // 셀프칭찬 버튼 클릭
//            btnSelf.setOnClickListener {
//                clickListener?.onClick("SELF")
//            }

            // '나중에 할께요' 버튼 클릭
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