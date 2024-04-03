package com.chingchan.theClap.ui.myPage.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chingchan.theClap.ui.myPage.fragment.MyPageReceiveCompFragment
import com.chingchan.theClap.ui.myPage.fragment.MyPageWriteCompFragment

private const val FRAG_NUMS = 2

class MyPageCompVpAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private var myPageReceiveCompFragment: MyPageReceiveCompFragment = MyPageReceiveCompFragment()
    private var myPageWriteCompFragment: MyPageWriteCompFragment = MyPageWriteCompFragment()

    override fun getItemCount(): Int {
        return FRAG_NUMS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> myPageReceiveCompFragment
            else -> myPageWriteCompFragment
        }
    }

    // '받은 칭찬' Fragment 객체 받기
    fun getReceiveFragment(): MyPageReceiveCompFragment{
        return myPageReceiveCompFragment
    }

    // '작성한 칭찬' Fragment 객체 받기
    fun getWriteFragment(): MyPageWriteCompFragment{
        return myPageWriteCompFragment
    }
}