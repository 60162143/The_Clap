package com.chingchan.theClap.ui.myPage.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.widget.NestedScrollView

class HeaderScrollView : NestedScrollView, ViewTreeObserver.OnGlobalLayoutListener {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attr: AttributeSet?) : this(context, attr, 0)
    constructor(context: Context, attr: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attr,
        defStyleAttr
    ) {
        overScrollMode = OVER_SCROLL_NEVER
        viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    var header: View? = null
        set(value) {
            field = value

            Log.e("field : ", value.toString())

            field?.let {
                it.translationZ = 1f
//                it.setOnClickListener { _ ->
//                    //클릭 시, 헤더뷰가 최상단으로 오게 스크롤 이동
//                    this.smoothScrollTo(scrollX, it.top)
//                    callStickListener()
//                }
            }
        }

    var userName: View? = null
        set(value) {
            field = value
        }

    var stickListener: (View) -> Unit = {}
    var freeListener: (View) -> Unit = {}

    // 헤더가 천장에 달라 붙어 있는지 아닌지를 체크하는 flag
    private var mIsHeaderSticky = false

    // 헤더의 초기 위치를 저장할 변수
    // 스크롤되는 위치와 비교되어서 헤더가 천장을 넘어서는 스크롤인지 아닌지 판단하는데 이용
    private var mHeaderInitPosition = 0f

    // 레이아웃에 변경이 일어날 경우 호출
    override fun onGlobalLayout() {
        mHeaderInitPosition = header?.top?.toFloat() ?: 0f
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val scrolly = t // 스크롤 뷰가 스크롤 된 정도

        // scrolly가 헤더의 초기 y포지션보다 클 경우, 헤더를 천장에 붙인다
        //따라서 ['스크롤 뷰가 스크롤 된 정도'가 '원래 있단 헤더의 y포지션' 보다 큰 경우] 라는 것은 [헤더가 천장을 넘어섰다], 라는 뜻이 된다.
        if (scrolly > mHeaderInitPosition) {
            userName?.visibility = View.VISIBLE

            stickHeader(scrolly - mHeaderInitPosition)  // stickHeader함수를 통해 천장에 붙인다
        } else {
            userName?.visibility = View.GONE

            freeHeader()    // freeHeader함수를 통해 천장에서 땐다
        }
    }

    // 천장에 붙이는 부분
    private fun stickHeader(position: Float) {
        // translationY는 뷰의 top포지션에 대한 상대적인 포지션
        // 파라미터로 들어온 포지션은 scrolly - mHeaderInitPosition인데, 이것은 [헤더를 넘어서서 스크롤 된만큼]을 의미
        // 즉, 헤더의 위치를 아래로 쭈우우우욱 늘려주는 것
        header?.translationY = position
        callStickListener()
    }

    private fun callStickListener() {
        // 붙어있지 않으면 -> 리스너 콜 -> flag를 true
        if (!mIsHeaderSticky) {
            stickListener(header ?: return)
            mIsHeaderSticky = true
        }
    }

    // 헤더의 translationY를 0으로 해서 복원
    private fun freeHeader() {
        header?.translationY = 0f
        callFreeListener()
    }

    private fun callFreeListener() {
        if (mIsHeaderSticky) {
            freeListener(header ?: return)
            mIsHeaderSticky = false
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        viewTreeObserver.removeOnGlobalLayoutListener(this)
    }
}