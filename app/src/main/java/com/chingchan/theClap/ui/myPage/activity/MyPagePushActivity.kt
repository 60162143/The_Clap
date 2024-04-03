package com.chingchan.theClap.ui.myPage.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.databinding.ActiMypagePushBinding

class MyPagePushActivity : AppCompatActivity() {
    private lateinit var binding: ActiMypagePushBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiMypagePushBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            // 앱 푸시 스위치 리스너
            switchPush.setOnCheckedChangeListener { buttonView, isChecked ->

            }

            // 챌린지 스위치 리스너
            switchChallenge.setOnCheckedChangeListener { buttonView, isChecked ->

            }

            // 댓글 및 친구 스위치 리스너
            switchFriend.setOnCheckedChangeListener { buttonView, isChecked ->

            }

            // 혜택 스위치 리스너
            switchBenefit.setOnCheckedChangeListener { buttonView, isChecked ->

            }
        }
    }
}