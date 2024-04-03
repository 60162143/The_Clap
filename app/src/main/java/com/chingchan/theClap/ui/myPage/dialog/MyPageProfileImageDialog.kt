package com.chingchan.theClap.ui.myPage.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import com.chingchan.theClap.databinding.DlMypageProfileImageBinding


class MyPageProfileImageDialog(context: Context) : Dialog(context) {
    interface OnClickListener {
        fun onClick(type: String) {}
    }

    private var clickListener: OnClickListener? = null

    private lateinit var binding: DlMypageProfileImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DlMypageProfileImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명하게
        window?.setGravity(Gravity.TOP or Gravity.CENTER)  // 상단 중앙에 위치

        with(binding){

            // 사진 촬영 버튼 클릭
            btnCamera.setOnClickListener {
                clickListener?.onClick("CAMERA")
            }

            // 사진 업로드 버튼 클릭
            btnStorage.setOnClickListener {
                clickListener?.onClick("STORAGE")
            }

            // 기본 이미지 버튼 클릭
            btnDelete.setOnClickListener {
                clickListener?.onClick("DELETE")
            }
        }
    }

    // 클릭 리스너 SET
    fun setListener(clickListener: OnClickListener){
        this.clickListener = clickListener
    }
}