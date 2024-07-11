package com.cashfree.kyc_verification_core.services

import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.cashfree.kyc_verification_core.channel.CFCallbackEventBus
import com.cashfree.kyc_verification_core.channel.CFVerificationCallback
import com.cashfree.kyc_verification_core.ui.KycVerificationActivity
import com.cashfree.kyc_verification_core.utils.Constants
import com.cashfree.pg.base.exception.CFException
import java.lang.ref.WeakReference
import java.util.concurrent.Executors


internal interface IVerificationService {
    fun doVerification()
    fun setCheckoutCallback(cfVerificationCallback: CFVerificationCallback)
}


class CFKycVerificationService private constructor(
    private val context: Context,
    private val url: String
) : IVerificationService {

    private var contextRef: WeakReference<Context>? = null

    init {
        CFCallbackEventBus.initialize(Executors.newSingleThreadExecutor())
        this.contextRef = WeakReference(context)
    }

    @Throws(CFException::class)
    override fun doVerification() {
        this.contextRef = WeakReference<Context>(context)
        startVerification()
    }

    override fun setCheckoutCallback(cfVerificationCallback: CFVerificationCallback) {
        CFCallbackEventBus.getInstance()?.setCheckoutCallback(cfVerificationCallback)
    }

    private fun startVerification() {
        contextRef.let {
            it?.get()?.let { context ->
                Intent(context, KycVerificationActivity::class.java).apply {
                    putExtras(getVerificationBundle())
                    context.startActivity(this)
                }
            }
        }
    }

    private fun getVerificationBundle(): Bundle {
        return Bundle().apply {
            putString(Constants.FORM_URL, url)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CFKycVerificationService? = null

        @Throws(CFException::class)
        fun initialize(context: Context, url: String): CFKycVerificationService {
            if (context is Service || context is Application) {
                throw CFException("Calling context must be activity or fragment")
            }

            if (url.isEmpty()) {
                throw CFException("URL must be provided")
            }

            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CFKycVerificationService(context, url).also { INSTANCE = it }
            }
        }

        @Throws(CFException::class)
        fun getInstance(): CFKycVerificationService {
            return INSTANCE ?: throw CFException("CFKycVerificationService is not initialized")
        }
    }

    class Builder {
        private lateinit var context: Context
        private var url: String = ""

        fun setContext(context: Context) = apply { this.context = context }
        fun setUrl(url: String) = apply { this.url = url }

        @Throws(IllegalArgumentException::class, CFException::class)
        fun build(): CFKycVerificationService {
            if (!::context.isInitialized) {
                throw IllegalArgumentException("Context must be provided")
            }
            if (url.isEmpty()) {
                throw IllegalArgumentException("URL must be provided")
            }
            return initialize(context, url)
        }
    }


}