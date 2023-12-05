package com.chingchan.theClap.ui.compliment

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chingchan.theClap.R
import com.chingchan.theClap.ui.compliment.data.CompCatData

class CompCatRecyclerAdapter() : RecyclerView.Adapter<CompCatRecyclerAdapter.RecyclerViewHolder>() {

    interface OnCatClickListener {
        fun onCatClick(position: Int) {}
    }

    var catClickListener: OnCatClickListener? = null

    // 추가
    private val differCallback = object : DiffUtil.ItemCallback<CompCatData>() {
        override fun areItemsTheSame(oldItem: CompCatData, newItem: CompCatData): Boolean {
            // User의 id를 비교해서 같으면 areContentsTheSame으로 이동(id 대신 data 클래스에 식별할 수 있는 변수 사용)
            return oldItem.boardTypeId == newItem.boardTypeId
        }

        override fun areContentsTheSame(oldItem: CompCatData, newItem: CompCatData): Boolean {
            // User의 내용을 비교해서 같으면 true -> UI 변경 없음
            // User의 내용을 비교해서 다르면 false -> UI 변경
            return oldItem.hashCode() == newItem.hashCode()
        }

        // 값이 변경되었을 경우 여기서 payload가 있는 viewHolder로 가서 UI 변경됨
        override fun getChangePayload(oldItem: CompCatData, newItem: CompCatData): Any? {
            return super.getChangePayload(oldItem, newItem)
        }
    }

    // 리스트가 많으면 백그라운드에서 실행하는 게 좋은데 AsyncListDiffer은 자동으로 백그라운드에서 실행
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RecyclerViewHolder((parent))

    override fun getItemCount(): Int = differ.currentList.size

    // 기본 뷰홀더
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val compCatData = differ.currentList[position]

        holder.catBtn.isSelected = compCatData.isSelect
        holder.catBtn.text = compCatData.name
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
            val compCatData = differ.currentList[position]

            holder.catBtn.isSelected = compCatData.isSelect
        }
    }

    inner class RecyclerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.rv_comp_write_cat, parent, false)){

        val catBtn = itemView.findViewById<Button>(R.id.rv_comp_write_btn_cat)!!

        init {
            catBtn.setOnClickListener {
                catClickListener?.onCatClick(adapterPosition)
            }
        }
    }
}