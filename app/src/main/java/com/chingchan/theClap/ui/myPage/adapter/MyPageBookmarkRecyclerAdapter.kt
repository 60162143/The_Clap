package com.chingchan.theClap.ui.myPage.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chingchan.theClap.databinding.RvCompBinding
import com.chingchan.theClap.databinding.RvMypageCompBinding
import com.chingchan.theClap.ui.compliment.data.CompData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class MyPageBookmarkRecyclerAdapter() : RecyclerView.Adapter<MyPageBookmarkRecyclerAdapter.RecyclerViewHolder>() {

    interface OnClickListener {
        fun onClick(position: Int, type: String) {}
    }

    var clickListener: OnClickListener? = null

    // 추가
    private val differCallback = object : DiffUtil.ItemCallback<CompData>() {
        override fun areItemsTheSame(oldItem: CompData, newItem: CompData): Boolean {
            // User의 id를 비교해서 같으면 areContentsTheSame으로 이동(id 대신 data 클래스에 식별할 수 있는 변수 사용)
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CompData, newItem: CompData): Boolean {
            // User의 내용을 비교해서 같으면 true -> UI 변경 없음
            // User의 내용을 비교해서 다르면 false -> UI 변경
            return oldItem.hashCode() == newItem.hashCode()
        }

        // 값이 변경되었을 경우 여기서 payload가 있는 viewHolder로 가서 UI 변경됨
        override fun getChangePayload(oldItem: CompData, newItem: CompData): Any? {
            return super.getChangePayload(oldItem, newItem)
        }
    }

    // 리스트가 많으면 백그라운드에서 실행하는 게 좋은데 AsyncListDiffer은 자동으로 백그라운드에서 실행
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        //미리 만들어진 뷰홀더가 없는 경우 새로 생성하는 함수(레이아웃 생성)
        return RecyclerViewHolder(RvMypageCompBinding.inflate(LayoutInflater.from(parent.context),parent,false))
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
        }else{

        }
    }

    inner class RecyclerViewHolder(private val binding: RvMypageCompBinding) : RecyclerView.ViewHolder(binding.root){
        //뷰홀더: 내가 넣고자하는 data를 실제 레이아웃의 데이터로 연결시키는 기능
        fun bind(compData: CompData){//view와 데이터를 연결시키는 함수-/>뷰에 데이터 넣음

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

                catType.text = compData.typeName
                compTitle.text = compData.title
                compContent.text = compData.content
                btnCompHeart.text = compData.likes.toString()
                btnCompCmt.text = compData.comments.toString()
                btnCompView.text = compData.views.toString()

                btnCompHeart.isSelected = compData.like // 좋아요 여부
            }
        }

        init {
            with(binding){
                // 유저 프로필 이미지 클릭 리스너
                compUserImage.setOnClickListener {
                    clickListener?.onClick(adapterPosition, "USER_IMAGE")
                }

                // 더보기 버튼 클릭 리스너
                btnMore.setOnClickListener {
                    clickListener?.onClick(adapterPosition, "MORE")
                }

                // 게시글 좋아요 버튼 클릭 리스너
                btnCompHeart.setOnClickListener {
                    clickListener?.onClick(adapterPosition, "HEART")
                }

                // 게시글 전체 클릭 리스너
                itemView.setOnClickListener {
                    clickListener?.onClick(adapterPosition, "TOTAL")
                }
            }
        }
    }
}