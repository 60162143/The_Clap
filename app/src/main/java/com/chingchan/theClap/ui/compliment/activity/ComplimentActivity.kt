package com.chingchan.theClap.ui.compliment.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chingchan.theClap.databinding.ActiCompBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.adapter.CompCatRecyclerAdapter
import com.chingchan.theClap.ui.compliment.adapter.CompRecyclerAdapter
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompData
import com.chingchan.theClap.ui.compliment.data.CompDetResData
import com.chingchan.theClap.ui.compliment.data.CompOneResData
import com.chingchan.theClap.ui.compliment.data.CompResData
import com.chingchan.theClap.ui.compliment.dialog.CompLoginDialog
import com.chingchan.theClap.ui.compliment.dialog.CompNewWriteDialog
import com.chingchan.theClap.ui.compliment.dialog.CompWriteFloatBtnDialog
import com.chingchan.theClap.ui.login.activity.LoginActivity
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.activity.MyPageProfileOtherActivity
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.toast.customToast
import kotlinx.coroutines.Job
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ComplimentActivity : AppCompatActivity() {
    private lateinit var binding: ActiCompBinding

    private lateinit var compRecyclerAdapter: CompRecyclerAdapter   // 게시글 리사이클러뷰 어뎁터
    private lateinit var catRecyclerAdapter: CompCatRecyclerAdapter // 카테고리 리사이클러뷰 어뎁터

    private val loginDialog: CompLoginDialog by lazy { CompLoginDialog(this) }  // 로그인 다이얼로그
    private val newWriteDialog: CompNewWriteDialog by lazy { CompNewWriteDialog(this) } // 새글 작성 다이얼로그
    private val writeFloatBtnDialog: CompWriteFloatBtnDialog by lazy { CompWriteFloatBtnDialog(this) }  // 칭찬글 작성 다이얼로그

    private var compDataList: ArrayList<CompData> = ArrayList()
    private lateinit var compCatDataList: ArrayList<CompCatData>

    private var curCategoryPosition = 0 // 현재 선택된 카테고리 position
    private var curCategoryTypeId: Int? = 0 // 현재 선택된 카테고리 타입 ID

    private var curCompPosition = -1 // 현재 선택된 게시글 position
    private var curCompPage = 0 // 현재 게시글 페이지
    private var isCompPageLast = true  // 현제 게시글이 페이지가 마지막인지 여부

    private var isUserWriteComp = false  // 유저가 작성한 게시글이 있는지 여부

    private var showShimmer: Job? = null    // Shimmer Coroutine 생명주기 제어 변수

    // 게시글 작성 Intent registerForActivityResult
    private val writeResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 3000) {    // 3000일 경우 글 작성 완료
            val compWriteData = GlobalFunction.getParcelableExtra(result.data!!, "compWriteData", CompData::class.java)!!    // 게시글 데이터

            curCompPosition = -1    // 게시글을 누른게 아닌 추가이기 때문에 position이 없음

            // 게시글 상세 화면으로 화면 전환
            val intent = Intent(this, ComplimentDetailActivity::class.java)
            intent.putParcelableArrayListExtra("compCatData", compCatDataList)  // 카테고리 리스트 데이터
            intent.putExtra("compData", compWriteData)  // 게시글 데이터
            detailResult.launch(intent)
        }
    }

    // 게시글 상세화면 Intent registerForActivityResult
    private val detailResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == 1000) {    // 뒤로가기 버튼으로 왔을 경우
            // 게시글을 클릭해서 들어갔을 경우만 동작
            // 게시글 작성 시에는 게시글이 추가되므로 Position이 없음 -> 단건 조회 필요 X
            if(curCompPosition != -1){
                getOneCompData(result.data?.getIntExtra("boardId", 0)!!)    // 게시글 단건 조회
            }else{
                clearAndSetData(curCategoryPosition)   // 데이터 초기화
            }
        }else if(result.resultCode == 1001){    // 게시글 숨김, 삭제, 차단일 경우
            clearAndSetData(curCategoryPosition)   // 데이터 초기화
        }else if(result.resultCode == 1002){    // 게시글 신고일 경우
            clearAndSetData(curCategoryPosition)   // 데이터 초기화
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiCompBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compCatDataList = GlobalFunction.getSerializableExtra(intent, "compCatData", ArrayList<CompCatData>()::class.java)!!    // 카테고리 리스트 데이터

        // 현재 선택되어 있는 카테고리 Type Id SET
        curCategoryTypeId = intent.getIntExtra("boardTypeId", 0).let {
            compCatDataList[it].isSelect = true // 현재 선택된 카테고리 Flag 설정
            curCategoryPosition = it    // 현재 선택된 카테고리 position

            when (it) {
                0 -> null   // 카테고리가 '전체'일 경우 null
                else -> it  // '전체' 이외의 카테고리일 경우 boardTypeId
            }
        }

        // 로그인 되어 있으면 유저가 작성한 게시글이 있는지 확인
        if(isLoginCheck()){
            isUserWriteComp(UserData.getUserId(applicationContext)) // 게시글을 1개라도 작성했는지 체크
        }

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){
            // 카테고리 리사이클러뷰
            catRecyclerAdapter = CompCatRecyclerAdapter()
            rvCat.adapter = catRecyclerAdapter

            rvCat.scrollToPosition(curCategoryPosition) // 현재 선택되어 있는 카테고리로 스크롤 이동

            // DiffUtil 적용 후 데이터 추가
            catRecyclerAdapter.differ.submitList(compCatDataList.toMutableList())

            // 카테고리 리사이클러뷰 클릭 리스너
            catRecyclerAdapter.clickListener = object : CompCatRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {

                    clearAndSetData(position)   // 데이터 초기화

                    // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                    val newList = catRecyclerAdapter.differ.currentList.toMutableList() // 카테고리 데이터

                    // 객체의 주소를 바꾸기 위해 copy 사용
                    val newPreviousData = newList[curCategoryPosition].copy(isSelect = false)   // 이전 선택되어 있는 카테고리 Flag 변경

                    // 객체의 주소를 바꾸기 위해 copy 사용
                    val newPostData = newList[position].copy(isSelect = true)   // 새로 선택된 카테고리 Flag 변경

                    // 기존 데이터 값 갱신
                    compCatDataList[curCategoryPosition].isSelect = false
                    compCatDataList[position].isSelect = true

                    // 새로운 객체로 저장
                    newList[curCategoryPosition] = newPreviousData
                    newList[position] = newPostData

                    curCategoryPosition = position  // 현재 선택되어 있는 카테고리의 index Update
                    curCategoryTypeId = compCatDataList[position].boardTypeId   // 현재 선택되어 있는 카테고리의 typeId

                    // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                    catRecyclerAdapter.differ.submitList(newList.toMutableList())
                }
            }

            // 게시글 리사이클러뷰
            compRecyclerAdapter = CompRecyclerAdapter()
            rvComp.adapter = compRecyclerAdapter
            rvComp.itemAnimator = null  // 깜빡거리는 애니메이션 제거

            getCompData(curCategoryTypeId, curCompPage)   // 게시글 목록 조회

            // 리사이클러뷰의 마지막 Item이 완전히 보일 떄 호출
            rvComp.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    // 리사이클러뷰 아이템 위치 찾기, 아이템 위치가 완전히 보일때 호출됨
                    val rvPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()

                    // 리사이클러뷰 아이템 총개수 (index 접근 이기 때문에 -1)
                    val totalCount =
                        recyclerView.adapter?.itemCount?.minus(1)

                    // 페이징이 가능한 데이터 수일 경우 페이징 처리
                    if (totalCount != null) {
                        if(curCompPage > 0 && rvPosition == totalCount && !isCompPageLast){
                            getCompData(curCategoryTypeId, curCompPage)   // 게시글 목록 추가 조회
                        }
                    }
                }
            })

            // 게시글 클릭 리스너
            compRecyclerAdapter.clickListener = object : CompRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(!isLoginCheck()){    // 로그인을 하지 않았을 경우
                        loginDialog.show()  // 로그인 다이얼로그 띄우기
                    }else if(!isUserWriteComp){ // 게시글을 1개라도 작성하지 않은 경우
                        newWriteDialog.show()   // 게시글 작성 다디얼로그 띄우기
                    }else{
                        if(type == "USER_IMAGE"){   // 유저 프로필 이미지 클릭 시
                            curCompPosition = position  // 현재 누른 위치의 position 갱신

                            val newList = compRecyclerAdapter.differ.currentList.toMutableList()    // 게시글 데이터

                            getProfileData(newList[curCompPosition].userId) // 프로필 데이터 조회
                        }else if(type == "HEART"){  // 게시글 좋아요 버튼 클릭 시
                            if(UserData.getLoginType(applicationContext) == "GUEST"){
                                customToast.showCustomToast("로그인을 진행해주세요.", this@ComplimentActivity)
                            }else{
                                curCompPosition = position  // 현재 누른 위치의 position 갱신

                                likeComp(compDataList[position].id)  // 좋아요 등록 / 등록 해제
                            }
                        }else if(type == "TOTAL"){  // 게시글 클릭 시
                            curCompPosition = position  // 현재 누른 위치의 position 갱신
                            increaseView(compDataList[position])  // 조회 수 증가
                        }
                    }
                }
            }

            // 로그인 다이얼로그 클릭 리스너
            loginDialog.setListener(object : CompLoginDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "KAKAO" || type == "NAVER" || type == "GOOGLE"){ // 소셜 아이콘 클릭 시
                        loginDialog.dismiss()   // 다이얼로그 닫기

                        doLogin()   // 로그인 화면으로 이동
                    }else if(type == "CANCEL"){ // 취소 버튼 클릭 시
                        loginDialog.dismiss()   // 다이얼로그 닫기
                    }
                }
            })

            // 칭찬글 새로 작성 다이얼로그 클릭 리스너
            newWriteDialog.setListener(object : CompNewWriteDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "FRIEND"){   // 지인 칭찬 버튼 클릭 시
                        newWriteDialog.dismiss()    // 다이얼로그 닫기

                        goWriteActivity()   // 게시글 작성 화면으로 이동
                    }else if(type == "CANCEL"){ // 취소 버튼 클릭 시
                        newWriteDialog.dismiss()    // 다이얼로그 닫기
                    }
                }
            })

            // 글쓰기 플로팅 버튼 다이얼로그 클릭 리스너
            writeFloatBtnDialog.setListener(object : CompWriteFloatBtnDialog.OnClickListener{
                override fun onClick(type: String) {
                    if(type == "FRIEND"){   // 지인 칭찬 버튼 클릭 시
                        writeFloatBtnDialog.dismiss()   // 다이얼로그 닫기

                        goWriteActivity()   // 게시글 작성 화면으로 이동
                    }
                }
            })

            // 글쓰기 플로팅 버튼 클릭 리스너
            btnWrite.setOnClickListener(View.OnClickListener {
                if(UserData.getLoginType(applicationContext) == "GUEST"){
                    customToast.showCustomToast("로그인을 진행해주세요.", this@ComplimentActivity)
                }else{
                    goWriteActivity()   // 게시글 작성 화면으로 이동
                }
            })

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                finish()    // 종료
            })
        }
    }

    // 게시글 데이터 조회
    fun getCompData(boardTypeId: Int?, curPage: Int) {
        val apiObjectCall = ApiObject.getCompService.getComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , typeId = boardTypeId, page = curPage, size = 10)

        apiObjectCall.enqueue(object: Callback<CompResData> {
            override fun onResponse(call: Call<CompResData>, response: Response<CompResData>) {
                if(response.isSuccessful) { // 응답 성공 시
                    val newCompDataList = response.body()?.data?.content!!  // 새로 페이징된 게시글 데이터

                    if(newCompDataList.isEmpty()){  // 페이징된 게시글 데이터가 없을 경우
                        binding.rvComp.visibility = View.GONE   // 게시글 리사이클러뷰 숨기기
                        binding.textEmpty.visibility = View.VISIBLE // 데이터가 없다는 텍스트 보이기
                    }else{
                        binding.rvComp.visibility = View.VISIBLE    // 게시글 리사이클러뷰 보이기
                        binding.textEmpty.visibility = View.GONE    // 데이터가 없다는 텍스트 숨기기
                    }

                    // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                    val newList = compRecyclerAdapter.differ.currentList.toMutableList()

                    if(newList.isNotEmpty()){   // 기존 페이징된 게시글 있을 경우
                        // 기존 페이징된 게시글에 새로 페이징된 게시글 추가
                        compRecyclerAdapter.differ.submitList(newList.toMutableList() + newCompDataList.toMutableList())
                    }else{  // 기존 페이징된 게시글이 없을 경우
                        compRecyclerAdapter.differ.submitList(newCompDataList.toMutableList())  // 새로 페이징된 게시글 추가
                    }

                    compDataList += newCompDataList // 기존 페이징된 게시글에 새로 페이징된 게시글 추가

                    // 게시글 데이터가 있고 첫번째 페이지일 경우 맨 위로 스크롤
                    if(compDataList.isNotEmpty() && curPage == 0){
                        binding.rvComp.smoothScrollToPosition(0)
                    }

                    isCompPageLast = response.body()?.data!!.isLast  // 마지막 페이지인지 여부

                    // 마지막 페이지가 아닐 경우 페이지 +1
                    if(!isCompPageLast){
                        curCompPage += 1 // 다음 페이지 +1
                    }

                }else{
                    customToast.showCustomToast("게시글 목록을 불러오지 못했습니다.", this@ComplimentActivity)
                }
            }

            override fun onFailure(call: Call<CompResData>, t: Throwable) {
                customToast.showCustomToast("인터넷 연결을 확인해 주세요.", this@ComplimentActivity)
            }
        })
    }

    // 로그인 되어 있는지 체크
    fun isLoginCheck(): Boolean{
        return ((UserData.getUserId(applicationContext) != 0))  // 유저 ID가 있는지 확인
    }

    // 로그인 화면으로 이동
    fun doLogin(){
        val intent = Intent(this@ComplimentActivity, LoginActivity::class.java)
        startActivity(intent)
    }

    // 게시글 조회수 증가
    fun increaseView(compData: CompData){
        // 본인이 작성한 글일 경우 조회수 증가 X
        if(compData.userId != UserData.getUserId(applicationContext)){
            val apiObjectCall = ApiObject.getCompService.increaseView(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
                , boardId = compData.id)

            apiObjectCall.enqueue(object: Callback<CompDetResData> {
                override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
                    if(response.isSuccessful) { // 응답 성공 시
                        val status = response.body()?.status!!

                        if(status == "success"){
                            // 게시글 상세 Activity로 화면 전환
                            val intent = Intent(this@ComplimentActivity, ComplimentDetailActivity::class.java)
                            intent.putParcelableArrayListExtra("compCatData", compCatDataList)
                            compData.views = compData.views + 1
                            intent.putExtra("compData", compData)
                            detailResult.launch(intent)
                        }else{
                            customToast.showCustomToast("게시글 조회수 증가가 되지 못했습니다.", this@ComplimentActivity)
                        }

                    }else{
                        customToast.showCustomToast("게시글 조회수 증가가 되지 못했습니다.", this@ComplimentActivity)
                    }
                }

                override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                    customToast.showCustomToast("Call Failed", this@ComplimentActivity)
                }
            })
        }else{
            // 게시글 상세 Activity로 화면 전환
            val intent = Intent(this@ComplimentActivity, ComplimentDetailActivity::class.java)
            intent.putParcelableArrayListExtra("compCatData", compCatDataList)
            intent.putExtra("compData", compData)
            detailResult.launch(intent)
        }
    }

    // 게시글 단건 조회
    private fun getOneCompData(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.getOneComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompOneResData> {
            override fun onResponse(call: Call<CompOneResData>, response: Response<CompOneResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compRecyclerAdapter.differ.currentList.toMutableList()

                        newList[curCompPosition] = response.body()?.data

                        compDataList[curCompPosition] = response.body()?.data!!


                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        compRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 조회를 못했습니다.", this@ComplimentActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 조회를 못했습니다.", this@ComplimentActivity)
                }
            }

            override fun onFailure(call: Call<CompOneResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentActivity)
            }
        })
    }

    // 게시글을 1개라도 작성했는지 체크
    private fun isUserWriteComp(userId: Int){
        val apiObjectCall = ApiObject.getCompService.getComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , userId = userId, page = 0, size = 1)

        apiObjectCall.enqueue(object: Callback<CompResData> {
            override fun onResponse(call: Call<CompResData>, response: Response<CompResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    isUserWriteComp = response.body()?.data?.content!!.isNotEmpty()     // 유저가 작성한 게시글이 있는지 확인

                }else{
                    customToast.showCustomToast("게시글 작성여부를 불러오지 못했습니다.", this@ComplimentActivity)
                }
            }

            override fun onFailure(call: Call<CompResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentActivity)
            }
        })
    }

    // 게시글 좋아요 등록 / 등록 해제
    private fun likeComp(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.likeComp(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompDetResData> {
            override fun onResponse(call: Call<CompDetResData>, response: Response<CompDetResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        // differ의 현재 리스트를 받아와서 newList에 넣기
                        // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                        val newList = compRecyclerAdapter.differ.currentList.toMutableList()

                        var isLikeComp = !newList[curCompPosition].like
                        var heartCnt = newList[curCompPosition].likes

                        if(isLikeComp){
                            heartCnt += 1
                        }else{
                            heartCnt -= 1
                        }

                        var newData = newList[curCompPosition].copy(like = isLikeComp, likes = heartCnt)

                        newList[curCompPosition] = newData
                        compDataList[curCompPosition] = newData

                        // adapter의 differ.submitList()로 newList 제출
                        // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                        compRecyclerAdapter.differ.submitList(newList.toMutableList())
                    }else{
                        customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", this@ComplimentActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 좋아요 등록/해제 하지 못했습니다.", this@ComplimentActivity)
                }
            }

            override fun onFailure(call: Call<CompDetResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentActivity)
            }
        })
    }

    // 내 프로필 정보 조회
    private fun getProfileData(userId: Int){
        val apiObjectCall = ApiObject.getMyPageService.getProfile(authorization = "Bearer " + UserData.getAccessToken(applicationContext)
            , id = userId)

        apiObjectCall.enqueue(object: Callback<ProfileResData> {
            override fun onResponse(call: Call<ProfileResData>, response: Response<ProfileResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val profileData = response.body()?.data!!

                        val intent = Intent(this@ComplimentActivity, MyPageProfileOtherActivity::class.java)
                        intent.putExtra("profileData", profileData)

                        startActivity(intent)
                    }else{
                        customToast.showCustomToast("프로필 정보 조회를 못했습니다.", this@ComplimentActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<ProfileResData>(
                        ProfileResData::class.java,
                        ProfileResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00011.name){    // 프로필 데이터 조회 x ( 차단 or 삭제...?)
                            customToast.showCustomToast(ErrorCode.S00011.message, this@ComplimentActivity)
                        }else{
                            customToast.showCustomToast("프로필 정보 조회를 못했습니다.", this@ComplimentActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", this@ComplimentActivity)
            }
        })
    }

    // 게시글 작성 화면으로 화면전환
    private fun goWriteActivity(){
        val intent = Intent(this, ComplimentWriteActivity::class.java)
        intent.putParcelableArrayListExtra("compCatData", compCatDataList)
        intent.putExtra("writeType", "friend")
        writeResult.launch(intent)
    }

    // 데이터 초기화
    private fun clearAndSetData(categoryPosition: Int){
        // DiffUtil 적용 후 데이터 추가
        compRecyclerAdapter.differ.submitList(null)     // 게시글 데이터 초기화
        compDataList.clear()

        curCompPage = 0 // 게시글 페이지 초기화

        // 서버에서 카테고리 별로 데이터 GET
        getCompData(compCatDataList[categoryPosition].boardTypeId, curCompPage)
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }
}