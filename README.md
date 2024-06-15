# **서로에게 박수치는 공간, The Clap**

## **📗 목차**

<b>

- 📝 [프로젝트 개요](#-프로젝트-개요)
- 🛠 [기술 및 도구](#-기술-및-도구)
- 👨🏻‍💻 [기능 구현](#-기능-구현)
  - [로그인 및 회원가입 화면](#1-로그인-및-회원가입-화면)
  - [메인 화면](#2-메인-화면)
  - [게시글 조회 화면](#3-게시글-조회-화면)
  - [내소식 화면](#4-내소식-화면)
  - [마이페이지 화면](#5-마이페이지-화면)
  - [기타](#6-기타)
    - [사용 라이브러리](#1-사용-라이브러리)
    - [소셜-로그인 API](#2-소셜-로그인-api)

</b>

## **📝 프로젝트 개요**

<img width="200" height="200" alt="메인 페이지" src="https://github.com/60162143/The_Clap/assets/33407087/c56e460a-1d3a-4241-9ec4-e65a24b24527" />

> **프로젝트:** 서로에게 박수치는 공간 **더클랩(The Clap)**
>
> **기획 및 제작:** 기획자 4명, 디자인 2명, 백엔드 2명, 안드로이드 개발자 1명, IOS 개발자 1명 - 총 10명
>
> **분류:** 팀 프로젝트 (Android Ver.)
>
> **제작 기간:** 23.10 ~ 24.04
>
> **주요 기능:**
  - **자신 또는 주변 친구나 지인 칭찬**
  
  - **상대방 프로필 페이지에서 칭찬 내역 조회**
  - **주간 베스트 칭찬글을 보고 지인에게 공유**
  - **칭찬 챌린지에 참여**
>
> **문의:** no2955922@naver.com

<br />

## **🛠 기술 및 도구**
- **Framework :**
  <img align="center" src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat&logo=Android Studio&logoColor=white">
- **Language :**
  &nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Kotlin-7F52FF?style=flat&logo=Kotlin&logoColor=white">
- **SCM :**
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Github-181717?style=flat&logo=github&logoColor=white">
- **Build :**
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white">
<br />

## **👨🏻‍💻 기능 구현**



### **1. 로그인 및 회원가입 화면**
***
  <details open>
  <summary><b>View 접기/펼치기</b></summary>
  
  <img width="300" height="600" alt="어플 실행 화면" src="https://github.com/60162143/The_Clap/assets/33407087/ec102037-ebda-4d6b-8d51-7b4fe6cb3dd3" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="회원가입 화면" src="https://github.com/60162143/The_Clap/assets/33407087/ef497485-3d7a-44d6-9cac-38198a3976c8" />
  
  </div>
  </details>

- 스플래시 화면 실행 후 메인 화면 전환
  
- **카카오, 네이버, 구글 소셜 로그인** 기능 구현
- **OAuth 인증을 통해 JWT Access, Refresh 토큰 발급** 기능 구현
- **앱 실행에 필요한 권한 요청** 기능 구현
- **닉네임 등록/수정** 기능 구현

<br />

### **2. 메인 화면**
***
  <details open>
  <summary><b>View 접기/펼치기</b></summary>
  
  <img width="300" height="600" alt="메인 화면" src="https://github.com/60162143/The_Clap/assets/33407087/ce4b0efe-5846-4866-8671-d56533ca6840" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="검색 화면" src="https://github.com/60162143/The_Clap/assets/33407087/dd83b2b2-5142-405d-aede-e11f32aa2a79" />  &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="게시글 작성 화면" src="https://github.com/60162143/The_Clap/assets/33407087/2b1c4ed0-822e-4ba8-ba46-b37bb4a8bcd6" />
  
  </div>
  </details>

  - 상단 **게시글/유저 조회** 기능 구현
  
  - **주간마다 랭킹에 등재된 게시글 조회** 기능 구현
  - **칭찬 챌린지 조회 및 참여** 기능 구현
  - 하단 **플로팅 버튼으로 게시글 작성** 기능 구현 ( 이미지 업로드 )

<br />

### **3. 게시글 조회 화면**
***
  <details open>
  <summary><b>View 접기/펼치기</b></summary>
  
  <img width="300" height="600" alt="게시글 조회 화면" src="https://github.com/60162143/The_Clap/assets/33407087/a5efc057-75df-4f1f-b416-4d815238d9d6" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="타유저 프로필 검색 화면" src="https://github.com/60162143/The_Clap/assets/33407087/5180f196-231d-4a2b-ac32-506a793ace46" />
  
  </div>
  </details>

  - **게시글 목록 조회 및 게시글 상세 조회** 기능 구현
  
  - **좋아요, 댓글, 북마크 작성** 기능 구현
  - **게시글 CRUD** 기능 구현
  - **타유저 프로필 조회 및 팔로잉, 팔로우** 기능 구현

<br />

### **4. 내소식 화면**
***
  <details open>
  <summary><b>View 접기/펼치기</b></summary>
  
  <img width="300" height="600" alt="내소식 화면" src="https://github.com/60162143/The_Clap/assets/33407087/36cc65fe-4b41-45be-aa27-3a131eefba6c" />
  
  </div>
  </details>

  - **본인의 활동 or 타유저가 나에게 한 활동 조회** 기능 구현
  
  - **토글 및 스위치를 이용한 구분** 기능 구현
  - **가게 이미지 클릭 시 가게 상세 정보 조회** 기능 구현

<br />

### **5. 마이페이지 화면**
***
  <details open>
  <summary><b>View 접기/펼치기</b></summary>
  
  <img width="300" height="600" alt="지도 화면" src="https://github.com/60162143/The_Clap/assets/33407087/9bbc9712-ccca-4395-8cfe-022764be4697" />  &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="프로필 수정" src="https://github.com/60162143/The_Clap/assets/33407087/4e5c73f9-8863-4b80-bf60-e7ba6ece6987" />
  
  </div>
  </details>

  - **작성한 칭찬/북마크한 게시글 조회** 기능 구현
  
  - **내소식과 동일한** 기능 구현
  - **FAQ/공지사항 조회** 기능 구현
  - **로그아웃/계정탈퇴** 기능 구현
  - **프로필 수정** 기능 구현

<br />

### **6. 기타**

  - #### **1. 사용 라이브러리**
    - **Coroutine Library** : 가벼운 스레드(Light-weight thread)로 동시성(Concurrency) 작업을 간편하게 처리할 수 있게 해주는 역할, 코루틴을 통해 UI 스레드가 중단되는 문제를 효율적으로 처리할 수 있음
      <details open>
      <summary><b>적용 코드 접기/펼치기</b></summary>
      
      ```kotlin
        // Coroutines
        implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1"

        // 데이터를 메인 쓰레드가 백그라운드에서 가져옴으로써 효율적이고 간단하게 구현 가능
        // 데이터 로드 함수  
        private fun loadData() {
          showShimmer = lifecycleScope.launch {  // 결과를 반환하지 않는 코루틴 시작에 사용(결과 반환X), 자체/ 자식 코루틴 실행을 취소할 수 있는 Job 반환

            repeatOnLifecycle(Lifecycle.State.STARTED){  // STARTED 생명주기에 맞춰 코루틴 재시작

              // 카테고리 데이터 GET
              getCategoryList()  
              
            }
          }
        }
      ```
      
      </div>
      </details>
      <br>
      
    - **Shimmer Library** : 표시될 정보의 대략적인 형태를 미리 보여줘서 다음 화면까지 부드럽게 연결해주는 역할
      <details open>
      <summary><b>적용 코드 접기/펼치기</b></summary>
        
      ```kotlin
        // Shimmer
        implementation "com.facebook.shimmer:shimmer:0.5.0"
      ```
      
   
      ```xml
          <!--스켈레톤 로딩 화면에서 보여줄 layout을 작성한다. 위에서 작성한 아이템의 기본 구조와 유사하게 작성하며 백그라운드 색상은 흰색이 아닌 유색으로 지정해줘야 반짝이는 효과를 나타낼 수 있다.-->
            <LinearLayout
                android:layout_width="match_parent"
                android:layout_height="match_parent"
                android:orientation="vertical">

                <!-- 스켈레톤 UI로 표시될 뷰들을 추가합니다 -->
                <com.facebook.shimmer.ShimmerFrameLayout
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:id="@+id/frag_noti_shimmer_layout"
                    android:visibility="gone">

                    <LinearLayout
                        android:layout_width="match_parent"
                        android:layout_height="match_parent"
                        android:orientation="vertical">

                        <include layout="@layout/frag_notification_shim" />

                    </LinearLayout>

                </com.facebook.shimmer.ShimmerFrameLayout>

            </LinearLayout>
      ```
      
      ```kotlin
        // 스켈레톤 로딩 화면 UI delay
        private fun loadData() {
            showShimmer = lifecycleScope.launch {  // 코루틴 내에서 처리
                repeatOnLifecycle(Lifecycle.State.STARTED){  // STARTED 생명주기에 맞춰 실행
                    showEventData(isLoading = true)  // 로딩중일때를 의미, Shimmer UI 보이기
                    delay(1000)  // 1초동안 보이기
    
                    getNotificationData()   // 알림 데이터 조회
    
                    getFollowMatchCount()   // 서로 팔로잉한 유저 수 조회
    
                    showEventData(isLoading = false)  // 로딩완료를 의미, 기존 UI 보이기
                }
            }
        }

        // Shimmer Flag에 대한 UI 변화 SET
        private fun showEventData(isLoading: Boolean) {
            with(binding){
                if (isLoading) {
                    fragNotiShimmerLayout.startShimmer()  // 로딩 애니메이션 시작
                    fragNotiShimmerLayout.visibility = View.VISIBLE  // Shimmer Layout 보이기
                    fragNotiLayout.visibility = View.GONE  // 기존 Layout 숨기기
                    titleConstraintLayout.visibility = View.GONE  // 기존 Layout 숨기기
                } else {
                    fragNotiShimmerLayout.stopShimmer()  // 로딩 애니메이션 종료
                    fragNotiShimmerLayout.visibility = View.GONE  // Shimmer Layout 숨기기
                    fragNotiLayout.visibility = View.VISIBLE  // 기존 Layout 보이기
                    titleConstraintLayout.visibility = View.VISIBLE  // 기존 Layout 보이기
                }
            }
        }
      ```
      
      </div>
      </details>
      <details open>
      <summary><b>적용 화면 접기/펼치기</b></summary>
        <img width="300" height="600" alt="Shimmer 화면" src="https://github.com/60162143/The_Clap/assets/33407087/bb3e8201-65ec-473e-ae51-b0e0050a160d" />
      </details>
      <br>
      
    - **LifeCycle Library** : 액티비티나 프래그먼트의 현재 수명 주기 상태를 기반으로 동작을 자동으로 조절할 수 있는 구성요소를 빌드할 수 있는 클래스 및 인터페이스를 제공
      <details open>
      <summary><b>적용 코드 접기/펼치기</b></summary>
      
      ```kotlin
          // LifeCycle Observe Library
          implementation 'androidx.lifecycle:lifecycle-process:2.7.0'
    
          // 잠금화면을 위한 옵저버
          class LifeCycleChecker(context: Context) : Application(), LifecycleEventObserver {
              private var isForeground = false
              private val lifecycle by lazy { ProcessLifecycleOwner.get().lifecycle }
          
              private var lock = true // 잠금 상태 여부 확인
              private val _context: Context
          
              init {
                  _context = context
              }
          
              override fun onCreate() {
                  super.onCreate()
          
                  lifecycle.addObserver(this) // 옵저버 생성
              }
          
              override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
          
                  when (event) {
                      Lifecycle.Event.ON_STOP -> {    // 앱이 백그라운드 상태로 전환
                          isForeground = false
          
                          if (AppLock(_context).isPassLockSet()) {
                              lock = true // 잠금로 변경
                          }
                      }
          
                      Lifecycle.Event.ON_START -> {   // 앱이 포그라운드 상태로 전환
                          isForeground = true
                          if(lock && AppLock(_context).isPassLockSet() && !AppLock(_context).getDoingPassLock()){
                              AppLock(_context).setDoingPassLock(true)    // 잠금 진행중 여부 설정
          
                              val intent = Intent(_context, MyPageLockPasswordActivity::class.java).apply {
                                  putExtra(AppLockConst.type, AppLockConst.UNLOCK_PASSWORD)
                              }
          
                              intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                              _context.startActivity(intent)
                          }
                      }
                      else -> {}
                  }
              }
          }
      ```
      
      </div>
      </details>
      <details open>
      <summary><b>적용 화면 접기/펼치기</b></summary>
        <img width="300" height="600" alt="비밀번호 화면" src="https://github.com/60162143/The_Clap/assets/33407087/34e61d94-46bf-4774-9d8c-19bc85dfa724" />
      </details>
      <br>
      
    - **Splash Screen Library** :  기존에 별도로 만들어서 사용하던 Splash 화면이 아닌, API를 통해 앱을 실행했을 때 설정한 Splash 화면을 보여주게 된다.
      <details open>
      <summary><b>적용 코드 접기/펼치기</b></summary>
      
      ```kotlin
          // splashScreen api Library
          implementation 'androidx.core:core-splashscreen:1.0.1'

          // Main Activity
          override fun onCreate(savedInstanceState: Bundle?) {
              super.onCreate(savedInstanceState)
      
              splashScreen = installSplashScreen()  // Splash Screen 생성
              startSplash()  // splash의 애니메이션 설정
          }

          // splash의 애니메이션 설정
          private fun startSplash() {
              splashScreen.setOnExitAnimationListener { splashScreenView ->
                  val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
                  val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)

                  // 애니메이션 실행
                  ObjectAnimator.ofPropertyValuesHolder(splashScreenView.iconView, scaleX, scaleY).run {
                      interpolator = AnticipateInterpolator()
                      duration = 3000L    // 3초 진행
      
                      // 로그인 유저 아이디 있는지 확인
                      if(UserData.getUserId(applicationContext) == 0){
                          val intent = Intent(this@MainActivity, LoginActivity::class.java)
                          startForResult.launch(intent)
                      }
      
      
                      doOnEnd {
      
                          splashScreenView.remove()
                      }
      
                      start()
                  }
              }
          }
      ```
      
      </div>
      </details>
      <br>
      
    - **Swiperefreshlayout Library** : Layout을 아래로 Swipe 하여 새로고침이 가능한 라이브러리
      <details open>
      <summary><b>적용 코드 접기/펼치기</b></summary>
      
      ```kotlin
        // SwipeRefeshLayout Library
        implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0'
      
        refreshView.setOnRefreshListener {  // 새로고침 시 리스너
        // 여기서 새로고침 시 작업

          getNotificationData()   // 내소식 데이터 조회

          getFollowMatchCount()   // 서로 팔로잉한 유저 수 조회

          Handler(Looper.getMainLooper()).postDelayed({
            refreshView.isRefreshing = false   // 새로고침 상태 종료
          }, 100)
        }
      ```
      
      </div>
      </details>
      <details open>
      <summary><b>적용 화면 접기/펼치기</b></summary>
        <img width="300" height="600" alt="SwipeRefreshLayout 화면" src="https://github.com/60162143/The_Clap/assets/33407087/f7482c90-a1e2-4c1d-99a1-2d9d20c8816b" />
      </details>
      <br>
      
    - **Glide Library** : 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
      <details open>
      <summary><b>적용 코드 접기/펼치기</b></summary>
      
      ```kotlin
          // Glide Library
          implementation 'com.github.bumptech.glide:glide:4.12.0'
    
        with(binding){
                GlideApp
                    .with(rvImage.context) // View, Fragment 혹은 Activity로부터 Context를 가져온다.
                    .load(compImageData.uri)  // 이미지를 로드한다. 다양한 방법으로 이미지를 불러올 수 있다. (Bitmap, Drawable, String, Uri, File, ResourId(Int), ByteArray)
                    .into(rvImage)  // 이미지를 보여줄 View를 지정한다.
            }
      ```
      
      </div>
      </details>
      <br>

    - **Retrofit2 Library** : API 통신을 위해 구현된 OkHTTP의 HTTP 통신을 간편하게 만들어주는 라이브러리를 뜻함, Async Task가 없이 Background 쓰레드를 실행 -> CallBack을 통하여 Main Thread에서 UI를 업데이트, 동일 Squareup사의 OkHttp 라이브러리의 상위 구현체
      <details open>
      <summary><b>적용 코드 접기/펼치기</b></summary>
   
      ```kotlin
        object ApiObject {
          private const val BASE_URL = BuildConfig.BASE_URL

          // Interface를 사용할 인스턴스, baseUrl(URL) / Converter(변환기) 설정
          val getRetrofit: Retrofit by lazy{
              Retrofit.Builder()
                  .baseUrl(BASE_URL)
                  .addConverterFactory(GsonConverterFactory.create())
                  .build()
          }
      
          val getLoginService : LoginAPI by lazy { getRetrofit.create(LoginAPI::class.java) } // 로그인 관련 API
          val getCompService : ComplimentAPI by lazy { getRetrofit.create(ComplimentAPI::class.java) }    // 게시글 관련 API
          val getMyPageService : MyPageAPI by lazy { getRetrofit.create(MyPageAPI::class.java) }  // 마이페이지 관련 API
          val getSearchService : SearchAPI by lazy { getRetrofit.create(SearchAPI::class.java) }  // 검색 관련 API
          val getNotificationService : NotificationAPI by lazy { getRetrofit.create(NotificationAPI::class.java) }    // 내소식 관련 API
      }
      ```
   
      ```kotlin
          interface LoginAPI {
              // 로그인 확인
              @POST("login/app")
              fun getLoginCheck(
                  @Body req : LoginUserReqData,
              ): Call<LoginUserResData>
          
              // 회원가입
              @POST("login/signup")
              fun getSignUp(
                  @Body req : MembershipUserData,
              ): Call<LoginUserResData>
          
              // 게스트 회원가입
              @POST("login/guestSignup")
              fun getGuestLogin(
              ): Call<GuestLoginUserResData>
          }
      ```
      
      ```kotlin
        // Retrofit2 Library
        implementation 'com.squareup.retrofit2:retrofit:2.9.0'
        implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
      
        // 로그인 체크
        // 로그인 확인 시 메인화면으로 이동
        // 로그인 정보 없을 시 회원가입화면으로 이동
        private fun loginCheck(userData: MembershipUserData) {
            val apiObjectCall = ApiObject.getLoginService.getLoginCheck(LoginUserReqData(userData.socialId, userData.loginType))
    
            apiObjectCall.enqueue(object: Callback<LoginUserResData> {
                override fun onResponse(call: Call<LoginUserResData>, response: Response<LoginUserResData>) {
                    // 추후 성공 시 여기 코드로 리팩토링
                    if(response.isSuccessful) {
                        val loginUserData = response.body()?.data

                        // 데이터 저장장
                        UserData.setUserId(applicationContext, loginUserData?.userId!!)
                        UserData.setAccessToken(applicationContext, loginUserData.accessToken)
                        UserData.setRefreshToken(applicationContext, loginUserData.refreshToken)
                        UserData.setUserName(applicationContext, loginUserData.name)
                        UserData.setNickName(applicationContext, loginUserData.nickname)
                        UserData.setLoginType(applicationContext, userData.loginType)
    
                        if(userData.loginType != "GUEST"){
                            customToast.showCustomToast("로그인에 성공했습니다.", this@LoginActivity)
                        }else{
                            customToast.showCustomToast("게스트 로그인에 성공했습니다.", this@LoginActivity)
                        }
    
                        moveToMainActivity(LoginResult.SUCCESS.code)
                    }else{
                        val errorData = ApiObject.getRetrofit.responseBodyConverter<LoginUserResData>(
                            LoginUserResData::class.java,
                            LoginUserResData::class.java.annotations
                        ).convert(response.errorBody()!!)
    
                        errorData?.code.let{
                            if(it == ErrorCode.S00002.name){
                                // 회원가입 하기
                                val intent = Intent(this@LoginActivity, LoginMembershipAgreementActivity::class.java).apply {
                                    putExtra("membershipUserData", membershipUserData);
                                }
                                startForResult.launch(intent)
    
    
                            }else{
                                customToast.showCustomToast("로그인에 실패했습니다.", this@LoginActivity)
                            }
                        }
                    }
                }
    
                override fun onFailure(call: Call<LoginUserResData>, t: Throwable) {
                    customToast.showCustomToast("Call Failed", this@LoginActivity)
                }
            })
        }
      ```
      
      </div>
      </details>
      <br />
      
  - #### **2. 소셜 로그인 api**

    - **Kakao Login API** : 카카오에서 제공하는 카카오 로그인 API
      <details open>
      <summary><b>적용 코드 접기/펼치기</b></summary>
      
      ```kotlin
        // Kakao Login api
        implementation "com.kakao.sdk:v2-user:2.10.0"

        // 카카오 SDK 초기화
        KakaoSdk.init(applicationContext, BuildConfig.KAKAO_LOGIN_API_KEY)

        // 카카오 로그인 동작
                val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                    if (error != null) {
                        when {
                            error.toString() == AuthErrorCause.AccessDenied.toString() -> {
                                customToast.showCustomToast("접근이 거부 됨(동의 취소)", this@LoginActivity)
                            }
                            error.toString() == AuthErrorCause.InvalidClient.toString() -> {
                                customToast.showCustomToast("유효하지 않은 앱", this@LoginActivity)
                            }
                            error.toString() == AuthErrorCause.InvalidGrant.toString() -> {
                                customToast.showCustomToast("인증 수단이 유효하지 않아 인증할 수 없는 상태", this@LoginActivity)
                            }
                            error.toString() == AuthErrorCause.InvalidRequest.toString() -> {
                                customToast.showCustomToast("요청 파라미터 오류", this@LoginActivity)
                            }
                            error.toString() == AuthErrorCause.InvalidScope.toString() -> {
                                customToast.showCustomToast("유효하지 않은 scope ID", this@LoginActivity)
                            }
                            error.toString() == AuthErrorCause.Misconfigured.toString() -> {
                                customToast.showCustomToast("설정이 올바르지 않음(android key hash)", this@LoginActivity)
                            }
                            error.toString() == AuthErrorCause.ServerError.toString() -> {
                                customToast.showCustomToast("서버 내부 에러", this@LoginActivity)
                            }
                            error.toString() == AuthErrorCause.Unauthorized.toString() -> {
                                customToast.showCustomToast("앱이 요청 권한이 없음", this@LoginActivity)
                            }
                            else -> { // Unknown
                                customToast.showCustomToast("관리자에게 문의해주세요.", this@LoginActivity)
                            }
                        }
                    }
                    else if (token != null) {
                        UserApiClient.instance.me { user, error ->

                            // 회원 가입 정보
                            membershipUserData = MembershipUserData(
                                email = user?.kakaoAccount?.email ?: "",
                                phone = "",
                                name = user?.kakaoAccount?.profile?.nickname ?: "",
                                nickname = "",
                                profile_image = user?.kakaoAccount?.profile?.profileImageUrl ?: "",
                                introduction = "",
                                loginType = LoginType.KAKAO.name,
                                userType = "",
                                socialId = user?.id?.toString() ?: ""
                            )

                            loginCheck(membershipUserData)  // 로그인 체크
                        }
                    }
                }

                // 로그인 진행
                if(UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)){
                    UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity, callback = callback)
                }else{
                    UserApiClient.instance.loginWithKakaoAccount(this@LoginActivity, callback = callback)
                }
      ```
      
      </div>
      </details>
      <br>
      
    - **Naver Login API** : 네이버에서 제공하는 카카오 로그인 API
      <details open>
      <summary><b>적용 코드 접기/펼치기</b></summary>
      
      ```kotlin
        // Naver Login api
        implementation 'com.navercorp.nid:oauth:5.3.0'

        // 네이버 SDK 초기화
        NaverIdLoginSDK.initialize(applicationContext, BuildConfig.NAVER_LOGIN_CLIENT_ID, BuildConfig.NAVER_LOGIN_CLIENT_SECRET, BuildConfig.NAVER_LOGIN_CLIENT_NAME)

        // 네이버 로그인 동작
                var naverToken :String? = ""

                val profileCallback = object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(response: NidProfileResponse) {
                        // 회원 가입 정보
                        membershipUserData = MembershipUserData(
                            email = response.profile?.email ?: "",
                            phone = "",
                            name = response.profile?.name ?: "",
                            nickname = "",
                            profile_image = response.profile?.profileImage ?: "",
                            introduction = "",
                            loginType = LoginType.NAVER.name,
                            userType = "",
                            socialId = response.profile?.id ?: ""
                        )

                        loginCheck(membershipUserData)  // 로그인 체크
                    }
                    override fun onFailure(httpStatus: Int, message: String) {
                        val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                        val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                        customToast.showCustomToast("로그인에 실패했습니다.", this@LoginActivity)
                    }
                    override fun onError(errorCode: Int, message: String) {
                        onFailure(errorCode, message)
                    }
                }

                /** OAuthLoginCallback을 authenticate() 메서드 호출 시 파라미터로 전달하거나 NidOAuthLoginButton 객체에 등록하면 인증이 종료되는 것을 확인할 수 있습니다. */
                val oauthLoginCallback = object : OAuthLoginCallback {
                    override fun onSuccess() {
                        // 네이버 로그인 인증이 성공했을 때 수행할 코드 추가
                        naverToken = NaverIdLoginSDK.getAccessToken()
                        var naverRefreshToken = NaverIdLoginSDK.getRefreshToken()
                        var naverExpiresAt = NaverIdLoginSDK.getExpiresAt().toString()
                        var naverTokenType = NaverIdLoginSDK.getTokenType()
                        var naverState = NaverIdLoginSDK.getState().toString()

                        //로그인 유저 정보 가져오기
                        NidOAuthLogin().callProfileApi(profileCallback)
                    }
                    override fun onFailure(httpStatus: Int, message: String) {
                        val errorCode = NaverIdLoginSDK.getLastErrorCode().code
                        val errorDescription = NaverIdLoginSDK.getLastErrorDescription()
                        customToast.showCustomToast("로그인에 실패했습니다.", this@LoginActivity)
                    }
                    override fun onError(errorCode: Int, message: String) {
                        onFailure(errorCode, message)
                    }
                }

                NaverIdLoginSDK.authenticate(this@LoginActivity, oauthLoginCallback)
      ```
      
      </div>
      </details>
      <br>
      
    - **Google Login API** : 구글에서 제공하는 카카오 로그인 API
      <details open>
      <summary><b>적용 코드 접기/펼치기</b></summary>
      
      ```kotlin
        implementation 'com.google.gms:google-services:4.4.0'
        implementation 'com.google.firebase:firebase-auth:22.3.1'

        // 구글 SDK 초기화
        companion object {
    
            private val instance = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(BuildConfig.GOOGLE_LOGIN_CLIENT_ID)
                .requestServerAuthCode(BuildConfig.GOOGLE_LOGIN_CLIENT_ID) // client id 를 이용해 server authcode를 요청한다.
                .requestEmail() // 이메일도 요청할 수 있다.
                .build()
    
            fun getInstance(): GoogleSignInOptions {
                return instance
            }
        }

      //  구글 로그인 동작
            val account = GoogleSignIn.getLastSignedInAccount(this@LoginActivity)

            val signInIntent = GoogleSignIn.getClient(applicationContext, InitApplication.getInstance()).signInIntent
            
            googleAuthLauncher.launch(signInIntent)

            // 구글 로그인 진행 후 결과 값 받아오기
            private val googleAuthLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        
                    try {
                        // 이름, 이메일 등이 필요하다면 아래와 같이 account를 통해 각 메소드를 불러올 수 있다.
                        val account = task.getResult(ApiException::class.java)
        
                        // 회원 가입 정보
                        membershipUserData =  MembershipUserData(
                            email = account?.email ?: "",
                            phone = "",
                            name = account?.displayName ?: "",
                            nickname = "",
                            profile_image = account?.photoUrl?.toString() ?: "",
                            introduction = "",
                            loginType = LoginType.GOOGLE.name,
                            userType = "",
                            socialId = account?.id ?: ""
                        )
        
                        loginCheck(membershipUserData)  // 로그인 체크
        
                    } catch (e: ApiException) {
                        customToast.showCustomToast("로그인에 실패했습니다.", this@LoginActivity)
                    }
                }else{
                    customToast.showCustomToast("로그인에 실패했습니다.", this@LoginActivity)
                }
            }
      ```
      
      </div>
      </details>
<br />
