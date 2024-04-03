package com.chingchan.theClap.ui.search.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.chingchan.theClap.ui.myPage.fragment.MyPageReceiveCompFragment
import com.chingchan.theClap.ui.myPage.fragment.MyPageWriteCompFragment
import com.chingchan.theClap.ui.search.fragment.SearchCompFragment
import com.chingchan.theClap.ui.search.fragment.SearchUserFragment

private const val FRAG_NUMS = 2

class SearchVpAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {

    private var searchCompFragment: SearchCompFragment = SearchCompFragment()
    private var searchUserFragment: SearchUserFragment = SearchUserFragment()

    override fun getItemCount(): Int {
        return FRAG_NUMS
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> searchCompFragment
            else -> searchUserFragment
        }
    }

    // 칭찬글 검색 Fragment 객체 받기
    fun getSearchCompFragment(): SearchCompFragment{
        return searchCompFragment
    }

    // 사용자 검색 Fragment 객체 받기
    fun getSearchUserFragment(): SearchUserFragment{
        return searchUserFragment
    }
}