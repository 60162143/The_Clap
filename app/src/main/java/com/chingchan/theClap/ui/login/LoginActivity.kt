package com.chingchan.theClap.ui.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.BuildConfig
import com.chingchan.theClap.databinding.ActiLoginBinding
import com.chingchan.theClap.globalData.ErrorCode
import com.chingchan.theClap.ui.login.data.LoginUserResData
import com.chingchan.theClap.globalFunction.ApiObject
import com.chingchan.theClap.ui.login.data.*
import com.chingchan.theClap.ui.toast.customToast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.model.AuthErrorCause
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.NidOAuthLogin
import com.navercorp.nid.oauth.OAuthLoginCallback
import com.navercorp.nid.profile.NidProfileCallback
import com.navercorp.nid.profile.data.NidProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActiLoginBinding
    private lateinit var getResultActivity: ActivityResultLauncher<Intent>
    private lateinit var membershipUserData: MembershipUserData
    private var isReady = false
    private var backPressedTime: Long = 0

    private val googleSignInClient: GoogleSignInClient by lazy { getGoogleClient() }

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
                Log.e("Google Login Error", e.stackTraceToString())
            }
        }else{
            customToast.showCustomToast("로그인에 실패했습니다.", this@LoginActivity)
            Log.e("google result Error", "${result.resultCode}")
        }
    }

    // 회원가입 Intent registerForActivityResult
    val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LoginResult.SUCCESS.code) {
            Log.d("result", "회원 가입 성공!!")

        }else if (result.resultCode == LoginResult.CANCEL.code) {
            Log.d("result", "처음부터 시작!!")
        }else if (result.resultCode == LoginResult.BACK.code) {

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        thread(start = true) {
//            for (i in 1..3) {
//                Thread.sleep(1000)
//                Log.d("SleepLog", "Sleep .. i = $i")
//            }
//            // remove Splash Screen after 3 sec
//            isReady = true
//        }
//
//        // Android 12 미만 버전에서 바로 IntroActivity의 UI가 뜨도록.
//        val content: View = findViewById(android.R.id.content)
//        // SplashScreen이 생성되고 그려질 때 계속해서 호출된다.
//        content.viewTreeObserver.addOnPreDrawListener(
//            object : ViewTreeObserver.OnPreDrawListener {
//                override fun onPreDraw(): Boolean {
//                    // Check if the initial data is ready.
//                    return if (isReady) {
//                        // 3초 후 Splash Screen 제거
//                        content.viewTreeObserver.removeOnPreDrawListener(this)
//                        true
//                    } else {
//                        // The content is not ready
//                        false
//                    }
//                }
//            }
//        )

        binding = ActiLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // 카카오, 네이버 SDK 초기화
        KakaoSdk.init(this, BuildConfig.KAKAO_LOGIN_API_KEY)
        NaverIdLoginSDK.initialize(this, BuildConfig.NAVER_LOGIN_CLIENT_ID, BuildConfig.NAVER_LOGIN_CLIENT_SECRET, BuildConfig.NAVER_LOGIN_CLIENT_NAME)

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)

        with(binding){

            // 기기 size 구하기 위한 변수
            val deviceHeight = getScreenHeight(this@LoginActivity)

            // 로고 레이아웃 height 기기 size의 반으로 조정
            val loginLogoLayoutParams = loginLogoLayout.layoutParams
            loginLogoLayoutParams.height = (deviceHeight / 2)
            loginLogoLayout.layoutParams = loginLogoLayoutParams

            // 소셜 로그인 레이아웃 height 기기 size의 반으로 조정
            val loginSocialLayoutParams = loginSocialLayout.layoutParams
            loginSocialLayoutParams.height = (deviceHeight / 2)
            loginSocialLayout.layoutParams = loginSocialLayoutParams

            // '우선 둘러볼게요' 버튼 클릭 리스너
            nonLoginBtn.setOnClickListener(View.OnClickListener {
                // 메인 Activity로 화면 전환
                moveToMainActivity(LoginResult.CANCEL.code)
            })

            // 이용약관 버튼 클릭 리스너
            loginAgreementBtn.setOnClickListener(View.OnClickListener {
                // 이용약관 WebView Activity로 화면 전환
                val intent = Intent(this@LoginActivity, LoginWebViewActivity::class.java)
                intent.putExtra("url", BuildConfig.LOGIN_AGREEMENT_URL)
                startActivity(intent)
            })

            // 개인정보 처리방침 버튼 클릭 리스너
            loginPolicyBtn.setOnClickListener(View.OnClickListener {
                // 이용약관 WebView Activity로 화면 전환
                val intent = Intent(this@LoginActivity, LoginWebViewActivity::class.java)
                intent.putExtra("url", BuildConfig.LOGIN_POLICY_URL)
                startActivity(intent)
            })


            // 카카오 로그인 버튼 클릭 리스너
            loginKakaoBtn.setOnClickListener {
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
                                profile_image = "",
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
            }


            // 네이버 로그인 버튼 클릭
            loginNaverBtn.setOnClickListener {
                var naverToken :String? = ""

                val profileCallback = object : NidProfileCallback<NidProfileResponse> {
                    override fun onSuccess(response: NidProfileResponse) {

                        // 회원 가입 정보
                        membershipUserData = MembershipUserData(
                            email = response.profile?.email ?: "",
                            phone = response.profile?.mobile ?: "",
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
//                var naverRefreshToken = NaverIdLoginSDK.getRefreshToken()
//                var naverExpiresAt = NaverIdLoginSDK.getExpiresAt().toString()
//                var naverTokenType = NaverIdLoginSDK.getTokenType()
//                var naverState = NaverIdLoginSDK.getState().toString()

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
            }

            // 구글 로그인 버튼 클릭 리스너
            loginGoogleBtn.setOnClickListener {
                val account = GoogleSignIn.getLastSignedInAccount(this@LoginActivity)

                val signInIntent = googleSignInClient.signInIntent
                googleAuthLauncher.launch(signInIntent)
            }
        }
    }

    @Suppress("DEPRECATION")
    // 'getDefaultDisplay()' was deprecated in API level 30.
    // 'getRealMetrics()' was deprecated in API level 31.
    fun getScreenWidth(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.width() - insets.left - insets.right
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            displayMetrics.widthPixels
        }
    }

    // 상태바와 네비게이션바를 제외한 기기 높이
    @Suppress("DEPRECATION")
    // 'getDefaultDisplay()' was deprecated in API level 30.
    // 'getRealMetrics()' was deprecated in API level 31.
    fun getScreenHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            windowMetrics.bounds.height() - insets.bottom - insets.top
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            displayMetrics.heightPixels - getStatusBarSize(context) - getNavigationBarSize(context)
        }
    }

    // 상단 status 바 height
    fun getStatusBarSize(context: Context): Int {
        var statusBarHeight = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) statusBarHeight = context.resources.getDimensionPixelSize(resourceId)

        return statusBarHeight
    }

    // 하단 네비게이션 바 height
    fun getNavigationBarSize(context: Context): Int {
        var navigationBarHeight = 0
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) navigationBarHeight = context.resources.getDimensionPixelSize(resourceId)

        return navigationBarHeight
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 현재 시간보다 크면 종료
            if(backPressedTime + 2000 > System.currentTimeMillis()){
                setResult(LoginResult.BACK.code, intent)
                finish()    // 액티비티 종료
            }else{
                customToast.showCustomToast("한번 더 뒤로가기 버튼을 누르면 종료됩니다.", this@LoginActivity)
            }

            // 현재 시간 담기
            backPressedTime = System.currentTimeMillis()
        }
    }

    // 구글 로그인 SDK 초기화
    private fun getGoogleClient(): GoogleSignInClient {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(BuildConfig.GOOGLE_LOGIN_CLIENT_ID)
//            .requestServerAuthCode(BuildConfig.GOOGLE_LOGIN_CLIENT_ID) // client id 를 이용해 server authcode를 요청한다.
            .requestEmail() // 이메일도 요청할 수 있다.
            .build()

        return GoogleSignIn.getClient(this, googleSignInOption)
    }

    // 메인 화면으로 이동
    private fun moveToMainActivity(resultCode: Int) {
        setResult(resultCode, intent)
        finish()
    }

    // 로그인 체크
    // 로그인 확인 시 메인화면으로 이동
    // 로그인 정보 없을 시 회원가입화면으로 이동
    private fun loginCheck(userData: MembershipUserData) {
        val apiObjectCall = ApiObject.getLoginService.getLoginCheck(LoginUserReqData(userData.socialId, userData.loginType))

        apiObjectCall.enqueue(object: Callback<LoginUserResData> {
            override fun onResponse(call: Call<LoginUserResData>, response: Response<LoginUserResData>) {
//                // 추후 성공 시 여기 코드로 리팩토링
                if(response.isSuccessful) {
                    val loginUserData = response.body()?.data

                    UserData.setUserId(applicationContext, loginUserData?.userId!!)
                    UserData.setAccessToken(applicationContext, loginUserData.accessToken)
                    UserData.setUserName(applicationContext, loginUserData.name)
                    UserData.setLoginType(applicationContext, userData.loginType)

                    customToast.showCustomToast("로그인에 성공했습니다.", this@LoginActivity)

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
}