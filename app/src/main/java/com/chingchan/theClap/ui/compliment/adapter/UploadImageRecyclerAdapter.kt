package com.chingchan.theClap.ui.compliment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.chingchan.theClap.databinding.RvCompWriteImageBinding
import com.chingchan.theClap.globalFunction.GlideApp
import com.chingchan.theClap.ui.compliment.data.UploadImageURI

class UploadImageRecyclerAdapter:
    ListAdapter<UploadImageURI, UploadImageRecyclerAdapter.UploadImageViewHolder>(diffUtil) {

//ListAdapter<데이터클래스,리사이클러뷰 뷰홀더> 를 인자로 받는다-

    interface OnImageDeleteClickListener {
        fun onImageDeleteClick(position: Int) {}
    }

    var onImageDeleteClickListener: OnImageDeleteClickListener? = null

    inner class UploadImageViewHolder(private val binding: RvCompWriteImageBinding): RecyclerView.ViewHolder(binding.root){
        //뷰홀더: 내가 넣고자하는 data를 실제 레이아웃의 데이터로 연결시키는 기능
        fun bind(uploadImageURI: UploadImageURI){//view와 데이터를 연결시키는 함수-/>뷰에 데이터 넣음

            if(uploadImageURI.isNew){
                with(binding){
                    GlideApp
                        .with(rvImage.context) //context가 어댑터에 없다 -> 뷰에 있겠죠?
                        .load(uploadImageURI.uri)
                        .into(rvImage)
                }
            }else{
                with(binding){
                    GlideApp
                        .with(rvImage.context) //context가 어댑터에 없다 -> 뷰에 있겠죠?
                        .load(uploadImageURI.url)
                        .into(rvImage)
                }
            }
        }

        init {
            binding.rvImageDelete.setOnClickListener {
                onImageDeleteClickListener?.onImageDeleteClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UploadImageViewHolder {
        //미리 만들어진 뷰홀더가 없는 경우 새로 생성하는 함수(레이아웃 생성)
        return UploadImageViewHolder(RvCompWriteImageBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: UploadImageViewHolder, position: Int) {
        //실제로 뷰홀더가 뷰에 그려졌을때 데이터를 뿌려주는 바인드해주는 함수(뷰홀더가 재활용될때 실행)
        holder.bind(currentList[position])
    }

    //diffutil사용하려면 diffutil.callback이라는 기능을 구현해야함
    companion object{
        val diffUtil=object: DiffUtil.ItemCallback<UploadImageURI>(){
            override fun areItemsTheSame(oldItem: UploadImageURI, newItem: UploadImageURI): Boolean {
                return oldItem.uri==newItem.uri
            }

            override fun areContentsTheSame(oldItem: UploadImageURI, newItem: UploadImageURI): Boolean {
                return oldItem==newItem
            }

        }
    }
}