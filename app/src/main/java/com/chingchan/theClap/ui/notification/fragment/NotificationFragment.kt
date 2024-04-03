package com.chingchan.theClap.ui.notification.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.chingchan.theClap.MainActivity
import com.chingchan.theClap.R
import com.chingchan.theClap.databinding.FragNotificationBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.ui.compliment.activity.ComplimentDetailActivity
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.CompCatResData
import com.chingchan.theClap.ui.compliment.data.CompOneResData
import com.chingchan.theClap.ui.login.data.LoginUserResData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.myPage.activity.MyPageProfileOtherActivity
import com.chingchan.theClap.ui.myPage.adapter.MyPageAnnounceRecyclerAdapter
import com.chingchan.theClap.ui.myPage.data.FollowMatchCountResData
import com.chingchan.theClap.ui.myPage.data.ProfileResData
import com.chingchan.theClap.ui.notification.adapter.NotificationRecyclerAdapter
import com.chingchan.theClap.ui.notification.data.NotificationData
import com.chingchan.theClap.ui.notification.data.NotificationDeleteResData
import com.chingchan.theClap.ui.notification.data.NotificationReadResData
import com.chingchan.theClap.ui.notification.data.NotificationResData
import com.chingchan.theClap.ui.notification.data.NotificationType
import com.chingchan.theClap.ui.toast.customToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationFragment : Fragment() {

    private var _binding: FragNotificationBinding? = null
    private lateinit var mainActivity: MainActivity

    private var notificationRecyclerAdapter: NotificationRecyclerAdapter = NotificationRecyclerAdapter()    // 내소식 리사이클러뷰 어뎁터

    private var otherActiveList: ArrayList<NotificationData> = ArrayList()  // '알림' 전제 데이터
    private var otherActiveIsReadList: ArrayList<NotificationData> = ArrayList()  // '알림' 읽음 데이터
    private var userActiveList: ArrayList<NotificationData> = ArrayList()   // '활동' 전체 데이터
    private var userActiveIsReadList: ArrayList<NotificationData> = ArrayList()   // '활동' 읽음 데이터

    private var compCatList: ArrayList<CompCatData> = ArrayList()   // 카테고리 데이터

    private var curActiveType: String = "USER_ACTIVE"   // 현재 내소식 타입
    private var curIsRead: Boolean = false  // 현재 내소식 읽음 여부

    private var showShimmer: Job? = null

    private var curPosition: Int = -1    // 현재 클릭한 리사이클러뷰 position

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragNotificationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToggleBtn(true)  // 토클 버튼 초기화

        with(binding){
            getCategoryList()   // 카테고리 데이터 조회

            // 내소식 리사이클러뷰
            rvNotification.adapter = notificationRecyclerAdapter
            rvNotification.itemAnimator = null

            loadData()  // 데이터 가져오기

            // '알림' 토글 버튼 클릭 리스너
            btnNoti.setOnClickListener(View.OnClickListener {
                setToggleBtn(true)
            })

            // '활동' 토글 버튼 클릭 리스너
            btnAct.setOnClickListener(View.OnClickListener {
                setToggleBtn(false)
            })

            // 읽음/안읽음 스위치 리스너
            switchCheck.setOnCheckedChangeListener { buttonView, isChecked ->
                curIsRead = isChecked

                if(isChecked){
                    textCheck.text = "읽음"

                    if(curActiveType == "OTHER_ACTIVE"){    // Type이 '알림'인 경우

                        otherActiveIsReadList.clear()  // '알림' 읽음 데이터 초기화

                        // '알림' 읽음 데이터 SET
                        for(otherActiveData in otherActiveList){
                            if(otherActiveData.isRead == "Y"){
                                otherActiveIsReadList.add(otherActiveData)
                            }
                        }
                    }else{  // Type이 '활동'인 경우

                        userActiveIsReadList.clear()  // '활동' 읽음 데이터 초기화

                        // '활동' 읽음 데이터 SET
                        for(userActiveData in userActiveList){
                            if(userActiveData.isRead == "Y"){
                                userActiveIsReadList.add(userActiveData)
                            }
                        }
                    }
                }else{
                    textCheck.text = "안읽음"
                }

                setData()   // 데이터 SET
            }

            refreshView.setOnRefreshListener {
                // 여기서 새로고침 시 작업

                getNotificationData()   // 내소식 데이터 조회

                getFollowMatchCount()   // 서로 팔로잉한 유저 수 조회

                Handler(Looper.getMainLooper()).postDelayed({
                    refreshView.isRefreshing = false   // 새로고침 상태 종료
                }, 100)
            }

            // 내소식 클릭 리스너
            notificationRecyclerAdapter.clickListener = object : NotificationRecyclerAdapter.OnClickListener{
                override fun onClick(position: Int, type: String) {
                    if(type == "TOTAL"){  // 전체 영역 클릭 시
                        curPosition = position  // 현재 누른 리사이클러뷰 포지션

                        if(curActiveType == "OTHER_ACTIVE"){    // Type이 '알림'인 경우
                            if(!curIsRead && otherActiveList[position].isRead == "N"){  // '안읽음' 토글 활성화 되어있고 읽지 않은 내소식인 경우
                                readNotificationData(otherActiveList[position].id)
                            }

                            if(otherActiveList[position].logTypeId == NotificationType.TYPE02.code){    // 댓글 관련
                                getOneCompData(otherActiveList[position].boardId)   // 게시글 데이터 조회
                            }else if(otherActiveList[position].logTypeId == NotificationType.TYPE05.code){    // 인기글 등록 관련
                                mainActivity.moveToHome()   // 홈 화면으로 이동
                            }
                        }else{  // Type이 '알림'인 경우
                            if(!curIsRead && userActiveList[position].isRead == "N"){  // '안읽음' 토글 활성화 되어있고 읽지 않은 내소식인 경우
                                readNotificationData(userActiveList[position].id)
                            }
                        }
                    }else if(type == "FOLLOW"){  // '팔로잉하러 가기' 클릭 시
                        curPosition = position  // 현재 누른 위치의 position 갱신

                        val newList = notificationRecyclerAdapter.differ.currentList.toMutableList()    // 알림 데이터

                        getProfileData(newList[curPosition].targetUserId) // 나를 팔로우한 유저 프로필 데이터 조회
                    }else if(type == "DELETE"){  // '삭제' 버튼 클릭 시
                        curPosition = position  // 현재 누른 리사이클러뷰 포지션

                        val logIds: ArrayList<Int> = ArrayList()

                        if(curActiveType == "OTHER_ACTIVE"){    // Type이 '알림'인 경우
                            if(curIsRead){  // '읽음' 토글 활성화 경우
                                logIds.add(otherActiveIsReadList[position].id)
                            }else{  // '안읽음' 토글 활성화 경우
                                logIds.add(otherActiveList[position].id)
                            }
                        }else{  // Type이 '활동'인 경우
                            if(curIsRead){  // '읽음' 토글 활성화 경우
                                logIds.add(userActiveIsReadList[position].id)
                            }else{  // '안읽음' 토글 활성화 경우
                                logIds.add(userActiveList[position].id)
                            }
                        }

                        deleteNotificationData(logIds)  // 내소식 데이터 삭제
                    }
                }
            }

            // '전체 삭제' 버튼 클릭 리스너
            btnAllDelete.setOnClickListener(View.OnClickListener {
                curPosition = -1    // 리사이클러뷰를 클릭한게 아닌 전체 삭제이기 때문에 curPosition 초기화

                val logIds: ArrayList<Int> = ArrayList()

                if(curActiveType == "OTHER_ACTIVE"){    // Type이 '알림'인 경우
                    if(curIsRead){  // '읽음' 토글 활성화 경우
                        for(otherActiveData in otherActiveIsReadList){
                            logIds.add(otherActiveData.id)
                        }
                    }else{  // '안읽음' 토글 활성화 경우
                        for(otherActiveData in otherActiveList){
                            logIds.add(otherActiveData.id)
                        }
                    }
                }else{  // Type이 '활동'인 경우
                    if(curIsRead){  // '읽음' 토글 활성화 경우
                        for(otherActiveData in userActiveIsReadList){
                            logIds.add(otherActiveData.id)
                        }
                    }else{  // '안읽음' 토글 활성화 경우
                        for(otherActiveData in userActiveList){
                            logIds.add(otherActiveData.id)
                        }
                    }
                }

                deleteNotificationData(logIds)  // 내소식 데이터 삭제
            })

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 스켈레톤 로딩 화면 UI delay
    private fun loadData() {
        showShimmer = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                showEventData(isLoading = true)
                delay(1000)

                getNotificationData()   // 알림 데이터 조회

                getFollowMatchCount()   // 서로 팔로잉한 유저 수 조회

                showEventData(isLoading = false)
            }
        }
    }

    private fun showEventData(isLoading: Boolean) {
        with(binding){
            if (isLoading) {
                fragNotiShimmerLayout.startShimmer()
                fragNotiShimmerLayout.visibility = View.VISIBLE
                fragNotiLayout.visibility = View.GONE
                titleConstraintLayout.visibility = View.GONE
            } else {
                fragNotiShimmerLayout.stopShimmer()
                fragNotiShimmerLayout.visibility = View.GONE
                fragNotiLayout.visibility = View.VISIBLE
                titleConstraintLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun setToggleBtn(tf: Boolean){
        with(binding){
            if(tf){
                btnNoti.isSelected = true
                btnAct.isSelected = false

                btnNoti.typeface = ResourcesCompat.getFont(mainActivity.applicationContext, R.font.pretendard_extrabold)
                btnAct.typeface = ResourcesCompat.getFont(mainActivity.applicationContext, R.font.pretendard_regular)

                curActiveType = "OTHER_ACTIVE"
            }else{
                btnNoti.isSelected = false
                btnAct.isSelected = true

                btnNoti.typeface = ResourcesCompat.getFont(mainActivity.applicationContext, R.font.pretendard_regular)
                btnAct.typeface = ResourcesCompat.getFont(mainActivity.applicationContext, R.font.pretendard_extrabold)

                curActiveType = "USER_ACTIVE"
            }

            setData()   // 데이터 SET
        }
    }

    // 내소식 데이터 조회
    private fun getNotificationData(){
        val apiObjectCall = ApiObject.getNotificationService.getLog(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext))

        apiObjectCall.enqueue(object: Callback<NotificationResData> {
            override fun onResponse(call: Call<NotificationResData>, response: Response<NotificationResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val notificationList = response.body()?.data!!

                        // 데이터 초기화
                        otherActiveList.clear()
                        otherActiveIsReadList.clear()
                        userActiveList.clear()
                        userActiveIsReadList.clear()

                        // 각 Type별로 데이터 저장
                        if(notificationList.isNotEmpty()){
                            for(notificationData in notificationList){
                                if(notificationData.userLogActiveType == "OTHER_ACTIVE"){   // Type이 '알림'인 경우

                                    otherActiveList.add(notificationData)

                                    if(notificationData.isRead == "Y"){ // '알림' 읽음 데이터
                                        otherActiveIsReadList.add(notificationData)
                                    }

                                }else if(notificationData.userLogActiveType == "USER_ACTIVE"){  // Type이 '활동'인 경우
                                    userActiveList.add(notificationData)

                                    if(notificationData.isRead == "Y"){ // '활동' 읽음 데이터
                                        userActiveIsReadList.add(notificationData)
                                    }
                                }
                            }
                        }

                        setData()   // 데이터 SET

                    }else{
                        customToast.showCustomToast("내소식 정보 조회를 못했습니다.", mainActivity)
                    }
                }else{
                    customToast.showCustomToast("내소식 정보 조회를 못했습니다.", mainActivity)
                }
            }

            override fun onFailure(call: Call<NotificationResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 내소식 데이터 삭제
    private fun deleteNotificationData(logIds: ArrayList<Int>){
        val apiObjectCall = ApiObject.getNotificationService.deleteLog(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext)
        , logIds = logIds)

        apiObjectCall.enqueue(object: Callback<NotificationDeleteResData> {
            override fun onResponse(call: Call<NotificationDeleteResData>, response: Response<NotificationDeleteResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    if(curActiveType == "OTHER_ACTIVE"){    // '알림'의 경우
                        if(curIsRead){  // '읽음' 토글 활성화 경우
                            if(curPosition > -1){   // 선택 삭제의 경우

                                // '알림' 전체 리스트에서 값 제거
                                for((index, notificationData) in otherActiveList.withIndex()){
                                    if(notificationData.id == otherActiveList[curPosition].id){
                                        otherActiveList.removeAt(index)    // '알림' 전체 리스트의 값 제거
                                        break
                                    }
                                }

                                otherActiveIsReadList.removeAt(curPosition)   // '알림' 읽음 리스트의 값 제거
                            }else{  // 전체 삭제의 경우

                                // '알림' 읽음 리스트 값 모두 제거
                                for(notificationReadData in otherActiveIsReadList){
                                    // '알림' 전체 리스트에서 값 제거
                                    for((index, notificationData) in otherActiveList.withIndex()){
                                        if(notificationData.id == notificationReadData.id){
                                            otherActiveList.removeAt(index)    // '알림' 전체 리스트의 값 제거
                                            break
                                        }
                                    }
                                }

                                otherActiveIsReadList.clear()  // '알림' 전체 리스트 초기화
                            }

                            setLayout(otherActiveIsReadList.isEmpty())    // 레이아웃 SET
                        }else{  // '안읽음' 토글 활성화 경우
                            if(curPosition > -1){   // 선택 삭제의 경우
                                otherActiveList.removeAt(curPosition)   // '알림' 전체 리스트의 값 제거
                            }else{  // 전체 삭제의 경우
                                otherActiveList.clear() // '알림' 전체 리스트 초기화
                            }

                            setLayout(otherActiveList.isEmpty())    // 레이아웃 SET
                        }
                    }else{  // '활동'의 경우
                        if(curIsRead){  // '읽음' 토글 활성화 경우
                            if(curPosition > -1){   // 선택 삭제의 경우

                                // '활동' 전체 리스트에서 값 제거
                                for((index, notificationData) in userActiveList.withIndex()){
                                    if(notificationData.id == userActiveList[curPosition].id){
                                        userActiveList.removeAt(index)    // '활동' 전체 리스트의 값 제거
                                        break
                                    }
                                }

                                userActiveIsReadList.removeAt(curPosition)   // '활동' 읽음 리스트의 값 제거
                            }else{  // 전체 삭제의 경우

                                // '활동' 읽음 리스트 값 모두 제거
                                for(notificationReadData in userActiveIsReadList){
                                    // '활동' 전체 리스트에서 값 제거
                                    for((index, notificationData) in userActiveList.withIndex()){
                                        if(notificationData.id == notificationReadData.id){
                                            userActiveList.removeAt(index)    // '활동' 전체 리스트의 값 제거
                                            break
                                        }
                                    }
                                }

                                userActiveIsReadList.clear()  // '활동' 전체 리스트 초기화
                            }

                            setLayout(userActiveIsReadList.isEmpty())    // 레이아웃 SET
                        }else{  // '안읽음' 토글 활성화 경우
                            if(curPosition > -1){   // 선택 삭제의 경우
                                userActiveList.removeAt(curPosition)   // '활동' 전체 리스트의 값 제거
                            }else{  // 전체 삭제의 경우
                                userActiveList.clear() // '활동' 전체 리스트 초기화
                            }

                            setLayout(userActiveList.isEmpty())    // 레이아웃 SET
                        }
                    }

                    setData()   // 데이터 SET

                    customToast.showCustomToast(response.body()?.data.toString(), mainActivity)
                }else{
                    customToast.showCustomToast("내소식 정보 삭제를 못했습니다.", mainActivity)
                }
            }

            override fun onFailure(call: Call<NotificationDeleteResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 내소식 데이터 '읽음' 처리
    private fun readNotificationData(logId: Int){
        val apiObjectCall = ApiObject.getNotificationService.readLog(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext)
        , logId = logId)

        apiObjectCall.enqueue(object: Callback<NotificationReadResData> {
            override fun onResponse(call: Call<NotificationReadResData>, response: Response<NotificationReadResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        if(curActiveType == "OTHER_ACTIVE"){    // Type이 '알림'인 경우
                            otherActiveList[curPosition] = otherActiveList[curPosition].copy(isRead = "Y")

                            // adapter의 differ.submitList()로 newList 제출
                            // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                            notificationRecyclerAdapter.differ.submitList(otherActiveList.toMutableList())
                        }else{  // Type이 '활동'인 경우
                            userActiveList[curPosition] = userActiveList[curPosition].copy(isRead = "Y")

                            // adapter의 differ.submitList()로 newList 제출
                            // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                            notificationRecyclerAdapter.differ.submitList(userActiveList.toMutableList())
                        }

                    }else{
                        customToast.showCustomToast("내소식 '읽음' 처리를 못했습니다.", mainActivity)
                    }
                }else{
                    customToast.showCustomToast("내소식 '읽음' 처리를 못했습니다.", mainActivity)
                }
            }

            override fun onFailure(call: Call<NotificationReadResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 카테고리 데이터 조회
    private fun getCategoryList() {
        val apiObjectCall = ApiObject.getCompService.getCompCat(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext))

        apiObjectCall.enqueue(object: Callback<CompCatResData> {
            override fun onResponse(call: Call<CompCatResData>, response: Response<CompCatResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    compCatList = response.body()?.data!!

                    compCatList.sortBy { T -> T.boardTypeId }
                }else{
                    customToast.showCustomToast("카테고리를 불러오지 못했습니다.", mainActivity)
                }
            }

            override fun onFailure(call: Call<CompCatResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 게시글 단건 조회
    private fun getOneCompData(boardId: Int){
        val apiObjectCall = ApiObject.getCompService.getOneComp(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext)
            , boardId = boardId)

        apiObjectCall.enqueue(object: Callback<CompOneResData> {
            override fun onResponse(call: Call<CompOneResData>, response: Response<CompOneResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val compData = response.body()?.data

                        // 게시글 상세 Activity로 화면 전환
                        val intent = Intent(mainActivity, ComplimentDetailActivity::class.java)

                        intent.putParcelableArrayListExtra("compCatData", compCatList)
                        intent.putExtra("compData", compData)
                        startActivity(intent)
                    }else{
                        customToast.showCustomToast("게시물 조회를 못했습니다.", mainActivity)
                    }

                }else{
                    customToast.showCustomToast("게시물 조회를 못했습니다.", mainActivity)
                }
            }

            override fun onFailure(call: Call<CompOneResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 서로 팔로잉한 수 조회
    private fun getFollowMatchCount(){
        val apiObjectCall = ApiObject.getMyPageService.getFollowMatchCount(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext))

        apiObjectCall.enqueue(object: Callback<FollowMatchCountResData> {
            override fun onResponse(call: Call<FollowMatchCountResData>, response: Response<FollowMatchCountResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        binding.followMatchCount.text = response.body()!!.data.followMatchCount.toString() + "명"
                    }else{
                        customToast.showCustomToast("서로 팔로잉한 수 조회를 못했습니다.", mainActivity)
                    }

                }else{
                    customToast.showCustomToast("서로 팔로잉한 수 조회를 못했습니다.", mainActivity)
                }
            }

            override fun onFailure(call: Call<FollowMatchCountResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 나를 팔로우한 유저 프로필 정보 조회
    private fun getProfileData(userId: Int){
        val apiObjectCall = ApiObject.getMyPageService.getProfile(authorization = "Bearer " + UserData.getAccessToken(mainActivity.applicationContext)
            , id = userId)

        apiObjectCall.enqueue(object: Callback<ProfileResData> {
            override fun onResponse(call: Call<ProfileResData>, response: Response<ProfileResData>) {
                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val status = response.body()?.status!!
                    if(status == "success"){
                        val profileData = response.body()?.data!!

                        val intent = Intent(mainActivity, MyPageProfileOtherActivity::class.java)
                        intent.putExtra("profileData", profileData)

                        startActivity(intent)
                    }else{
                        customToast.showCustomToast("프로필 정보 조회를 못했습니다.", mainActivity)
                    }

                }else{
                    val errorData = ApiObject.getRetrofit.responseBodyConverter<ProfileResData>(
                        ProfileResData::class.java,
                        ProfileResData::class.java.annotations
                    ).convert(response.errorBody()!!)

                    errorData?.code.let{
                        if(it == ErrorCode.S00011.name){    // 프로필 데이터 조회 x ( 차단 or 삭제...?)
                            customToast.showCustomToast(ErrorCode.S00011.message, mainActivity)
                        }else{
                            customToast.showCustomToast("프로필 정보 조회를 못했습니다.", mainActivity)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ProfileResData>, t: Throwable) {
                customToast.showCustomToast("Call Failed", mainActivity)
            }
        })
    }

    // 데이터 SET
    private fun setData(){
        notificationRecyclerAdapter.differ.submitList(null)

        if(curActiveType == "OTHER_ACTIVE"){
            if(curIsRead){  // '읽음' 토글 활성화 경우
                setLayout(otherActiveIsReadList.isEmpty())    // 레이아웃 SET

                // adapter의 differ.submitList()로 newList 제출
                // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                notificationRecyclerAdapter.differ.submitList(otherActiveIsReadList.toMutableList())

                binding.notificationCount.text = otherActiveIsReadList.size.toString()    // 내소식 총 개수 SET
            }else{  // '안읽음' 토글 활성화 경우
                setLayout(otherActiveList.isEmpty())    // 레이아웃 SET

                // adapter의 differ.submitList()로 newList 제출
                // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                notificationRecyclerAdapter.differ.submitList(otherActiveList.toMutableList())

                binding.notificationCount.text = otherActiveList.size.toString()    // 내소식 총 개수 SET
            }
        }else if(curActiveType == "USER_ACTIVE"){
            if(curIsRead){  // '읽음' 토글 활성화 경우
                setLayout(userActiveIsReadList.isEmpty())    // 레이아웃 SET

                // adapter의 differ.submitList()로 newList 제출
                // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                notificationRecyclerAdapter.differ.submitList(userActiveIsReadList.toMutableList())

                binding.notificationCount.text = userActiveIsReadList.size.toString()    // 내소식 총 개수 SET
            }else{  // '안읽음' 토글 활성화 경우
                setLayout(userActiveList.isEmpty())    // 레이아웃 SET

                // adapter의 differ.submitList()로 newList 제출
                // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                notificationRecyclerAdapter.differ.submitList(userActiveList.toMutableList())

                binding.notificationCount.text = userActiveList.size.toString()    // 내소식 총 개수 SET
            }
        }
    }

    // 내소식 데이터가 있는지 여부에 대한 레이아웃 변화
    private fun setLayout(isEmpty: Boolean){
        with(binding){
            if(isEmpty){
                layoutRecyclerview.visibility = View.GONE
                textEmpty.visibility = View.VISIBLE
            }else{
                layoutRecyclerview.visibility = View.VISIBLE
                textEmpty.visibility = View.GONE
            }
        }
    }
}