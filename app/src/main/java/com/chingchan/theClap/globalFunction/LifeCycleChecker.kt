package com.chingchan.theClap.globalFunction

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.chingchan.theClap.ui.myPage.activity.MyPageLockPasswordActivity
import com.chingchan.theClap.ui.myPage.data.AppLock
import com.chingchan.theClap.ui.myPage.data.AppLockConst

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