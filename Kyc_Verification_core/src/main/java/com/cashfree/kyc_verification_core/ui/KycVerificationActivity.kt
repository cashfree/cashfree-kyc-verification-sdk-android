package com.cashfree.kyc_verification_core.ui

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import com.cashfree.kyc_verification_core.R
import com.cashfree.kyc_verification_core.databinding.ActivityKycVerificationBinding

internal class KycVerificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKycVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKycVerificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleLoader(true)
        setWebView()
        loadUrl()
    }

    private fun loadUrl() {
        binding.kycWebView.loadUrl("https://forms-test.cashfree.com/verification/F6m5d0rv5s20")
    }

    private fun handleLoader(isVisible: Boolean) {
        binding.cfLoader.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun setWebView() {
        binding.kycWebView.settings.javaScriptEnabled=true
        setWebViewClient()
    }

    private fun setWebViewClient() {
        binding.kycWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                handleLoader(false)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                return super.shouldInterceptRequest(view, request)
            }
        }
    }


}