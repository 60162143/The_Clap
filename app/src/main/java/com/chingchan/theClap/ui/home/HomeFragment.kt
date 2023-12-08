package com.chingchan.theClap.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.FragHomeBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.math.abs

class HomeFragment : Fragment() {

    private var _binding: FragHomeBinding? = null
    private var showShimmer: Job? = null

    //뒤로가기 연속 클릭 대기 시간
    var mBackWait:Long = 0

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding){
            eventViewpager.adapter = HomeEventViewPagerAdapter(getEventList()) // 어댑터 생성
            eventViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향을 가로로
            eventViewpager.offscreenPageLimit=3
            eventViewpager.getChildAt(0).overScrollMode=View.OVER_SCROLL_NEVER

            val transform = CompositePageTransformer()
            transform.addTransformer(MarginPageTransformer(30))

            transform.addTransformer(ViewPager2.PageTransformer{ view: View, fl: Float ->
                val v = 1 - abs(fl)
                view.alpha = 0.5f + v * 0.5f
                view.scaleY = 0.8f + v * 0.2f
            })

            eventViewpager.setPageTransformer(transform)

            loadData()  // 데이터 가져오기
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
    }

    // 뷰 페이저에 들어갈 아이템
    private fun getEventList(): ArrayList<Int> {
        return arrayListOf<Int>(R.drawable.test_image_1, R.drawable.test_image_1, R.drawable.test_image_1)
    }

    // 스켈레톤 로딩 화면 UI delay
    private fun loadData() {
        showShimmer = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                showEventData(isLoading = true)
                delay(2000)

                showEventData(isLoading = false)
            }
        }
    }

    private fun showEventData(isLoading: Boolean) {
        with(binding){
            if (isLoading) {
                fragHomeShimmerLayout.startShimmer()
                fragHomeShimmerLayout.visibility = View.VISIBLE
                fragHomeLayout.visibility = View.GONE
            } else {
                fragHomeShimmerLayout.stopShimmer()
                fragHomeShimmerLayout.visibility = View.GONE
                fragHomeLayout.visibility = View.VISIBLE
            }
        }
    }
}