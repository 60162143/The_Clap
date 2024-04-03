package com.chingchan.theClap.ui.myPage.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.chingchan.theClap.databinding.DlCompWriteUploadImageBinding
import com.chingchan.theClap.databinding.DlMypageRecCompEditBinding
import com.chingchan.theClap.databinding.DlMypageWriteCompEditBinding


class MyPageWriteCompEditDialog(context: Context) : Dialog(context) {

    interface OnClickListener {
        fun onClick(type: String) {}
    }

    private var clickListener: OnClickListener? = null

    private lateinit var binding: DlMypageWriteCompEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DlMypageWriteCompEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명하게
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Width 꽉차게
        window?.setGravity(Gravity.BOTTOM)  // 하단에 위치

        setCanceledOnTouchOutside(false)    // 다이얼로그 외부 화면을 터치할 때 다이얼로그가 종료되지 않도록

        with(binding){
            // 수정하기 버튼 클릭
            btnEdit.setOnClickListener {
                clickListener?.onClick("EDIT")
            }

            // 삭제 버튼 클릭
            btnDelete.setOnClickListener {
                clickListener?.onClick("DELETE")
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