package com.chingchan.theClap.ui.compliment

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.databinding.ActiCompBinding
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompData
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount

import java.util.Date

class ComplimentActivity : AppCompatActivity() {
    private lateinit var binding: ActiCompBinding
    private var compDataList: ArrayList<CompData> = ArrayList<CompData>()
    private lateinit var compRecyclerAdapter: CompRecyclerAdapter
    private lateinit var catRecyclerAdapter: CompCatRecyclerAdapter
    private val loginDialog: CompLoginDialog by lazy { CompLoginDialog(this) }
    private lateinit var compCatDataList: ArrayList<CompCatData>
    private var curCategoryPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiCompBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compCatDataList = GlobalFunction.getSerializableExtra(intent, "compCatData", ArrayList<CompCatData>()::class.java)!!

        // 테스트 데이터
        compDataList.add(CompData(id = 0, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
            content = "착하기도 한데 친절하기도 한 것 같아. \n",
            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))

        compDataList.add(CompData(id = 1, nickname = "김아무개", typeId = 1, typeName = "인성/성품", title = "내 친구 OOO 는 정말 멋진 사람이에요2. ",
            content = "착하기도 한데 친절하기도 한 것 같아. \n" +
                    "그런데 웃는 모습까지도 예쁘니 친구들 한테 인기가 많아서 너무 부러워...\n" +
                    "그런데 웃는 모습까지도 예쁘니 친구들 한테 인기가 많아서 너무 부러워...\n" +
                    "그런데 웃는 모습까지도 예쁘니 친구들 한테 인기가 많아서 너무 부러워...\n" +
                    "그런데 웃는 모습까지도 예쁘니 친구들 한테 인기가 많아서 너무 부러워...\n" +
                    "그런데 웃는 모습까지도 예쁘니 친구들 한테 인기가 많아서 너무 부러워...",
            views = 3, likes = 4, createTs = "2023-11-28T13:22:57.155Z", modifyTs = "2023-12-06T13:22:57.155Z"))

        with(binding){
            catRecyclerAdapter = CompCatRecyclerAdapter()
            rvCat.adapter = catRecyclerAdapter

            // 카테고리 '전체' 선택 디폴트
            compCatDataList.get(0).isSelect = true

            // DiffUtil 적용 후 데이터 추가
            catRecyclerAdapter.differ.submitList(compCatDataList)

            catRecyclerAdapter.catClickListener = object : CompCatRecyclerAdapter.OnCatClickListener{
                override fun onCatClick(position: Int) {
                    // 서버에서 카테고리 별로 데이터 가져와야 함
                    getCompData(compCatDataList[position].boardTypeId)



                    // differ의 현재 리스트를 받아와서 newList에 넣기
                    // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                    val newList = catRecyclerAdapter.differ.currentList.toMutableList()

                    // 이전 선택되어 있는 카테고리
                    // 객체의 주소를 바꾸기 위해 copy 사용
                    val newPreviousData = newList[curCategoryPosition].copy(isSelect = false)

                    // 새로 선택된 카테고리
                    // 객체의 주소를 바꾸기 위해 copy 사용
                    val newPostData = newList[position].copy(isSelect = true)

                    // 기존 데이터 값 갱신
                    compCatDataList?.get(curCategoryPosition)?.isSelect = false
                    compCatDataList?.get(position)?.isSelect = true

                    // 새로운 객체로 저장
                    newList[curCategoryPosition] = newPreviousData
                    newList[position] = newPostData

                    curCategoryPosition = position  // 현재 선택되어 있는 카테고리의 index Update

                    // adapter의 differ.submitList()로 newList 제출
                    // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                    catRecyclerAdapter.differ.submitList(newList.toMutableList())
                }
            }

            // 게시글 리사이클러뷰
            compRecyclerAdapter = CompRecyclerAdapter()
            rvComp.adapter = compRecyclerAdapter

            // DiffUtil 적용 후 데이터 추가
            compRecyclerAdapter.differ.submitList(compDataList)

            // 게시글 클릭 리스너
            compRecyclerAdapter.clickListener = object : CompRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "USER_IMAGE"){
                        Log.e("click!!", "user_image")
                    }else if(type == "HEART"){
                        Log.e("click!!", "heart")
                    }else if(type == "SHARE"){
                        Log.e("click!!", "share")
                    }else if(type == "TOTAL"){
                        Log.e("click!!", "total")
                    }
                }
            }

//            var loginDialog = CompLoginDialog(this)
//            loginDialog.listener
//
//            // 게시글 클릭 리스너
//            loginDialog.setoncli = object : CompLoginDialog.OnClickListener{
//                override fun onClick(position: Int, type: String) {
//                    if(type == "USER_IMAGE"){
//                        Log.e("click!!", "user_image")
//                    }else if(type == "HEART"){
//                        Log.e("click!!", "heart")
//                    }else if(type == "SHARE"){
//                        Log.e("click!!", "share")
//                    }else if(type == "TOTAL"){
//                        Log.e("click!!", "total")
//                    }
//                }
//            }

            // 북마크 버튼 클릭 리스너
            btnBookmark.setOnClickListener(View.OnClickListener {
                btnBookmark.isSelected = !btnBookmark.isSelected
            })

            // 더보기 버튼 클릭 리스너
            btnMore.setOnClickListener(View.OnClickListener {

            })




        }

    }

    override fun onResume() {
        super.onResume()


    }

    fun getCompData(boardTypeId: Int) {

    }
}