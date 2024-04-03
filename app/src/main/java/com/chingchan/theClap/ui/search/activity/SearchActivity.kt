package com.chingchan.theClap.ui.search.activity

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.chingchan.theClap.databinding.ActiSearchBinding
import com.chingchan.theClap.ui.search.adapter.SearchRecyclerAdapter
import com.chingchan.theClap.ui.search.adapter.SearchVpAdapter
import com.chingchan.theClap.ui.search.data.SearchContData
import com.chingchan.theClap.ui.search.data.SearchData
import com.chingchan.theClap.ui.toast.customToast
import com.google.android.material.tabs.TabLayoutMediator
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActiSearchBinding

    private lateinit var searchRecyclerAdapter : SearchRecyclerAdapter
    private lateinit var searchVpAdapter: SearchVpAdapter

    private lateinit var searchDataList: ArrayList<SearchContData>

    private val tabTitle = arrayOf("칭찬글", "사용자")

    // 검색어 자동 저장 여부
    private var isAutoSave: Boolean = false
    private var isUserFragDraw: Boolean = false
    private var curSchData: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){

            searchVpAdapter = SearchVpAdapter(this@SearchActivity)
            vpSearch.adapter = searchVpAdapter

            TabLayoutMediator(tabLayout, vpSearch){ tab, position -> tab.text = tabTitle[position] }.attach()

            // 상위 Context Activity Set
            searchVpAdapter.getSearchCompFragment().setActivity(this@SearchActivity)
            searchVpAdapter.getSearchUserFragment().setActivity(this@SearchActivity)

            // ViewPager2의 페이지 변경 이벤트를 수신합니다.
            vpSearch.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    // 스와이프를 감지합니다.

                    if(vpSearch.currentItem == 1 && !isUserFragDraw){
                        isUserFragDraw = true
                        searchVpAdapter.getSearchUserFragment().setKeyword(curSchData)
                    }
                }
            })

            searchRecyclerAdapter = SearchRecyclerAdapter()
            rvSearch.adapter = searchRecyclerAdapter
            rvSearch.itemAnimator = null

            isAutoSave = SearchData.getAutoSave(applicationContext)

            if(isAutoSave){
                searchDataList = SearchData.getSearchData(applicationContext)
            }

            setAutoSaveLayout(isAutoSave)   // 검색어 자동 저장 여부에 따른 레이아웃 설정

            // 검색어 삭제 버튼 클릭 리스너
            searchRecyclerAdapter.clickListener = object : SearchRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "DELETE"){   // 삭제

                        SearchData.removeSearchData(applicationContext, position)

                        searchDataList.removeAt(position)

                        setAutoSaveLayout(isAutoSave)
                    }
                }
            }


            // 검색어 입력 칸 입력 텍스트 변화 확인 리스너
            editSch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {

                    setSearchLayout(false)  // 검색어 입력화면으로 전환

                    if(editSch.text.isNotEmpty()){
                        btnDelete.visibility = View.VISIBLE
                    }else{
                        btnDelete.visibility = View.GONE
                    }

                    return
                }
            })


            // editText에서 완료 클릭 시
            editSch.setOnEditorActionListener { v, actionId, event ->
                var handled = false

                // 텍스트가 입력되어 있고 완료 버튼 클릭 시
                if (editSch.text.isNotEmpty() && actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if(isAutoSave){
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("MM.dd")
                        val formattedDate = current.format(formatter)

                        val schData = SearchContData(getRandomString(10)
                                , editSch.text.toString()
                                , formattedDate)

                        // 검색어 데이터 추가
                        SearchData.setSearchData(applicationContext, schData)


                        searchDataList.add(0, schData)  // 데이터 추가

                        setAutoSaveLayout(isAutoSave)   // 검색어 자동 저장 여부에 따른 레이아웃 설정
                    }

                    curSchData = editSch.text.toString()

                    // 입력한 검색어 제거
                    editSch.text.clear()
                    btnDelete.visibility = View.GONE    // 검색어 삭제 버튼 제거

                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                    getSearchData(curSchData)    // 서버 작업 해야 함

                    handled = true
                }else{
                    customToast.showCustomToast("검색어를 입력해주세요.", this@SearchActivity)
                }
                handled
            }


            // 검색어 삭제 버튼 클릭 리스너
            btnDelete.setOnClickListener(View.OnClickListener {
                // 입력한 검색어 제거
                editSch.text.clear()

                setSearchLayout(false)  // 검색어 입력화면으로 전환
            })

            // 전체 삭제 버튼 클릭 리스너
            btnAllDelete.setOnClickListener(View.OnClickListener {
                SearchData.removeAll(applicationContext)

                searchDataList.clear()

                setAutoSaveLayout(isAutoSave)
            })

            // 자동 저장 버튼 클릭 리스너
            btnAutoSave.setOnClickListener(View.OnClickListener {
                isAutoSave = !isAutoSave

                SearchData.setAutoSave(applicationContext, isAutoSave)

                // 자동저장 켜져있을 경우 데이터 가져오기
                if(isAutoSave){
                    customToast.showCustomToast("자동저장 기능이 켜졌습니다.", this@SearchActivity)

                    searchDataList = SearchData.getSearchData(applicationContext)
                }else{
                    customToast.showCustomToast("자동저장 기능이 꺼졌습니다.", this@SearchActivity)
                }

                setAutoSaveLayout(isAutoSave)
            })

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                finish()
            })
        }
    }


    // 검색어 자동 저장 여부에 따른 레이아웃 설정
    private fun setAutoSaveLayout(tfAutoSave: Boolean){

        with(binding){
            if(tfAutoSave){

                layoutAllDelete.visibility = View.VISIBLE
                btnAutoSave.text = "자동 저장 끄기"

                if(searchDataList.isNotEmpty()){
                    rvSearch.visibility = View.VISIBLE
                    textEmpty.visibility = View.GONE

                    searchRecyclerAdapter.differ.submitList(null)
                    searchRecyclerAdapter.differ.submitList(searchDataList.toMutableList())
                }else{
                    rvSearch.visibility = View.GONE
                    textEmpty.visibility = View.VISIBLE
                    textEmpty.text = "검색 내역이 없습니다."
                }

            }else{
                rvSearch.visibility = View.GONE
                layoutAllDelete.visibility = View.GONE
                textEmpty.visibility = View.VISIBLE

                btnAutoSave.text = "자동 저장 켜기"
                textEmpty.text = "검색어 자동저장 기능이 꺼져있습니다."
            }
        }

    }

    private fun getRandomString(length: Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    // 검색 데이터 조회
    private fun getSearchData(schData: String){
        // 탭 레이아웃의 Fragment에 변화 알림
        searchVpAdapter.getSearchCompFragment().setKeyword(schData)

        // 유저 검색 Fragment가 그려졌을 경우만 호출
        if(isUserFragDraw){
            searchVpAdapter.getSearchUserFragment().setKeyword(schData)
        }

        setSearchLayout(true)
    }

    // 검색어 입력 화면 전환
    private fun setSearchLayout(isSearch: Boolean){
        with(binding){
            if(isSearch){
                layoutSchDetail.visibility = View.GONE
                layoutSchTab.visibility = View.VISIBLE
            }else{
                layoutSchDetail.visibility = View.VISIBLE
                layoutSchTab.visibility = View.GONE
            }
        }
    }

    // 화면 터치시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }
}