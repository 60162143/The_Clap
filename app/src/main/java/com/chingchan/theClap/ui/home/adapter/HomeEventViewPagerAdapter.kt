package com.chingchan.theClap.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.chingchan.theClap.R

class HomeEventViewPagerAdapter(eventList: ArrayList<Int>) : RecyclerView.Adapter<HomeEventViewPagerAdapter.PagerViewHolder>() {
    var item = eventList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

    override fun getItemCount(): Int = item.size

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.eventImage.setImageResource(item[position])
    }

    inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
        (LayoutInflater.from(parent.context).inflate(R.layout.vp_frag_home_event, parent, false)){

        val eventImage = itemView.findViewById<ImageView>(R.id.vp_event_image)!!
        val eventText = itemView.findViewById<TextView>(R.id.vp_event_text)!!
    }
}