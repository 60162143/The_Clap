package com.chingchan.theClap.ui.compliment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chingchan.theClap.R

class CompBannerViewPagerAdapter(eventList: ArrayList<String>) : RecyclerView.Adapter<CompBannerViewPagerAdapter.PagerViewHolder>() {
    var item = eventList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.eventText.text = item[position]
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.vp_frag_comp_banner, parent, false)){

        val eventText = itemView.findViewById<TextView>(R.id.vp_banner_text)!!
    }
}