package com.chingchan.theClap

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.chingchan.theClap.databinding.ActiMainBinding
import com.chingchan.theClap.globalFunction.LifeCycleChecker
import com.chingchan.theClap.ui.login.activity.LoginActivity
import com.chingchan.theClap.ui.login.data.LoginResult
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.toast.customToast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActiMainBinding
    private var backPressedTime: Long = 0
    private lateinit var splashScreen: SplashScreen
    private lateinit var liftCycleChecker: LifeCycleChecker

    private val navController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment_activity_main)
    }

    // 회원가입 Intent registerForActivityResult
    val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LoginResult.SUCCESS.code) {
            navController.navigate(R.id.navigation_home)    // 홈 화면으로 이동
        }else if (result.resultCode == LoginResult.CANCEL.code) {
            navController.navigate(R.id.navigation_home)    // 홈 화면으로 이동
        } else if (result.resultCode == LoginResult.BACK.code) {
            finish()    // 앱 종료
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        splashScreen = installSplashScreen()
        startSplash()

        binding = ActiMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 비밀번호 화면을 위한 LifeCycleChecker
//        liftCycleChecker = LifeCycleChecker(applicationContext)
//        liftCycleChecker.onCreate()

        NavigationUI.setupWithNavController(binding.navView, navController)

        // 뒤로 가기 버튼 클릭시 콜백 호출
        this.onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    // 뒤로가기 버튼 콜백
    private val backPressCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            // 현재 시간보다 크면 종료
            if(backPressedTime + 2000 > System.currentTimeMillis()){
                finish()    // 액티비티 종료
            }else{
                customToast.showCustomToast("한번 더 뒤로가기 버튼을 누르면 종료됩니다.", this@MainActivity)
            }

            // 현재 시간 담기
            backPressedTime = System.currentTimeMillis()
        }
    }

    // splash의 애니메이션 설정
    private fun startSplash() {
        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
            val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)

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

    fun moveToHome(){
        navController.navigate(R.id.navigation_home)
    }
}