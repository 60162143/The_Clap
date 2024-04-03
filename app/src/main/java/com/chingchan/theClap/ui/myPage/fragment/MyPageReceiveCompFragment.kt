package com.chingchan.theClap.ui.myPage.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.chingchan.theClap.databinding.FragMypageRecCompBinding
import com.chingchan.theClap.ui.compliment.activity.ComplimentDetailActivity
import com.chingchan.theClap.ui.compliment.adapter.CompRecyclerAdapter
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.dialog.CompEditDialog
import com.chingchan.theClap.ui.myPage.activity.MyPageCompActivity
import com.chingchan.theClap.ui.myPage.adapter.MyPageRecCompRecyclerAdapter
import com.chingchan.theClap.ui.myPage.dialog.MyPageRecCompBlockDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageRecCompDeleteDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageRecCompEditDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageRecCompHideDialog
import com.chingchan.theClap.ui.myPage.dialog.MyPageWriteCompEditDialog

class MyPageReceiveCompFragment : Fragment() {

    private var _binding: FragMypageRecCompBinding? = null
    private lateinit var myPageCompActivity: MyPageCompActivity

    private var myPageRecCompRecyclerAdapter: MyPageRecCompRecyclerAdapter  = MyPageRecCompRecyclerAdapter() // 받은 칭찬 리사이클러뷰 어뎁터

    private val recCompEditDialog: MyPageRecCompEditDialog by lazy { MyPageRecCompEditDialog(myPageCompActivity) } // 받은 칭찬 수정 다이얼로그
    private val recCompHideDialog: MyPageRecCompHideDialog by lazy { MyPageRecCompHideDialog(myPageCompActivity) } // 게시물 숨기기 다이얼로그
    private val recCompBlockDialog: MyPageRecCompBlockDialog by lazy { MyPageRecCompBlockDialog(myPageCompActivity) } // 게시물 차단하기 다이얼로그
    private val recCompDeleteDialog: MyPageRecCompDeleteDialog by lazy { MyPageRecCompDeleteDialog(myPageCompActivity) } // 게시물 삭제하기 다이얼로그


    private var compDataList: ArrayList<CompData> = ArrayList<CompData>()

    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        myPageCompActivity = context as MyPageCompActivity
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
                // 받은 칭찬 게시글 리사이클러뷰
                rvComp.adapter = myPageRecCompRecyclerAdapter
                rvComp.itemAnimator = null

                // DiffUtil 적용 후 데이터 추가
                myPageRecCompRecyclerAdapter.differ.submitList(compDataList)

                // 게시글 클릭 리스너
                myPageRecCompRecyclerAdapter.clickListener = object : MyPageRecCompRecyclerAdapter.OnClickListener{
                    override fun onClick(position: Int, type: String) {
                        if(type == "USER_IMAGE"){   // 유저 프로필 이미지 클릭 시
                            Log.e("click!!", "USER_IMAGE")
                        }else if(type == "MORE"){  // 더보기 버튼 클릭 시
                            recCompEditDialog.show()
                        }else if(type == "DELETE"){  // 삭제 버튼 클릭 시
                            Log.e("click!!", "DELETE")

                            recCompDeleteDialog.show()
                        }else if(type == "HEART"){  // 게시글 좋아요 버튼 클릭 시
                            Log.e("click!!", "HEART")
                        }else if(type == "TOTAL"){  // 전체 영역 클릭 시
                            Log.e("click!!", "TOTAL")
                        }
                    }
                }
            }


            // 받은 칭찬 수정 다이얼로그 클릭 리스너
            recCompEditDialog.setListener(object : MyPageRecCompEditDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "HIDE"){
                        recCompEditDialog.dismiss()

                        recCompHideDialog.show()
                    }else if(type == "BLOCK"){
                        recCompEditDialog.dismiss()

                        recCompBlockDialog.show()
                    }else if(type == "CANCEL"){
                        recCompEditDialog.dismiss()
                    }
                }
            })

            // 게시물 숨기기 다이얼로그 클릭 리스너
            recCompHideDialog.setListener(object : MyPageRecCompHideDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "HIDE") {
                        Log.e("recCompHideDialog", "HIDE")
                        recCompHideDialog.dismiss()

                    }else if(type == "CANCEL"){
                        Log.e("recCompHideDialog", "CANCEL")
                        recCompHideDialog.dismiss()
                    }
                }
            })

            // 게시물 차단하기 다이얼로그 클릭 리스너
            recCompBlockDialog.setListener(object : MyPageRecCompBlockDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "BLOCK") {
                        Log.e("recCompBlockDialog", "BLOCK")
                    }else if(type == "CANCEL"){
                        Log.e("recCompBlockDialog", "CANCEL")
                        recCompBlockDialog.dismiss()
                    }
                }
            })

            // 게시물 삭제하기 다이얼로그 클릭 리스너
            recCompDeleteDialog.setListener(object : MyPageRecCompDeleteDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "DELETE") {
                        Log.e("recCompDeleteDialog", "DELETE")
                    }else if(type == "CANCEL"){
                        Log.e("recCompDeleteDialog", "CANCEL")
                        recCompDeleteDialog.dismiss()
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getCompData(){
        // 테스트 데이터
//        compDataList.add(CompData(id = 0, userId = 368, nickname = "홍길동", typeId = 0, typeName = "전체", title = "내 친구 OOO 는 정말 멋진 사람이에요1. ",
//            content = "착하기도 한데 친절하기도 한 것 같아. \n",
//            views = 23, likes = 30, createTs = "2023-12-01T13:22:57.155Z", modifyTs = "2023-12-05T14:25:57.155Z"))

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

    fun editCheck(chkFlag: Boolean){
        if(compDataList.isNotEmpty()) {
            // differ의 현재 리스트를 받아와서 newList에 넣기
            // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
            val newList = myPageRecCompRecyclerAdapter.differ.currentList.toMutableList()

            for ((index, data) in newList.withIndex()) {
                // 객체의 주소를 바꾸기 위해 copy 사용
                newList[index] = data.copy(editFlag = chkFlag)
            }

            // adapter의 differ.submitList()로 newList 제출
            // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
            myPageRecCompRecyclerAdapter.differ.submitList(newList.toMutableList())
        }
    }
}