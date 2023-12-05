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
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.chingchan.theClap.databinding.ActiMainBinding
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.login.LoginActivity
import com.chingchan.theClap.ui.login.data.LoginResult
import com.chingchan.theClap.ui.login.data.MembershipUserData
import com.chingchan.theClap.ui.login.data.UserData
import com.chingchan.theClap.ui.toast.customToast

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActiMainBinding
    private var backPressedTime: Long = 0
    private lateinit var splashScreen: SplashScreen

    // 회원가입 Intent registerForActivityResult
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == LoginResult.SUCCESS.code) {

        }else if (result.resultCode == LoginResult.CANCEL.code) {   // '우선 둘러볼께요' 버튼 클릭시

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

        NavigationUI.setupWithNavController(binding.navView, findNavController(R.id.nav_host_fragment_activity_main))

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

                if(UserData.getLoginType(applicationContext).isEmpty() and (UserData.getUserId(applicationContext) == 0)){
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
}