package com.chingchan.theClap.ui.myPage.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chingchan.theClap.ui.myPage.fragment.MyPageOtherReceiveCompFragment
import com.chingchan.theClap.ui.myPage.fragment.MyPageOtherWriteCompFragment
import com.chingchan.theClap.ui.myPage.fragment.MyPageReceiveCompFragment
import com.chingchan.theClap.ui.myPage.fragment.MyPageWriteCompFragment

private const val FRAG_NUMS = 2

class MyPageOtherCompVpAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private var myPageOtherReceiveCompFragment: MyPageOtherReceiveCompFragment = MyPageOtherReceiveCompFragment()
    private var myPageOtherWriteCompFragment: MyPageOtherWriteCompFragment = MyPageOtherWriteCompFragment()

    override fun getItemCount(): Int {
        return FRAG_NUMS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> myPageOtherReceiveCompFragment
            else -> myPageOtherWriteCompFragment
        }
    }

    // '타인이 받은 칭찬' Fragment 객체 받기
    fun getOtherReceiveFragment(): MyPageOtherReceiveCompFragment{
        return myPageOtherReceiveCompFragment
    }

    // '타인이 작성한 칭찬' Fragment 객체 받기
    fun getOtherWriteFragment(): MyPageOtherWriteCompFragment{
        return myPageOtherWriteCompFragment
    }
}