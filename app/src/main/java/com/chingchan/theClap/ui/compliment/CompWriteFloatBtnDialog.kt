package com.chingchan.theClap.ui.compliment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import com.chingchan.theClap.databinding.DlCompLoginBinding
import com.chingchan.theClap.databinding.DlCompWriteFloatBtnBinding
import com.chingchan.theClap.databinding.DlCompWriteUploadImageBinding

class CompWriteFloatBtnDialog(context: Context) : Dialog(context) {

    interface OnClickListener {
        fun onClick(type: String) {}
    }

    private var clickListener: OnClickListener? = null

    private lateinit var binding: DlCompWriteFloatBtnBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DlCompWriteFloatBtnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명하게
        window?.setGravity(Gravity.BOTTOM or Gravity.END)  // 하단에 위치

        with(binding){

            // 지인 칭찬 버튼 클릭
            btnFriendComp.setOnClickListener {
                clickListener?.onClick("FRIEND")
            }

            // 셀프 칭찬 버튼 클릭
            btnSelfComp.setOnClickListener {
                clickListener?.onClick("SELF")
            }
        }
    }

    // 클릭 리스너 SET
    fun setListener(clickListener: OnClickListener){
        this.clickListener = clickListener
    }
}