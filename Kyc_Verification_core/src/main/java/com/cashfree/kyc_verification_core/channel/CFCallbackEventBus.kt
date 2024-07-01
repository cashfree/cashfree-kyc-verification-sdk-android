package com.cashfree.subscription.coresdk.channel

import com.cashfree.kyc_verification_core.channel.CFCallbackEvents
import com.cashfree.kyc_verification_core.channel.CFPaymentCallbackEvent
import com.cashfree.kyc_verification_core.models.CFErrorResponse
import com.cashfree.kyc_verification_core.models.CFVerificationResponse
import com.cashfree.pg.base.CFEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService

interface CFVerificationCallback {
    fun onVerificationResponse(response: CFVerificationResponse)
    fun onErrorResponse(error: CFErrorResponse)
}

internal class CFCallbackEventBus private constructor(
    executorService: ExecutorService
) : CFEventBus<CFPaymentCallbackEvent, CFPaymentCallbackEvent>(executorService) {

    private var cfVerificationCallback: CFVerificationCallback? = null

    init {
        subscribe { event: CFPaymentCallbackEvent ->
            triggerCallback(event)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: CFCallbackEventBus? = null

        fun initialize(executorService: ExecutorService) {
            synchronized(this) {
                INSTANCE = CFCallbackEventBus(executorService)
            }
        }

        fun getInstance() = INSTANCE
    }

    fun setCheckoutCallback(callback: CFVerificationCallback) {
        synchronized(CFCallbackEventBus::class) {
            cfVerificationCallback = callback
        }
    }

    private fun triggerCallback(event: CFPaymentCallbackEvent) {
        when (event.eventId) {
            CFCallbackEvents.Verify -> {
                cfVerificationCallback?.let {
                    it.let { callback ->
                        CoroutineScope(Dispatchers.Main).launch {
                            event.response?.let { response ->
                                callback.onVerificationResponse(response)
                            }
                        }
                    }
                }
            }
            CFCallbackEvents.Error -> {
                cfVerificationCallback?.let {
                    it.let { callback ->
                        CoroutineScope(Dispatchers.Main).launch {
                            event.error?.let { error ->
                                callback.onErrorResponse(error)
                            }
                        }
                    }
                }
            }
        }
    }


    override fun transformResponse(inputEvent: CFPaymentCallbackEvent): CFPaymentCallbackEvent =
        inputEvent
}