package com.chingchan.theClap.ui.compliment

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.databinding.ActiCompBinding
import com.chingchan.theClap.databinding.ActiCompCmtDetailBinding
import com.chingchan.theClap.databinding.ActiCompDetailBinding
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCmtData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.login.LoginActivity
import com.chingchan.theClap.ui.login.data.LoginResult
import com.chingchan.theClap.ui.login.data.UserData
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount

import java.util.Date

class ComplimentCommentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActiCompCmtDetailBinding

    private lateinit var compCmtDetailRecyclerAdapter: CompCmtDetailRecyclerAdapter

    private val editCmtDialog: CompCmtEditDialog by lazy { CompCmtEditDialog(this) } // 댓글 수정(본인) 다이얼로그
    private val editCmtOtherDialog: CompCmtEditOtherDialog by lazy { CompCmtEditOtherDialog(this) } // 댓글 수정(다른 사람) 다이얼로그

    private lateinit var compData: CompData
    private lateinit var compCmtDataList: ArrayList<CompCmtData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiCompCmtDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compData = GlobalFunction.getParcelableExtra(intent, "compData", CompData::class.java)!!    // 게시글 데이터
        compCmtDataList = GlobalFunction.getSerializableExtra(intent, "compCmtDataList", ArrayList<CompCmtData>()::class.java)!!    // 댓글 데이터

        with(binding){
            compCmtDetailRecyclerAdapter = CompCmtDetailRecyclerAdapter()
            rvComment.adapter = compCmtDetailRecyclerAdapter

            // DiffUtil 적용 후 데이터 추가
            compCmtDetailRecyclerAdapter.differ.submitList(compCmtDataList)

            // 댓글 클릭 리스너
            compCmtDetailRecyclerAdapter.clickListener = object : CompCmtDetailRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "USER_IMAGE"){
                        Log.e("click!!", "user_image")
                    }else if(type == "MORE"){
                        // 댓글 작성 유저가 본인인지 확인 후 다이얼로그 보이기
                        editCmtDialog.show()
//                        editCmtOtherDialog.show()
                    }else if(type == "HEART"){
                        Log.e("click!!", "heart")
                    }
                }
            }

            // 댓글 수정(본인) 다이얼로그 클릭 리스너
            editCmtDialog.setListener(object : CompCmtEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "EDIT"){
                        Log.e("editCmtDialog", "EDIT")
                    }else if(type == "DELETE"){
                        Log.e("editCmtDialog", "DELETE")
                    }else if(type == "CANCEL"){
                        Log.e("editCmtDialog", "CANCEL")
                        editCmtDialog.dismiss()
                    }
                }
            })

            // 댓글 수정(다른 사람) 다이얼로그 클릭 리스너
            editCmtOtherDialog.setListener(object : CompCmtEditOtherDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "CHAT"){
                        Log.e("editCmtOtherDialog", "CHAT")
                    }else if(type == "REPORT"){
                        Log.e("editCmtOtherDialog", "REPORT")
                    }else if(type == "CANCEL"){
                        Log.e("editCmtOtherDialog", "CANCEL")
                        editCmtOtherDialog.dismiss()
                    }
                }
            })

            // 댓글 작성 버튼 클릭 리스너
            btnConfirm.setOnClickListener(View.OnClickListener {
                Log.e("btnConfirm", "Click!!")
            })
        }
    }

    override fun onResume() {
        super.onResume()

    }
}