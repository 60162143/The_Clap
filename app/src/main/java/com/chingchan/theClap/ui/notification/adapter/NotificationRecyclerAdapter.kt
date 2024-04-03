package com.chingchan.theClap.ui.notification.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.RvCompBinding
import com.chingchan.theClap.databinding.RvMypageAnnounceBinding
import com.chingchan.theClap.databinding.RvMypageCompBinding
import com.chingchan.theClap.databinding.RvMypageFaqBinding
import com.chingchan.theClap.databinding.RvNotificationBinding
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.myPage.data.AnnounceData
import com.chingchan.theClap.ui.myPage.data.FAQData
import com.chingchan.theClap.ui.notification.data.NotificationData
import com.chingchan.theClap.ui.notification.data.NotificationType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class NotificationRecyclerAdapter() : RecyclerView.Adapter<NotificationRecyclerAdapter.RecyclerViewHolder>() {

    interface OnClickListener {
        fun onClick(position: Int, type: String) {}
    }

    var clickListener: OnClickListener? = null

    // 추가
    private val differCallback = object : DiffUtil.ItemCallback<NotificationData>() {
        override fun areItemsTheSame(oldItem: NotificationData, newItem: NotificationData): Boolean {
            // User의 id를 비교해서 같으면 areContentsTheSame으로 이동(id 대신 data 클래스에 식별할 수 있는 변수 사용)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationData, newItem: NotificationData): Boolean {
            // User의 내용을 비교해서 같으면 true -> UI 변경 없음
            // User의 내용을 비교해서 다르면 false -> UI 변경
            return oldItem.hashCode() == newItem.hashCode()
        }

        // 값이 변경되었을 경우 여기서 payload가 있는 viewHolder로 가서 UI 변경됨
        override fun getChangePayload(oldItem: NotificationData, newItem: NotificationData): Any? {
            return super.getChangePayload(oldItem, newItem)
        }
    }

    // 리스트가 많으면 백그라운드에서 실행하는 게 좋은데 AsyncListDiffer은 자동으로 백그라운드에서 실행
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        //미리 만들어진 뷰홀더가 없는 경우 새로 생성하는 함수(레이아웃 생성)
        return RecyclerViewHolder(RvNotificationBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int = differ.currentList.size

    // 기본 뷰홀더
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    // 값(isSelected)이 변경되었을경우 호출되는 뷰홀더
    override fun onBindViewHolder(
        holder: RecyclerViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if(payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    inner class RecyclerViewHolder(private val binding: RvNotificationBinding) : RecyclerView.ViewHolder(binding.root){
        //뷰홀더: 내가 넣고자하는 data를 실제 레이아웃의 데이터로 연결시키는 기능
        fun bind(notificationData: NotificationData){//view와 데이터를 연결시키는 함수-/>뷰에 데이터 넣음

            with(binding){
                val writeTime = LocalDateTime.parse(notificationData.createTs.replace("Z", ""))
                val currentTime = LocalDateTime.now()

                if(writeTime.until(currentTime, ChronoUnit.DAYS) >= 365){
                    textCreate.text = writeTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일"))
                }else if(writeTime.until(currentTime, ChronoUnit.HOURS) >= 24){
                    textCreate.text = writeTime.format(DateTimeFormatter.ofPattern("MM월 dd일"))
                }else if(writeTime.until(currentTime, ChronoUnit.MINUTES) >= 60){
                    textCreate.text = writeTime.until(currentTime, ChronoUnit.HOURS).toString() + "시간"
                }else if(writeTime.until(currentTime, ChronoUnit.MINUTES) >= 1){
                    textCreate.text = writeTime.until(currentTime, ChronoUnit.MINUTES).toString() + "분"
                }else{
                    textCreate.text = "방금"
                }

                // 1. String 문자열 데이터 취득
                val textContentData: String = notificationData.target + notificationData.message

                // 2. SpannableStringBuilder 타입으로 변환
                val builder = SpannableStringBuilder(textContentData)

                // 3. target 에 해당하는 문자열에 볼드체, 색상 적용
                val boldSpan = StyleSpan(Typeface.BOLD)
                builder.setSpan(boldSpan, 0, notificationData.target?.length ?: 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                builder.setSpan(ForegroundColorSpan(Color.BLACK), 0, notificationData.target?.length ?: 0, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                // 5. TextView에 적용
                textContent.text = builder

                btnFollow.visibility = View.GONE // 팔로우 버튼 숨기기

                if(notificationData.logTypeId == NotificationType.TYPE01.code){ // 팔로우 관련
                    notificationIcon.setImageResource(R.drawable.ic_noti_follow)

                    // 상대방을 팔로잉 하고 있지 않은 경우
                    if(!notificationData.followYN && notificationData.userLogActiveType == "OTHER_ACTIVE"){
                        btnFollow.visibility = View.VISIBLE // 팔로우 버튼 보이기
                    }

                }else if(notificationData.logTypeId == NotificationType.TYPE02.code){ // 댓글 관련
                    notificationIcon.setImageResource(R.drawable.ic_noti_comment)
                }else if(notificationData.logTypeId == NotificationType.TYPE03.code){ // 게시글 좋아요 관련
                    notificationIcon.setImageResource(R.drawable.ic_noti_heart)
                }else if(notificationData.logTypeId == NotificationType.TYPE04.code){ // 게시글 신고, 삭제 관련
                    notificationIcon.setImageResource(R.drawable.ic_noti_warning)
                }else if(notificationData.logTypeId == NotificationType.TYPE05.code){ // 인기글 등록 관련
                    notificationIcon.setImageResource(R.drawable.ic_noti_good)
                }else if(notificationData.logTypeId == NotificationType.TYPE06.code){ // 유저 신고 관련
                    notificationIcon.setImageResource(R.drawable.ic_noti_warning)
                }

                if(notificationData.isRead == "Y"){
                    layoutTotal.setBackgroundResource(R.drawable.border_round_gray1_16dp)
                }else{
                    layoutTotal.setBackgroundResource(R.drawable.round_gray1_16dp)
                }
            }
        }

        init {
            with(binding){
                // 알림 전체 클릭 리스너
                itemView.setOnClickListener {
                    clickListener?.onClick(adapterPosition, "TOTAL")
                }

                // '팔로잉하러 가기' 버튼 클릭 리스너
                btnFollow.setOnClickListener {
                    clickListener?.onClick(adapterPosition, "FOLLOW")
                }

                // 삭제 버튼 클릭 리스너
                btnDelete.setOnClickListener {
                    clickListener?.onClick(adapterPosition, "DELETE")
                }
            }
        }
    }
}