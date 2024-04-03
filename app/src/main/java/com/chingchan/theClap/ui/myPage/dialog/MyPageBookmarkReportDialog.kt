package com.chingchan.theClap.ui.myPage.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.DlCompReportBinding
import com.chingchan.theClap.ui.compliment.data.ReportType
import com.chingchan.theClap.ui.toast.customToast


class MyPageBookmarkReportDialog(context: Context) : Dialog(context) {
    interface OnClickListener {
        fun onClick(type: String, reportType: String, reportReason: String) {}
    }

    private var clickListener: OnClickListener? = null

    private lateinit var binding: DlCompReportBinding
    private var _context = context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DlCompReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) // 배경 투명하게
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) // Width 꽉차게

        setCanceledOnTouchOutside(false)    // 다이얼로그 외부 화면을 터치할 때 다이얼로그가 종료되지 않도록

        with(binding){

            // 텍스트에 밑줄 추가
            btnCancel.paintFlags = btnCancel.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            // 신고하기 버튼 클릭
            btnReport.setOnClickListener {
                // '기타' 선택 후 사유를 입력하지 않거나 5자 미만으로 작성한 경우
                if(rgBtn5.isChecked and (editReason.text.isEmpty() or (editReason.text.toString().length < 5))){
                    customToast.showCustomToast("사유를 입력해 주세요.", _context)
                }else{
                    when(radioGroup.checkedRadioButtonId){
                        rgBtn1.id -> clickListener?.onClick("REPORT", ReportType.COMMERCIAL_CONTENT_OR_SPAM.name, "")
                        rgBtn2.id -> clickListener?.onClick("REPORT", ReportType.INAPPROPRIATE_CONTENT.name, "")
                        rgBtn3.id -> clickListener?.onClick("REPORT", ReportType.VIOLENCE.name, "")
                        rgBtn4.id -> clickListener?.onClick("REPORT", ReportType.MISINFORMATION.name, "")
                        rgBtn5.id -> clickListener?.onClick("REPORT", ReportType.OTHER.name, editReason.text.toString())
                    }

                    initView()  // View 초기화
                }
            }

            // '나중에 할께요' 버튼 클릭
            btnCancel.setOnClickListener {
                initView()  // View 초기화

                clickListener?.onClick("CANCEL", "", "")
            }

            // '기타' 선택시에만 사유 입력 가능
            radioGroup.setOnCheckedChangeListener{ _, i ->
                editReason.isEnabled = i == rgBtn5.id

                if(i == rgBtn5.id){
                    editReason.setTextColor(ContextCompat.getColor(_context, R.color.gray6))
                }else{
                    editReason.setTextColor(ContextCompat.getColor(_context, R.color.gray3))
                }
            }
        }
    }

    // View 초기화
    private fun initView(){
        with(binding){
            // 초기화
            rgBtn1.isChecked = true
            editReason.text.clear()
        }
    }

    // 클릭 리스너 SET
    fun setListener(clickListener: OnClickListener){
        this.clickListener = clickListener
    }

    // 화면 터치시 키보드 내리기
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val imm: InputMethodManager = _context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return true
    }
}