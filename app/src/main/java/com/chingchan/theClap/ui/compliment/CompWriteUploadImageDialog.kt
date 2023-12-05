package com.chingchan.theClap.ui.compliment

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.chingchan.theClap.databinding.DlCompWriteUploadImageBinding


class CompWriteUploadImageDialog(context: Context, uploadImageDialogInterface: UploadImageDialogInterface) : Dialog(context) {

    private lateinit var binding: DlCompWriteUploadImageBinding

    private var uploadImageDialogInterface:UploadImageDialogInterface? = null

    // 인터페이스 연결
    init {
        this.uploadImageDialogInterface = uploadImageDialogInterface
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DlCompWriteUploadImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명하게
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Width 꽉차게
        window?.setGravity(Gravity.BOTTOM)  // 하단에 위치

        // 사진 보관함 버튼 클릭
        binding.btnStorage.setOnClickListener {
            this.uploadImageDialogInterface?.onStorageBtnClicked()
        }

        // 사진 찍기 버튼 클릭
        binding.btnCamera.setOnClickListener {
            this.uploadImageDialogInterface?.onCameraBtnClicked()
        }

        // 사진 찍기 버튼 클릭
        binding.btnCancel.setOnClickListener {
            this.uploadImageDialogInterface?.onCancelBtnClicked()
        }
    }
}