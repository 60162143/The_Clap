# **서로에게 박수치는 공간, The Clap**

## **📗 목차**

<b>

- 📝 [프로젝트 개요](#-프로젝트-개요)
- 🛠 [기술 및 도구](#-기술-및-도구)
- 👨🏻‍💻 [기능 구현](#-기능-구현)
  - [메인 화면](#1-메인-화면)
  - [로그인/회원가입 화면](#2-로그인-화면)
  - [가게 상세 화면](#3-가게-상세-화면)
    - [가게 정보 조회 화면](#1-가게-정보-조회-화면)
    - [리뷰 화면](#2-리뷰-화면)
    - [주문/결제 화면](#3-주문-결제-화면)
  - [협업 화면](#4-협업-화면)
  - [지도 화면](#5-지도-화면)
  - [가방 화면](#6-가방-화면)
  - [마이페이지 화면](#7-마이페이지-화면)
    - [내 정보 조회 화면](#1-내-정보-조회-화면)
    - [내 정보 수정 화면](#2-내-정보-수정-화면)
  - [기타](#8-기타)
    - [사용 라이브러리](#1-사용-라이브러리)
    - [사용 API](#2-사용-api)
- 📓 [ERD 설계](#-데이터베이스)
- 📮 [데이터 크롤링](#-데이터-크롤링)

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
  - **회원간의 대화**
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



### **1. 로그인/회원가입 화면**
***
  <details>
  <summary>View 접기/펼치기</summary>
  
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
  <details>
  <summary>View 접기/펼치기</summary>
  
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
  <details>
  <summary>View 접기/펼치기</summary>
  
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
  <details>
  <summary>View 접기/펼치기</summary>
  
  <img width="300" height="600" alt="내소식 화면" src="https://github.com/60162143/The_Clap/assets/33407087/36cc65fe-4b41-45be-aa27-3a131eefba6c" />
  
  </div>
  </details>

  - **본인의 활동 or 타유저가 나에게 한 활동 조회** 기능 구현
  
  - **토글 및 스위치를 이용한 구분** 기능 구현
  - **가게 이미지 클릭 시 가게 상세 정보 조회** 기능 구현

<br />

### **5. 마이페이지 화면**
***
  <details>
  <summary>View 접기/펼치기</summary>
  
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
      <details>
      <summary>적용 코드 접기/펼치기</summary>
      
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
      <details>
      <summary>적용 코드 접기/펼치기</summary>
        
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
      <br>
      
    - **LifeCycle Library** : 액티비티나 프래그먼트의 현재 수명 주기 상태를 기반으로 동작을 자동으로 조절할 수 있는 구성요소를 빌드할 수 있는 클래스 및 인터페이스를 제공
      <details>
      <summary>예시 접기/펼치기</summary>
      
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
      <br>
      
    - **Splash Screen Library** :  기존에 별도로 만들어서 사용하던 Splash 화면이 아닌, API를 통해 앱을 실행했을 때 설정한 Splash 화면을 보여주게 된다.
      <details>
      <summary>예시 접기/펼치기</summary>
      
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
      <details>
      <summary>예시 접기/펼치기</summary>
      
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
      <br>
      
    - **Glide Library** : 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
      <details>
      <summary>예시 접기/펼치기</summary>
      
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
      
    - **Glide Library** : 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
      <details>
      <summary>예시 접기/펼치기</summary>
      
      ```kotlin
        with(binding){
                GlideApp
                    .with(rvImage.context) // View, Fragment 혹은 Activity로부터 Context를 가져온다.
                    .load(compImageData.uri)  // 이미지를 로드한다. 다양한 방법으로 이미지를 불러올 수 있다. (Bitmap, Drawable, String, Uri, File, ResourId(Int), ByteArray)
                    .into(rvImage)  // 이미지를 보여줄 View를 지정한다.
            }
      ```
      
      </div>
      </details>
    - **Styleable Toast Library** : 폰트, 배경색, 아이콘 등 토스트의 전반적인 디자인을 themes.xml에서 원하는 대로 지정해 줄 수 있는 라이브러리
      <details>
      <summary>예시 접기/펼치기</summary>
      
      ```java
        // themes.xml
        <style name="orangeToast">
            <item name="stTextBold">텍스트 스타일 Bold 유무</item>
            <item name="stTextColor">텍스트 색상</item>
            <item name="stFont">폰트</item>
            <item name="stTextSize">텍스트 사이즈</item>
            <item name="stColorBackground">배경색</item>
            <item name="stStrokeWidth">테두리 두께</item>
            <item name="stStrokeColor">테두리 색상</item>
            <item name="stIconStart">왼쪽에 나타날 아이콘</item>
            <item name="stIconEnd">오른쪽에 나타날 아이콘</item>
            <item name="stLength">지속 시간  LONG or SHORT</item>
            <item name="stGravity">위치  top or center</item>
            <item name="stRadius">가장자리 둥글게</item>
        </style>
      ```
      
      </div>
      </details>

    - **Volley Library** : 네트워킹을 보다 쉽고 빠르게 만들어주는 HTTP 라이브러리
      <details>
      <summary>예시 접기/펼치기</summary>
      
      ```java
        public void sendRequest(){
          String url = "https://www.google.co.kr";
  
          //StringRequest를 만듬 (파라미터구분을 쉽게하기위해 엔터를 쳐서 구분하면 좋다)
          //StringRequest는 요청객체중 하나이며 가장 많이 쓰인다고한다.
          //요청객체는 다음고 같이 보내는방식(GET,POST), URL, 응답성공리스너, 응답실패리스너 이렇게 4개의 파라미터를 전달할 수 있다.(리퀘스트큐에 ㅇㅇ)
          //화면에 결과를 표시할때 핸들러를 사용하지 않아도되는 장점이있다.
          StringRequest request = new StringRequest(
                  Request.Method.GET,
                  url,
                  new Response.Listener<String>() {  //응답을 문자열로 받아서 여기다 넣어달란말임(응답을 성공적으로 받았을 떄 이메소드가 자동으로 호출됨
                      @Override
                      public void onResponse(String response) {
                          println("응답 => " + response);
                      }
                  },
                  new Response.ErrorListener(){ //에러발생시 호출될 리스너 객체
                      @Override
                      public void onErrorResponse(VolleyError error) {
                          println("에러 => "+ error.getMessage());
                      }
                  }
          ){
              //만약 POST 방식에서 전달할 요청 파라미터가 있다면 getParams 메소드에서 반환하는 HashMap 객체에 넣어줍니다.
              //이렇게 만든 요청 객체는 요청 큐에 넣어주는 것만 해주면 됩니다.
              //POST방식으로 안할거면 없어도 되는거같다.
              @Override
              protected Map<String, String> getParams() throws AuthFailureError {
                  Map<String, String> params = new HashMap<String, String>();
                  return params;
              }
          };
  
          //아래 add코드처럼 넣어줄때 Volley라고하는게 내부에서 캐싱을 해준다, 즉, 한번 보내고 받은 응답결과가 있으면
          //그 다음에 보냈을 떄 이전 게 있으면 그냥 이전거를 보여줄수도  있다.
          //따라서 이렇게 하지말고 매번 받은 결과를 그대로 보여주기 위해 다음과같이 setShouldCache를 false로한다.
          //결과적으로 이전 결과가 있어도 새로 요청한 응답을 보여줌
          request.setShouldCache(false);
          AppHelper.requestQueue.add(request);
          println("요청 보냄!!");
        }
      ```
      
      </div>
      </details>
      
    - **TedPermission Library** : 안드로이드에서 퍼미션 권한 관리에 도움을 주는 라이브러리
      <details>
      <summary>예시 접기/펼치기</summary>
      
      ```java
        PermissionListener permissionlistener = new PermissionListener() {
              @Override
              public void onPermissionGranted() {
                  Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
              }
  
              @Override
              public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                  Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
              }
  
          };
  
          TedPermission.with(this)
                  .setPermissionListener(permissionlistener)
                  .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                  .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                  .setPermissions(Manifest.permission.READ_CONTACTS)
                  .check();
      ```
      
      </div>
      </details>
      
    - **SMTP Mail Library** : Javax의 기본 Mail 라이브러리
      <details>
      <summary>예시 접기/펼치기</summary>
      
      ```java
        public GMailSender(String user, String password) {
          this.user = user;
          this.password = password;
          emailCode = createEmailCode();
          Properties props = new Properties();
          props.setProperty("mail.transport.protocol", "smtp");
          props.setProperty("mail.host", mailhost);
          props.put("mail.smtp.auth", "true");
          props.put("mail.smtp.port", "465");
          props.put("mail.smtp.socketFactory.port", "465");
          props.put("mail.smtp.socketFactory.class",
                  "javax.net.ssl.SSLSocketFactory");
          props.put("mail.smtp.socketFactory.fallback", "false");
          props.setProperty("mail.smtp.quitwait", "false");
  
          //구글에서 지원하는 smtp 정보를 받아와 MimeMessage 객체에 전달해준다.
          session = Session.getDefaultInstance(props, this);
      }
      ```
      
      </div>
      </details>

    - **ftp4j-1.6 Library** : Ftp 파일 전송 라이브러리
      <details>
      <summary>예시 접기/펼치기</summary>
      
      ```java
        public void uploadFile(File fileName){
 
          FTPClient client = new FTPClient();
   
          try {
              client.connect(FTP_HOST,21);//ftp 서버와 연결, 호스트와 포트를 기입
              client.login(FTP_USER, FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
              client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
              client.changeDirectory("uploadtest/");//서버에서 넣고 싶은 파일 경로를 기입
   
              client.upload(fileName, new MyTransferListener());//업로드 시작
   
              handler.post(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(getApplicationContext(),"성공",Toast.LENGTH_SHORT).show();
                  }
              });
   
          } catch (Exception e) {
   
              handler.post(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                  }
              });
   
              e.printStackTrace();
              try {
                  client.disconnect(true);
              } catch (Exception e2) {
                  e2.printStackTrace();
              }
          }
      }
      ```
      
      </div>
      </details>

<br />
      
  - #### **2. 사용 api**

    - **Kakao Login API** : 카카오에서 제공하는 카카오 로그인 API
      <details>
      <summary>예시 접기/펼치기</summary>
      
      ```java
        // 카카오톡이 설치되어 있는지 확인하는 메서드 , 카카오에서 제공함. 콜백 객체를 이용합.
        Function2<OAuthToken,Throwable,Unit> callback =new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            // 콜백 메서드 ,
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                Log.e(TAG,"CallBack Method");
                //oAuthToken != null 이라면 로그인 성공
                if(oAuthToken!=null){
                    // 토큰이 전달된다면 로그인이 성공한 것이고 토큰이 전달되지 않으면 로그인 실패한다.
                    updateKakaoLoginUi();

                }else {
                    //로그인 실패
                    Log.e(TAG, "invoke: login fail" );
                }

                return null;
            }
        };
      ```
      
      </div>
      </details>

    - **Kakao Map API** : 카카오에서 제공하는 카카오 지도 API
      <details>
      <summary>예시 접기/펼치기</summary>
      
      ```java
        // 지도 띄우기
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
      ```
      
      </div>
      </details>

    - **BootPay Payment API** : 부트페이에서 제공하는 PG 결제 연동 API
      <details>
      <summary>예시 접기/펼치기</summary>
      
      ```java
        // 결제호출
        BootUser bootUser = new BootUser().setPhone("010-1234-5678");
        BootExtra bootExtra = new BootExtra().setQuotas(new int[] {0,2,3});

        Bootpay.init(getFragmentManager())
                .setApplicationId([ Android SDK용 Application ID ]) // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.) // 결제할 PG 사
                .setMethod(Method.) // 결제수단
                .setContext(this)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
                .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName("맥북프로's 임다") // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호expire_month
                .setPrice(10000) // 결제할 금액
                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    @Override
                    public void onConfirm(@Nullable String message) {

                        if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
                        else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                        Log.d("confirm", message);
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {
                        Log.d("done", message);
                    }
                })
                .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                    @Override
                    public void onReady(@Nullable String message) {
                        Log.d("ready", message);
                    }
                })
                .onCancel(new CancelListener() { // 결제 취소시 호출
                    @Override
                    public void onCancel(@Nullable String message) {

                        Log.d("cancel", message);
                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {
                        Log.d("error", message);
                    }
                })
                .onClose(
                        new CloseListener() { //결제창이 닫힐때 실행되는 부분
                    @Override
                    public void onClose(String message) {
                        Log.d("close", "close");
                    }
                })
                .request();
      ```
      
      </div>
      </details>

<br />

## **📓 데이터베이스**
### ERD 설계
  <details>
  <summary>ERD 접기/펼치기</summary>
    
  <img width="1800" alt="erd" src="https://github.com/60162143/itda-Server/assets/33407087/107ae9fb-7c0d-41ca-9d09-fe5aa1f30197">
    
  </div>
  </details>

[**Itda Rest API 자세히 보기**](https://github.com/60162143/itda-Server)

<br />

## **📮 데이터 크롤링**

[**Itda Data Crawling 자세히 보기**](https://github.com/60162143/itda-data-Crawling)

