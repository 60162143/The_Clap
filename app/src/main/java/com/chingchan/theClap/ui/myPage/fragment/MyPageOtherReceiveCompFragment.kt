package com.chingchan.theClap.ui.myPage.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chingchan.theClap.databinding.FragMypageRecCompBinding
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.myPage.activity.MyPageCompActivity
import com.chingchan.theClap.ui.myPage.activity.MyPageProfileOtherActivity
import com.chingchan.theClap.ui.myPage.adapter.MyPageOtherRecCompRecyclerAdapter
import com.chingchan.theClap.ui.myPage.adapter.MyPageRecCompRecyclerAdapter
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherRecCompBlockDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherRecCompEditDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageOtherRecCompHideDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageRecCompBlockDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageRecCompDeleteDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageRecCompEditDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageRecCompHideDialog

class MyPageOtherReceiveCompFragment : Fragment() {

    private var _binding: FragMypageRecCompBinding? = null
    private lateinit var myPageProfileOtherActivity: MyPageProfileOtherActivity

    // 타인이 받은 칭찬 리사이클러뷰 어뎁터
    private var myPageOtherRecCompRecyclerAdapter: MyPageOtherRecCompRecyclerAdapter = MyPageOtherRecCompRecyclerAdapter()

    private val recOtherCompEditDialog: MyPageOtherRecCompEditDialog by lazy { MyPageOtherRecCompEditDialog(myPageProfileOtherActivity) } // 타인이 받은 칭찬 수정 다이얼로그
    private val recOtherCompHideDialog: MyPageOtherRecCompHideDialog by lazy { MyPageOtherRecCompHideDialog(myPageProfileOtherActivity) } // 타인이 받은 칭찬 게시물 숨기기 다이얼로그
    private val recOtherCompBlockDialog: MyPageOtherRecCompBlockDialog by lazy { MyPageOtherRecCompBlockDialog(myPageProfileOtherActivity) } // 타인이 받은 칭찬 게시물 차단하기 다이얼로그


    private var compDataList: ArrayList<CompData> = ArrayList<CompData>()

    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        myPageProfileOtherActivity = context as MyPageProfileOtherActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragMypageRecCompBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getCompData()   // 서버에서 데이터 받아와야 함

        with(binding){

            // 데이터가 없을 경우 데이터가 없다는 텍스트 보이기
            if(compDataList.isEmpty()){
                rvComp.visibility = View.GONE
                textEmpty.visibility = View.VISIBLE
            }else{
                rvComp.visibility = View.VISIBLE
                textEmpty.visibility = View.GONE
            }

            // 타인이 받은 칭찬 게시글 리사이클러뷰
            rvComp.adapter = myPageOtherRecCompRecyclerAdapter
            rvComp.itemAnimator = null

            // DiffUtil 적용 후 데이터 추가
            myPageOtherRecCompRecyclerAdapter.differ.submitList(compDataList.toMutableList())

            // 게시글 클릭 리스너
            myPageOtherRecCompRecyclerAdapter.clickListener = object : MyPageOtherRecCompRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "USER_IMAGE"){   // 유저 프로필 이미지 클릭 시
                        Log.e("click!!", "USER_IMAGE")
                    }else if(type == "MORE"){  // 더보기 버튼 클릭 시
                        Log.e("click!!", "MORE")

                        recOtherCompEditDialog.show()

                    }else if(type == "HEART"){  // 게시글 좋아요 버튼 클릭 시
                        Log.e("click!!", "HEART")
                    }else if(type == "TOTAL"){  // 전체 영역 클릭 시
                        Log.e("click!!", "TOTAL")
                    }
                }
            }


            // 타인이 받은 칭찬 수정 다이얼로그 클릭 리스너
            recOtherCompEditDialog.setListener(object : MyPageOtherRecCompEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "HIDE"){
                        Log.e("recOtherCompEditDialog", "HIDE")

                        recOtherCompEditDialog.dismiss()
                        recOtherCompHideDialog.show()
                    }else if(type == "BLOCK"){
                        Log.e("recOtherCompEditDialog", "BLOCK")

                        recOtherCompEditDialog.dismiss()
                        recOtherCompBlockDialog.show()
                    }else if(type == "CANCEL"){
                        Log.e("recOtherCompEditDialog", "CANCEL")
                        recOtherCompEditDialog.dismiss()
                    }
                }
            })

            // 타인이 받은 칭찬 게시물 숨기기 다이얼로그 클릭 리스너
            recOtherCompHideDialog.setListener(object : MyPageOtherRecCompHideDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "HIDE") {
                        Log.e("recOtherCompHideDialog", "HIDE")
                    }else if(type == "CANCEL"){
                        Log.e("recOtherCompHideDialog", "CANCEL")
                        recOtherCompHideDialog.dismiss()
                    }
                }
            })

            // 타인이 받은 칭찬 게시물 차단하기 다이얼로그 클릭 리스너
            recOtherCompBlockDialog.setListener(object : MyPageOtherRecCompBlockDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "BLOCK") {
                        Log.e("recOtherCompBlockDialog", "BLOCK")
                    }else if(type == "CANCEL"){
                        Log.e("recOtherCompBlockDialog", "CANCEL")
                        recOtherCompBlockDialog.dismiss()
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    // 받은 칭찬 조회
    // 나중에 구현하자!
    private fun getCompData(){
        compDataList.clear()    // 데이터 초기화


        // 테스트 데이터
//        compDataList.add(CompData(id = 0, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))
//
//        compDataList.add(CompData(id = 1, nickname = "김아무개", typeId = 1, typeName = "인성/성품", title = "내 친구 OOO 는 정말 멋진 사람이에요2. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n" +
//                    "그런데 웃는 모습까지도 예쁘니 친구들 한테 인기가 많아서 너무 부러워...\n" +
//                    "그런데 웃는 모습까지도 예쁘니 친구들 한테 인기가 많아서 너무 부러워...\n" +
//                    "그런데 웃는 모습까지도 예쁘니 친구들 한테 인기가 많아서 너무 부러워...\n" +
//                    "그런데 웃는 모습까지도 예쁘니 친구들 한테 인기가 많아서 너무 부러워...\n" +
//                    "그런데 웃는 모습까지도 예쁘니 친구들 한테 인기가 많아서 너무 부러워...",
//            views = 3, likes = 4, createTs = "2023-11-28T13:22:57.155Z", modifyTs = "2023-12-06T13:22:57.155Z"))
//
//        compDataList.add(CompData(id = 2, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))
//        compDataList.add(CompData(id = 3, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))
//        compDataList.add(CompData(id = 4, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))
//        compDataList.add(CompData(id = 5, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))
//        compDataList.add(CompData(id = 6, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))
//        compDataList.add(CompData(id = 7, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))
//        compDataList.add(CompData(id = 8, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))
//        compDataList.add(CompData(id = 9, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))
//        compDataList.add(CompData(id = 10, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))

    }

    fun refreshData(){
        // 서버에서 데이터 받기

        // 받은 데이터 리사이클러뷰에 넣기

        // adapter의 differ.submitList()로 newList 제출
        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
        myPageOtherRecCompRecyclerAdapter.differ.submitList(null)

        getCompData()

        with(binding){
            // 데이터가 없을 경우 데이터가 없다는 텍스트 보이기
            if(compDataList.isEmpty()){
                rvComp.visibility = View.GONE
                textEmpty.visibility = View.VISIBLE
            }else{
                rvComp.visibility = View.VISIBLE
                textEmpty.visibility = View.GONE
            }
        }

        myPageOtherRecCompRecyclerAdapter.differ.submitList(compDataList.toMutableList())

        // 상위 엑티비티에 새로고침이 완료된 Flag SET
        myPageProfileOtherActivity.isReceiveRefreshComplete = true
    }

    fun redraw(){
        // adapter의 differ.submitList()로 newList 제출
        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영

        myPageOtherRecCompRecyclerAdapter.differ.submitList(compDataList.toMutableList())

        if(compDataList.isNotEmpty()){
            binding.rvComp.smoothScrollToPosition(0)
        }

        binding.rvComp.visibility = View.VISIBLE
    }

    fun eraseDraw(){
        // adapter의 differ.submitList()로 newList 제출
        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
        myPageOtherRecCompRecyclerAdapter.differ.submitList(null)

        binding.rvComp.visibility = View.GONE
    }
}