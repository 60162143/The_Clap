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

class ComplimentDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActiCompDetailBinding

    private lateinit var compCmtRecyclerAdapter: CompCmtRecyclerAdapter

    private val editDialog: CompEditDialog by lazy { CompEditDialog(this) } // 게시글 수정(본인) 다이얼로그
    private val editOtherDialog: CompEditOtherDialog by lazy { CompEditOtherDialog(this) } // 게시글 수정(다른 사람) 다이얼로그
    private val deleteDialog: CompDeleteDialog by lazy { CompDeleteDialog(this) } // 삭제하기 다이얼로그
    private val hideDialog: CompHideDialog by lazy { CompHideDialog(this) } // 숨김 처리하기 다이얼로그
    private val blockDialog: CompBlockDialog by lazy { CompBlockDialog(this) } // 차단하기 다이얼로그
    private val reportDialog: CompReportDialog by lazy { CompReportDialog(this) } // 신고하기 다이얼로그
    private val cmtSortDialog: CompCmtSortDialog by lazy { CompCmtSortDialog(this) } // 댓글 정렬 다이얼로그
    private val editCmtDialog: CompCmtEditDialog by lazy { CompCmtEditDialog(this) } // 댓글 수정(본인) 다이얼로그
    private val editCmtOtherDialog: CompCmtEditOtherDialog by lazy { CompCmtEditOtherDialog(this) } // 댓글 수정(다른 사람) 다이얼로그

    private lateinit var compData: CompData
    private var compCmtDataList: ArrayList<CompCmtData> = ArrayList<CompCmtData>()
    private lateinit var compCatDataList: ArrayList<CompCatData>

    // 게시글 수정 Intent registerForActivityResult
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LoginResult.SUCCESS.code) {
            Log.e("comp", "success!!")
        }else if (result.resultCode == LoginResult.CANCEL.code) {

        } else if (result.resultCode == LoginResult.BACK.code) {

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiCompDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compData = GlobalFunction.getParcelableExtra(intent, "compData", CompData::class.java)!!    // 게시글 데이터
        compCatDataList = GlobalFunction.getSerializableExtra(intent, "compCatData", ArrayList<CompCatData>()::class.java)!!    // 카테고리 데이터

        // 테스트 데이터
        compCmtDataList.add(CompCmtData(boardId = 1, content = "수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. ", parentId = 0, groupOrder = 0, groupLayer = 0))
        compCmtDataList.add(CompCmtData(boardId = 2, content = "수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. ", parentId = 0, groupOrder = 0, groupLayer = 0))
        compCmtDataList.add(CompCmtData(boardId = 3, content = "수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. ", parentId = 0, groupOrder = 0, groupLayer = 0))
        compCmtDataList.add(CompCmtData(boardId = 4, content = "수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. ", parentId = 0, groupOrder = 0, groupLayer = 0))
        compCmtDataList.add(CompCmtData(boardId = 5, content = "수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. ", parentId = 0, groupOrder = 0, groupLayer = 0))
        compCmtDataList.add(CompCmtData(boardId = 6, content = "수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. ", parentId = 0, groupOrder = 0, groupLayer = 0))
        compCmtDataList.add(CompCmtData(boardId = 7, content = "수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. ", parentId = 0, groupOrder = 0, groupLayer = 0))
        compCmtDataList.add(CompCmtData(boardId = 8, content = "수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. 수고했습니다. ", parentId = 0, groupOrder = 0, groupLayer = 0))

        with(binding){

            userName.text = compData.nickname

            userWriteTime.text = compData.modifyTs
            var writeTime = LocalDateTime.parse(compData.modifyTs.replace("Z", ""))
            val currentTime = LocalDateTime.now()

            if(writeTime.until(currentTime, ChronoUnit.DAYS) >= 365){
                userWriteTime.text = writeTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
            }else if(writeTime.until(currentTime, ChronoUnit.HOURS) >= 24){
                userWriteTime.text = writeTime.format(DateTimeFormatter.ofPattern("MM월 dd일"))
            }else if(writeTime.until(currentTime, ChronoUnit.MINUTES) >= 60){
                userWriteTime.text = writeTime.until(currentTime, ChronoUnit.HOURS).toString() + "시간"
            }else if(writeTime.until(currentTime, ChronoUnit.MINUTES) >= 1){
                userWriteTime.text = writeTime.until(currentTime, ChronoUnit.MINUTES).toString() + "분"
            }else{
                userWriteTime.text = "방금"
            }

            rvCompWriteBtnCat.text = compData.typeName
            compTitle.text = compData.title
            compContent.text = compData.content
            heartCount.text = compData.likes.toString()
//                commentCount.text = compData.likes.toString()
            compViewCount.text = compData.views.toString()





            compCmtRecyclerAdapter = CompCmtRecyclerAdapter()
            rvComment.adapter = compCmtRecyclerAdapter

            // DiffUtil 적용 후 데이터 추가
            compCmtRecyclerAdapter.differ.submitList(compCmtDataList)

            // 댓글 클릭 리스너
            compCmtRecyclerAdapter.clickListener = object : CompCmtRecyclerAdapter.OnClickListener{
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

            // 게시글 수정(본인) 다이얼로그 클릭 리스너
            editDialog.setListener(object : CompEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "EDIT"){
                        Log.e("editDialog", "EDIT")
                    }else if(type == "HIDE"){
                        Log.e("editDialog", "HIDE")
                        editDialog.dismiss()
                        hideDialog.show()
                    }else if(type == "DELETE"){
                        Log.e("editDialog", "DELETE")
                        editDialog.dismiss()
                        deleteDialog.show()
                    }else if(type == "CANCEL"){
                        Log.e("editDialog", "CANCEL")
                        editDialog.dismiss()
                    }
                }
            })

            // 게시글 수정(다른 사람) 다이얼로그 클릭 리스너
            editOtherDialog.setListener(object : CompEditOtherDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "BLOCK"){
                        Log.e("editOtherDialog", "BLOCK")
                        editOtherDialog.dismiss()
                        blockDialog.show()
                    }else if(type == "REPORT"){
                        editOtherDialog.dismiss()
                        reportDialog.show()
                        Log.e("editOtherDialog", "REPORT")
                    }else if(type == "CHAT"){
                        Log.e("editOtherDialog", "CHAT")
                    }else if(type == "CANCEL"){
                        Log.e("editOtherDialog", "CANCEL")
                        editOtherDialog.dismiss()
                    }
                }
            })

            // 삭제하기 다이얼로그 클릭 리스너
            deleteDialog.setListener(object : CompDeleteDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "DELETE"){
                        Log.e("deleteDialog", "DELETE")

                    }else if(type == "CANCEL"){
                        Log.e("deleteDialog", "CANCEL")
                        blockDialog.dismiss()
                    }
                }
            })

            // 숨김 처리하기 다이얼로그 클릭 리스너
            hideDialog.setListener(object : CompHideDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "HIDE"){
                        Log.e("hideDialog", "HIDE")

                    }else if(type == "CANCEL"){
                        Log.e("hideDialog", "CANCEL")
                        blockDialog.dismiss()
                    }
                }
            })

            // 차단하기 다이얼로그 클릭 리스너
            blockDialog.setListener(object : CompBlockDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "BLOCK"){
                        Log.e("blockDialog", "BLOCK")

                    }else if(type == "CANCEL"){
                        Log.e("blockDialog", "CANCEL")
                        blockDialog.dismiss()
                    }
                }
            })

            // 신고하기 다이얼로그 클릭 리스너
            reportDialog.setListener(object : CompReportDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "REPORT"){
                        Log.e("reportDialog", "REPORT")

                    }else if(type == "CANCEL"){
                        Log.e("reportDialog", "CANCEL")
                        reportDialog.dismiss()
                    }
                }
            })

            // 댓글 정렬 다이얼로그 클릭 리스너
            cmtSortDialog.setListener(object : CompCmtSortDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "NEWEST"){
                        Log.e("reportDialog", "NEWEST")

                    }else if(type == "OLD"){
                        Log.e("reportDialog", "OLD")

                    }else if(type == "CANCEL"){
                        Log.e("reportDialog", "CANCEL")
                        cmtSortDialog.dismiss()
                    }
                }
            })

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

            // 북마크 버튼 클릭 리스너
            btnBookmark.setOnClickListener(View.OnClickListener {
                btnBookmark.isSelected = !btnBookmark.isSelected
            })

            // 더보기 버튼 클릭 리스너
            btnMore.setOnClickListener(View.OnClickListener {
                if(UserData.getUserId(applicationContext) == compData.userId){
                    editDialog.show()
                }else{
                    editOtherDialog.show()
                }
            })

            // 댓글 정렬 버튼 클릭 리스너
            btnCmtSort.setOnClickListener(View.OnClickListener {
                cmtSortDialog.show()
            })

            // 댓글 작성 버튼 클릭 리스너
            btnConfirm.setOnClickListener(View.OnClickListener {
                Log.e("btnConfirm", "Click!!")
            })

            // 유저 프로필 이미지 클릭 리스너
            userImage.setOnClickListener(View.OnClickListener {
                Log.e("userImage", "Click!!")
            })

            // 좋아요 버튼 클릭 리스너
            btnHeart.setOnClickListener(View.OnClickListener {
                Log.e("btnHeart", "Click!!")
            })

            // 댓글 버튼 클릭 리스너
            btnComment.setOnClickListener(View.OnClickListener {
                Log.e("btnComment", "Click!!")

                // 게시글 상세 Activity로 화면 전환
                val intent = Intent(this@ComplimentDetailActivity, ComplimentCommentDetailActivity::class.java)
                intent.putParcelableArrayListExtra("compCmtDataList", compCmtDataList)
                intent.putExtra("compData", compData)
                startActivity(intent)
            })

            // 공유 버튼 클릭 리스너
            btnShare.setOnClickListener(View.OnClickListener {
                Log.e("btnShare", "Click!!")
            })
        }
    }

    override fun onResume() {
        super.onResume()

    }
}