package com.chingchan.theClap.ui.login

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.chingchan.theClap.databinding.ActiLoginWebBinding

class LoginWebViewActivity  : AppCompatActivity() {
    private lateinit var binding: ActiLoginWebBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiLoginWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val webViewURL = intent.getStringExtra("url")!!

        with(binding){
            binding.webView.apply {
                webViewClient = WebViewClient()
                // 페이지 컨트롤을 위한 기본적인 함수, 다양한 요청, 알림을 수신하는 기능
                settings.javaScriptEnabled = true // 자바스크립트 허용
            }

            binding.webView.loadUrl(webViewURL)

            // 뒤로 가기 버튼
            btnBack.setOnClickListener(View.OnClickListener {
                finish()
            })
        }

    }
}