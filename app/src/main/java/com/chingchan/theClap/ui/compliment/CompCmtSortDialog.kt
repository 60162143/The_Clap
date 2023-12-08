package com.chingchan.theClap.ui.compliment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.DlCompBlockBinding
import com.chingchan.theClap.databinding.DlCompCmtSortBinding
import com.chingchan.theClap.databinding.DlCompEditBinding
import com.chingchan.theClap.databinding.DlCompLoginBinding
import com.chingchan.theClap.databinding.DlCompReportBinding
import com.chingchan.theClap.databinding.DlCompWriteUploadImageBinding


class CompCmtSortDialog(context: Context) : Dialog(context) {
    interface OnClickListener {
        fun onClick(type: String) {}
    }

    private var clickListener: OnClickListener? = null

    private lateinit var binding: DlCompCmtSortBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DlCompCmtSortBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명하게
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Width 꽉차게
        window?.setGravity(Gravity.BOTTOM)  // 하단에 위치

        setCanceledOnTouchOutside(false)    // 다이얼로그 외부 화면을 터치할 때 다이얼로그가 종료되지 않도록

        with(binding){

            // 최신순 버튼 클릭
            btnNewest.setOnClickListener {
                clickListener?.onClick("NEWEST")
            }

            // 오래된순 버튼 클릭
            btnOld.setOnClickListener {
                clickListener?.onClick("OLD")
            }

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